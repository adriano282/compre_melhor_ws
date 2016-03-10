package com.compremelhor.ws.rest;

import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

public class RestServiceTestApplication {
	private static final String APPLICATION_URL = 
			"http://localhost:8080/compre_melhor_ws/rest/";
	
	private WebTarget userResource;
	
	public static void main(String...args) {
		new RestServiceTestApplication().runSample();
	}
	
	public RestServiceTestApplication() {
		Client restClient = ClientBuilder.newClient();
		
		userResource = restClient.target(APPLICATION_URL + "user");
	}
	
	public void runSample() {
		String document = "427610576";
		String username = "adriano";
		String senha = "123345";
		
		try {
			userResource.path(username)
				.path(document)
				.path("CPF")
				.path(senha)
				.request().post(Entity.json(""), String.class);
		} catch (WebApplicationException e) {
			Response response = e.getResponse();
			StatusType statusInfo = response.getStatusInfo();
			System.out.println(statusInfo);
			System.out.println(response
							.readEntity(JsonObject.class)
							.getString("entity"));
		}
		 
	}
}
