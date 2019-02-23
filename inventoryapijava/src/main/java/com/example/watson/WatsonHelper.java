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
	
	final String ASSISTANT_ID = "2131796c-7457-4f05-b07a-d25424297e66";
	

	public MessageResponse message(String inputMsg) throws Exception {

		if (service == null) {
			
			System.out.println("Service is nulll *********************************");
			init();
		}
		MessageInputOptions inputOptions = new MessageInputOptions();
	    inputOptions.setReturnContext(true);
		MessageInput.Builder builder = new MessageInput.Builder().messageType("text").text(inputMsg);
		MessageInput input = builder.options(inputOptions).build();
		
		// create global context with user ID
	    MessageContextGlobalSystem system = new MessageContextGlobalSystem();
	    system.setUserId("my_user_id");
	    MessageContextGlobal globalContext = new MessageContextGlobal();
	    globalContext.setSystem(system);

	    // build user-defined context variables, put in skill-specific context for main skill
	    Map<String, String> userDefinedContext = new HashMap<>();
	    userDefinedContext.put("account_num","123456");
	    Map<String, Map> mainSkillContext = new HashMap<>();
	    mainSkillContext.put("user_defined", userDefinedContext);
	    MessageContextSkills skillsContext = new MessageContextSkills();
	    skillsContext.put("main skill", mainSkillContext);

	    MessageContext context = new MessageContext();
	    context.setGlobal(globalContext);
	    context.setSkills(skillsContext);

		

		MessageOptions.Builder messageOptionsBuilder = new MessageOptions.Builder();
		
		messageOptionsBuilder = messageOptionsBuilder.assistantId(ASSISTANT_ID);
		messageOptionsBuilder = messageOptionsBuilder.sessionId(sresponse.getSessionId());
		messageOptionsBuilder = messageOptionsBuilder.context(context);
		MessageOptions options = messageOptionsBuilder.input(input).build();
		// sync
		MessageResponse response = service.message(options).execute();
		context = response.getContext();
		System.out.println("COntext ***from response **************"+ context);

		System.out.println(response);
		return response;

	}

	public void init() {
		iamOptions = new IamOptions.Builder().apiKey("sm2Trx14ybklxGiW-xxH86-EnPEUhGWJxqvjTRUpSjB4").build();
		service = new Assistant("2018-02-16", iamOptions);
		service.setEndPoint("https://gateway.watsonplatform.net/assistant/api");
		CreateSessionOptions soptions = new CreateSessionOptions.Builder(ASSISTANT_ID)
				.build();
		sresponse = service.createSession(soptions).execute();
	}

}