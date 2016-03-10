package com.compremelhor.ws.resource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class TesteREST {
	private static final String APPLICATION_URL =
			"http://localhost:8080/compre_melhor_ws/rest/";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Client restClient = ClientBuilder.newClient();
			WebTarget userResource = 
					restClient.target(APPLICATION_URL + "user");
			
			
		
	}

}
