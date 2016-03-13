package com.compremelhor.ws.resource;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@RunWith(OrderedRunner.class)
public class ManufacturerResourceTest {

	private Client client;
	private static final String APPLICATION_URL = 
			"http://localhost:8080/compre_melhor_ws/rest/manufacturers/";
	private static Logger logger;
	
	private static String currentResource = "";
	private static int currentId;
	
	@Before
	public void setup() {
		logger = Logger.getGlobal();
		client = ClientBuilder.newClient();
	}
		
	@Test
	@Order(order = 1)
	public void testCreateManufacturer() {
		currentResource = createManufacturer();
	}
	
	@Test
	@Order(order = 2)
	public void testGetManufacturer() {
		Assert.assertNotNull(client);		
		String json = client.target(currentResource).request().get(String.class);
		Manufacturer m = getManufacturerFromJson(json);
		currentId = m.getId();
		Assert.assertNotEquals(0, currentId);
		logger.log(Level.INFO, "GET /manufacturers/" + currentId);
	}
	
	@Test
	@Order(order = 3)
	public void testUpdateManufacturer() {
		String json = "{\"name\":\"Name Changed\"}";
		Response response = 
				client.target(currentResource).request().put(Entity.json(json));
		Assert.assertEquals(200, response.getStatus());
		logger.log(Level.INFO, "PUT /manufacturers/" + currentId + "\nBODY: " + json);
	}
	
	@Test
	@Order(order = 4)
	public void testDeleteManufacturer() {
		deleteManufacturer(currentResource);
		logger.log(Level.INFO, "DELETE /manufacturers/" + currentId);
	}
	
	public void deleteManufacturer(String resourceURI) {
		Response response = 
				ClientBuilder.newClient().target(resourceURI).request().delete();
		Assert.assertEquals(200, response.getStatus());
	}

	public String createManufacturer() {
		String json = "{ \"name\" : \"FABRICANTE TESTE\" }";		
		Response response = ClientBuilder.newClient().target(APPLICATION_URL)
			.request()
			.post(Entity.json(json));
		
		Assert.assertEquals(201, response.getStatus());
		logger.log(Level.INFO, "POST /manufactures/\nBODY: " + json);
		return response.getLocation().toString();
	}
	
	private Manufacturer getManufacturerFromJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		mapper.findAndRegisterModules();
		Manufacturer m = null; 
		
		try {
			m = mapper.readValue(json, Manufacturer.class);
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
		return m;
	}
}
