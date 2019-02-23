package com.example;

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
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Unit test for simple function.
 */
public class FunctionAppTest {
  @Test
  @Ignore
  public void testFunction() {

	
	Gson gson = new Gson();
    JsonObject args = new JsonObject();
    args.addProperty("input", "Want to make order");
    args.addProperty("assistantId", "2131796c-7457-4f05-b07a-d25424297e66");
    args.addProperty("url", "https://gateway.watsonplatform.net/assistant/api/");
    args.addProperty("apikey", "sm2Trx14ybklxGiW-xxH86-EnPEUhGWJxqvjTRUpSjB4");

    JsonObject response = FunctionApp.main(args);
    
    System.out.println("response from watson "+gson.toJson(response));
    args.add("messageContext", response.get("context"));
    args.addProperty("input", "Bricks");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    args.addProperty("input", 10);
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    args.addProperty("input", "23 main street,chennai - 600010");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    args.addProperty("input", "4");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    
    args.addProperty("input", "4343432434");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    
    args.addProperty("input", "Yes");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    
    args.addProperty("input", "Credit Card");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    
    args.addProperty("input", "8887777777712345");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    args.addProperty("input", "434");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    
    args.addProperty("input", "434444");
    args.addProperty("sessionId", response.get("sessionId").getAsString());
    response = FunctionApp.main(args);
    System.out.println("response from watson "+gson.toJson(response));
    assertNotNull(response);

    

  }
  
  
  @Test
  @Ignore
  public void testReturn() {


		
		Gson gson = new Gson();
	    JsonObject args = new JsonObject();
	    args.addProperty("input", "Want to return item");
	    args.addProperty("assistantId", "2131796c-7457-4f05-b07a-d25424297e66");
	    args.addProperty("url", "https://gateway.watsonplatform.net/assistant/api/");
	    args.addProperty("apikey", "sm2Trx14ybklxGiW-xxH86-EnPEUhGWJxqvjTRUpSjB4");

	    JsonObject response = FunctionApp.main(args);
	    
	    System.out.println("response from watson "+gson.toJson(response));
	    args.add("messageContext", response.get("context"));
	    args.addProperty("input", "103aaaa");
	    args.addProperty("sessionId", response.get("sessionId").getAsString());
	    
	    response = FunctionApp.main(args);
	    System.out.println("response from watson "+gson.toJson(response));
	    args.addProperty("input", "Brics");
	    args.addProperty("sessionId", response.get("sessionId").getAsString());
	    response = FunctionApp.main(args);
	    System.out.println("response from watson "+gson.toJson(response));
	    args.addProperty("input", 10);
	    args.addProperty("sessionId", response.get("sessionId").getAsString());
	    response = FunctionApp.main(args);
	    System.out.println("response from watson "+gson.toJson(response));
	    args.addProperty("input", "test@test.com");
	    args.addProperty("sessionId", response.get("sessionId").getAsString());
	    response = FunctionApp.main(args);
	    System.out.println("response from watson "+gson.toJson(response));
	    
	    args.addProperty("input", "Yes");
	    args.addProperty("sessionId", response.get("sessionId").getAsString());
	    response = FunctionApp.main(args);
	    System.out.println("response from watson "+gson.toJson(response));
	    
  }
}
