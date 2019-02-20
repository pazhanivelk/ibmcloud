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

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContext;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContextGlobal;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContextGlobalSystem;
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
	
	final String ASSISTANT_ID = "e4d12fb1-b2b3-45f0-838a-df2cb7ac8d8a";
	String sessionId = "";

	public MessageResponse message(JsonObject inputMsg) throws Exception {

		String inputStr = inputMsg.get("input").getAsString();
		if (service == null) {
			
			System.out.println("Service is nulll *********************************");
			init();
		}
		
		CreateSessionOptions soptions = new CreateSessionOptions.Builder(ASSISTANT_ID)
				.build();
		
		if (inputMsg.get("sessionId") == null) {
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
		System.out.println("COntext ***from response **************"+ context);

		System.out.println(response);
		return response;

	}
	
	public MessageContext getMessageContext(JsonObject inputArgs) {
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
		
		
		
	    MessageContextGlobalSystem system = new MessageContextGlobalSystem();
	    JsonObject systemObj = messageContext.get("system").getAsJsonObject();
	    system.setUserId(systemObj.get("user_id").getAsString());
	    system.setTurnCount(systemObj.get("turn_count").getAsLong());
	    MessageContextGlobal globalContext = new MessageContextGlobal();
	    globalContext.setSystem(system);

	    

	    MessageContext context = new MessageContext();
	    context.setGlobal(globalContext);
	    return context;
	}

	public void init() {
		iamOptions = new IamOptions.Builder().apiKey("hWkKSgKBU3Cz5ObICTgdChNvQIfNBYq2TtXfQdgLp_vh").build();
		service = new Assistant("2018-02-16", iamOptions);
		service.setEndPoint("https://gateway-lon.watsonplatform.net/assistant/api");

	}
	
	public String getSessionId() {
		return sessionId;
	}

}