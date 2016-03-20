package com.compremelhor.ws.resource.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.compremelhor.model.entity.EntityModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

public abstract class TestResource<T extends EntityModel> {
	private Class<T> clazz;
	public String root;
	private Client client;
	protected String APPLICATION_URL = "http://localhost:8080/compre_melhor_ws/rest/";
	protected String APPLICATION_ROOT = "http://localhost:8080/compre_melhor_ws/rest/";
	protected static Logger logger;
	
	protected static String currentResource = "";
	protected static int currentId;
	
	@Before
	public void openClient() {
		client = ClientBuilder.newClient();
	}
	
	@After	
	public void closeClient() {
		client.close();
	}
	
	public TestResource(Class<T> clazz, String root) {
		this.clazz = clazz;
		this.APPLICATION_URL = APPLICATION_URL.concat(root);
		this.root = root;
		logger = Logger.getGlobal();
		
	}
	
	public T getResource() {
		openClient();

		logger.log(Level.WARNING, currentResource);
		String json = client.target(currentResource).request().get(String.class);
		
		T t = getEntityFromJson(json);
		currentId = t.getId();
		Assert.assertNotEquals(0, currentId);
		logger.log(Level.INFO, "GET /" +root + currentId);
		
		closeClient();
		return t; 
	}
	
	public void updateResorce(String json) {		
		openClient();
		Response response = 
				client.target(currentResource).request().put(Entity.json(json));
		Assert.assertEquals(200, response.getStatus());
		logger.log(Level.INFO, "PUT /" + root + currentId + "\nBODY: " + json);
		closeClient();
	}
	
	public void deleteResource(String resourceURI) {
		openClient();
		Response response = client.target(resourceURI).request().delete();
		logger.log(Level.INFO, "DELETE /" +root + currentId);
		Assert.assertEquals(200, response.getStatus());
		closeClient();
	}
	
	public String createResource(String json) {
		openClient();
		
		Response response = client.target(APPLICATION_URL)
			.request()
			.post(Entity.json(json));
		
		Assert.assertNotNull(response);
		Assert.assertEquals(201, response.getStatus());
		logger.log(Level.INFO, "POST /" + root + "\nBODY: " + json);
		currentResource = response.getLocation().toString();
		
		closeClient();
		return currentResource;
	}
	
	public String createResource(String json, String url) {
		Response response = client.target(url)
			.request()
			.post(Entity.json(json));
		
		Assert.assertEquals(201, response.getStatus());
		logger.log(Level.INFO, "POST /" + url + "\nBODY: " + json);
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
