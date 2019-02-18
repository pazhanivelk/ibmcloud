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

import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContext;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;
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
	
	final String ASSISTANT_ID = "660019c9-f2c4-49ef-9416-3f79e9c8aa3f";

	public MessageResponse message(String inputMsg) throws Exception {

		if (service == null) {
			
			System.out.println("Service is nulll *********************************");
			init();
		}
		MessageInput.Builder builder = new MessageInput.Builder().messageType("text").text(inputMsg);
		MessageInput input = builder.build();

		MessageOptions.Builder messageOptionsBuilder = new MessageOptions.Builder();
		
		messageOptionsBuilder = messageOptionsBuilder.assistantId(ASSISTANT_ID);
		messageOptionsBuilder = messageOptionsBuilder.sessionId(sresponse.getSessionId());
		if (context != null) {
			System.out.println("COntext *****************"+ context.toString());
			messageOptionsBuilder = messageOptionsBuilder.context(context);
		}
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