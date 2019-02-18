package com.example;

import java.util.List;
import java.util.Map;

import com.example.db.DBUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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

/**
 * Hello FunctionApp use below command to create cloud functions
 * ibmcloud fn action create databaseapi target/demo-function.jar --main com.example.FunctionApp
 * use below command to update it
 * ibmcloud fn action update databaseapi target/demo-function.jar --main com.example.FunctionApp
 */
public class FunctionApp {
	public static JsonObject main(JsonObject args) {
		JsonArray array = null;
		try {
			String sql = generateSQL(args);

			List<Map<String, String>> data = DBUtils.executeQuery(sql);
			System.out.println( "data **********"+data );
			array = listToJSONArray(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("results", array);
		return jsonObject;
	}

	private static JsonArray listToJSONArray(List<Map<String, String>> data) {
		JsonArray jsonArray = new JsonArray();
		for (Map<String, String > map : data) {
			Gson gson = new Gson();
			jsonArray.add(gson.toJsonTree(map).getAsJsonObject());
		}
		return jsonArray;
	}
	

	public static String generateSQL(JsonObject inputJson) {

		String returnVal = "";
		String columns = inputJson.get("fields").getAsString();
		String tableName = inputJson.get("tableName").getAsString();
		JsonElement whereConditionsObj = inputJson.get("wherecondition");
		returnVal = String.format("select %s from %s ",  columns, tableName);
		if (whereConditionsObj != null) {
			returnVal = returnVal + " where " + whereConditionsObj.getAsString();
		}
		return returnVal;

	}

}
