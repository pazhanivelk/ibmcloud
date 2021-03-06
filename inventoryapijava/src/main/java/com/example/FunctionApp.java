package com.example;

import java.util.Map;

import com.example.watson.WatsonHelper;
import com.google.gson.Gson;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageResponse;

/**
 * Hello FunctionApp use below command to create cloud functions *
 * ibmcloud fn action create inventoryapi target/inventoryapi.jar --main  com.example.FunctionApp
 * use below command to update it *
 * ibmcloud fn action update inventoryapi target/inventoryapi.jar --main com.example.FunctionApp
 */
public class FunctionApp {
	public static JsonObject main(JsonObject args) {

		// System.out.println(args.getAsString());
		
		args.addProperty("assistantId", "2131796c-7457-4f05-b07a-d25424297e66");
		args.addProperty("url", "https://gateway.watsonplatform.net/assistant/api/");
		args.addProperty("apikey", "sm2Trx14ybklxGiW-xxH86-EnPEUhGWJxqvjTRUpSjB4");
		WatsonHelper helper = WatsonHelper.getInstance(args.get("assistantId").getAsString(), args.get("url").getAsString(),
				args.get("apikey").getAsString());
		JsonObject result = new JsonObject();

		try {
			Gson gson = new Gson();
			System.out.println("############### args" + args);
			Map<String, Object> responseMsg = helper.message(args);
			
			String responseStr = gson.toJson(responseMsg);
			JsonObject responseJson = new JsonParser().parse(responseStr).getAsJsonObject();
			responseJson.addProperty("sessionId", helper.getSessionId());
			return responseJson;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
