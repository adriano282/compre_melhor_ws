package com.compremelhor.ws.resource;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import com.compremelhor.model.entity.EntityModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

public abstract class TestResource<T extends EntityModel> {
	private Class<T> clazz;
	private String root;
	protected static Client client = ClientBuilder.newClient();;
	protected String APPLICATION_URL = "http://localhost:8080/compre_melhor_ws/rest/";
	protected static Logger logger;
	
	protected static String currentResource = "";
	protected static int currentId;
	
	
	public TestResource(Class<T> clazz, String root) {
		this.clazz = clazz;
		this.APPLICATION_URL = APPLICATION_URL.concat(root);
		this.root = root;
		logger = Logger.getGlobal();
		
	}
	
	public void getResource() {
		String json = client.target(currentResource).request().get(String.class);
		
		T t = getEntityFromJson(json);
		currentId = t.getId();
		Assert.assertNotEquals(0, currentId);
		logger.log(Level.INFO, "GET /" +root + currentId);
	}
	
	public void updateResorce(String json) {
		Response response = 
				client.target(currentResource).request().put(Entity.json(json));
		Assert.assertEquals(200, response.getStatus());
		logger.log(Level.INFO, "PUT /" + root + currentId + "\nBODY: " + json);
	}
	
	public void deleteResource(String resourceURI) {
		Response response = 
				ClientBuilder.newClient().target(resourceURI).request().delete();
		Assert.assertEquals(200, response.getStatus());
	}
	
	public String createResource(String json) {
		logger.log(Level.WARNING, APPLICATION_URL);
		Response response = ClientBuilder.newClient().target(APPLICATION_URL)
			.request()
			.post(Entity.json(json));
		
		Assert.assertEquals(201, response.getStatus());
		logger.log(Level.INFO, "POST /" + root + "\nBODY: " + json);
		return response.getLocation().toString();
	}
	
	protected T getEntityFromJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		T t = null; 
		
		try {
			t = mapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
}
