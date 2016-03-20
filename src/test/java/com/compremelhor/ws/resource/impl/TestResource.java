package com.compremelhor.ws.resource.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
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
import com.google.gson.Gson;

public abstract class TestResource<T extends EntityModel> {
	
	protected static Gson myGson = new Gson();
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
		String json;
		try {
			 json = client.target(currentResource).request().get(String.class);
		} catch (WebApplicationException e) {
			return null;
		}		
		T t = getEntityFromJson(json);
		closeClient();
		return t; 
	}
	
	public void updateResorce(String json) {		
		openClient();
		logger.log(Level.INFO, "PUT /" + currentResource + "\nBODY: " + json);
		Response response = 
				client.target(currentResource).request().put(Entity.json(json));
		Assert.assertEquals(200, response.getStatus());		
		closeClient();
	}
	
	public void deleteResource(String resourceURI) {
		openClient();
		logger.log(Level.INFO, "DELETE /" +resourceURI);
		Response response = client.target(resourceURI).request().delete();		
		Assert.assertEquals(200, response.getStatus());
		closeClient();
	}
	
	public String createResource(String json) {
		openClient();
		logger.log(Level.INFO, "POST /" + root + "\nBODY: " + json);
		Response response = client.target(APPLICATION_URL)
			.request()
			.post(Entity.json(json));
		
		Assert.assertNotNull(response);
		Assert.assertEquals(201, response.getStatus());
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
		
		if (json == null) return null;
		
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
