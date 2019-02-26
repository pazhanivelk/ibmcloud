/*
 * Copyright 2017 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.example.watson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.example.db.DBUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.DialogNodeAction;
import com.ibm.watson.developer_cloud.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.developer_cloud.assistant.v2.model.DialogRuntimeResponseGeneric;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContext;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContextGlobal;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContextGlobalSystem;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContextSkill;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContextSkills;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInputOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageResponse;
import com.ibm.watson.developer_cloud.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

/**
 * Example of how to call the Assisant message method synchronously,
 * asynchronously, and using react.
 *
 * @version v2-experimental
 */
public class WatsonHelper {

	IamOptions iamOptions = null;
	Assistant service = null;
	CreateSessionOptions sessionOptions = null;
	SessionResponse sresponse = null;
	MessageContext context = null;
	
	
	
	private String ASSISTANT_ID;
	private String url ;
	private String apiKey;
	
	private final List<String> dbfields;  
	
	public WatsonHelper(String aSSISTANT_ID, String url, String apiKey) {
		super();
		ASSISTANT_ID = aSSISTANT_ID;
		this.url = url;
		this.apiKey = apiKey;
		dbfields = Arrays.asList("order_id","product_id","quantity","order_status","delivery_status","payment_status","delivery_date","email_addr","phonenumber","address","payment_type","card_number","cvv","bank_name","card_type","product_name", "amount");
		
	}

	String sessionId = "";

	public Map<String, Object> message(JsonObject inputMsg) throws Exception {

		String inputStr = inputMsg.get("input").getAsString();
		if (service == null) {
			
			System.out.println("Service is nulll *********************************");
			init();
		}
		context = getMessageContext(inputMsg);
		CreateSessionOptions soptions = new CreateSessionOptions.Builder(ASSISTANT_ID)
				.build();
		
		if (inputMsg.get("sessionId") == null || StringUtils.isBlank(inputMsg.get("sessionId").getAsString())) {
			sresponse = service.createSession(soptions).execute();
			sessionId = sresponse.getSessionId();
		}
		else {
			sessionId = inputMsg.get("sessionId").getAsString();
		}
		MessageInputOptions inputOptions = new MessageInputOptions();
	    inputOptions.setReturnContext(true);
		MessageInput.Builder builder = new MessageInput.Builder().messageType("text").text(inputStr);
		MessageInput input = builder.options(inputOptions).build();
				

		MessageOptions.Builder messageOptionsBuilder = new MessageOptions.Builder();
		
		messageOptionsBuilder = messageOptionsBuilder.assistantId(ASSISTANT_ID);
		messageOptionsBuilder = messageOptionsBuilder.sessionId(sessionId);
		messageOptionsBuilder = messageOptionsBuilder.context(context);
		MessageOptions options = messageOptionsBuilder.input(input).build();
		// sync
		MessageResponse response = service.message(options).execute();
		context = response.getContext();
		return processResponse(response);
	}
	
	private Map<String, Object> processResponse(MessageResponse response) throws Exception{
		Map<String,Object> responseMap = new HashMap<>();
		String returnString = "" ;
        responseMap.put("messageContext", response.getContext());
		for (DialogRuntimeResponseGeneric generic : response.getOutput().getGeneric()) {
			if (generic.getResponseType().equals("option")) {
				returnString = returnString + generic.getTitle()  +"\n";
				List<DialogNodeOutputOptionsElement > outputOptions = generic.getOptions();
				int i = 1;
				for(DialogNodeOutputOptionsElement element:outputOptions) {
					returnString = returnString + "\t" + i + ":" + element.getValue().getInput().text() +"\n";
					i=i+1;
					
				}

			}
			if (generic.getResponseType().equals("text")){
				returnString = generic.getText();      
			}
			
		}
		responseMap.put("response", returnString);
		if (response.getOutput().getActions() == null || response.getOutput().getActions().isEmpty() ) {
				return responseMap;
		}
		DBUtils dbUtils = new DBUtils();
		DialogNodeAction action = response.getOutput().getActions().get(0);
		String actionName = action.getName();
		MessageContext context = response.getContext();
		Map mainSkills  = (Map)context.getSkills().get("main skill");
		Map<String, Object> userDefinedData = (Map<String, Object>)mainSkills.get("user_defined");
		Map<String, String > orderData = userDefinedData.entrySet().stream().
				filter(e -> dbfields.contains(e.getKey()) && e.getValue() != null).
				collect(Collectors.toMap(Map.Entry :: getKey, v ->  v.getValue().toString() ));
		String responseStr = null;
		if (responseMap.get("response") != null) {
			responseStr = responseMap.get("response").toString();
			responseMap.put("response",response);
		}
		switch(actionName) {
			
			case  "cancelOrder":
				orderData.put("order_status", "CANCELLED");
				userDefinedData.put("finalstatus", "success");
				if (responseStr != null) {
					responseStr.replace("$finalstatus", "success" );
				}
				dbUtils.updateOrderData(orderData);
				break;
			case "returnOrder":
				orderData.put("order_status", "RETURNED");
				userDefinedData.put("finalstatus", "success");
				if (responseStr != null) {
					responseStr.replace("$finalstatus", "success" );
				}
				dbUtils.updateOrderData(orderData);
				break;
			case "saveOrder":
				String orderId = dbUtils.getNextOrdeID();
				orderData.put("order_status", "FULFILLMENT");
				userDefinedData.put("order_id", orderId);
				orderData.put("order_id", orderId);
				if (responseStr != null) {
					responseStr=responseStr.replace("order_id", orderId );
				}
				dbUtils.insertOrderData(orderData);
				break;
			case "updatePayment":
				orderData.put("payment_status", "PAID");
				userDefinedData.put("$finalstatus", "success");
				dbUtils.updateOrderData(orderData);
				break;
			case "getOrderStatus":
				String orderStatus = dbUtils.getNextOrderStatus(orderData.get("order_id"));
				userDefinedData.put("$finalstatus", orderStatus);
				if (responseStr != null) {
					responseStr.replace("$finalstatus", "orderStatus" );
				}
				break;
		}
		
		return responseMap;
		
	}

	public MessageContext getMessageContext(JsonObject inputArgs) {
		if (inputArgs.has("messageContext")) {
			Gson gson = new Gson();
			context = gson.fromJson(inputArgs.get("messageContext"), MessageContext.class);
			return context;
		}
		return null;
		/*
		JsonObject messageContext = null;
		
		
		String userId = "my_user_id";
		if (inputArgs.has("messageContext")) {
			messageContext = inputArgs.get("messageContext").getAsJsonObject();
		}
		if (messageContext == null) {
			messageContext = new JsonObject();
			JsonObject globalObj = new JsonObject();
			JsonObject systemObj = new JsonObject();
			systemObj.addProperty("user_ud", "my_user_id");
			systemObj.addProperty("turn_count", 0);
			globalObj.add("system", systemObj);
	
		}
		
		MessageContext context = new MessageContext();
		
	    MessageContextGlobalSystem system = new MessageContextGlobalSystem();
	    if (messageContext.has("global")) {
		    JsonObject systemObj = messageContext.get("system").getAsJsonObject();
		    if (systemObj.has("user_id")){
		    	system.setUserId(systemObj.get("user_id").getAsString());
	    	}
		    system.setTurnCount(systemObj.get("turn_count").getAsLong());
		    MessageContextGlobal globalContext = new MessageContextGlobal();
		    globalContext.setSystem(system);
		    context.setGlobal(globalContext);
	    }
	    
	    if (messageContext.has("skills")) {
	    	
	    	MessageContextSkills skills = new MessageContextSkills();
	    	context.setSkills(skills);
	    	
	    	JsonObject skillsJson = messageContext.get("skills").getAsJsonObject();
	    	if (skillsJson.has("main skill")){
		    	MessageContextSkill skill = new MessageContextSkill();
		    	
		    	skills.put("main skill", skill);
		    	JsonObject mainSkillObj = skillsJson.get("main skill").getAsJsonObject();
		    	if (mainSkillObj.has("user_defined")) {
		    		Map<String, String> userDefinedContext = new HashMap<>();
		    		JsonObject userDefinedSkill
			        userDefinedContext.put("account_num","123456");	
		    	}
		    	
	    	}

	    	
	    	
	    	
	    }
	    
	    

	    

	    
	    
	    return context;
	*/}

	public void init() {
		iamOptions = new IamOptions.Builder().apiKey(apiKey).build();
		service = new Assistant("2018-02-16", iamOptions);
		service.setEndPoint(url);

	}
	
	public static WatsonHelper getInstance(String assId, String url, String apiKey) {
		WatsonHelper helper = new WatsonHelper(assId, url, apiKey);
		helper.init();
		return helper;
	}
	
	public String getSessionId() {
		return sessionId;
	}

}