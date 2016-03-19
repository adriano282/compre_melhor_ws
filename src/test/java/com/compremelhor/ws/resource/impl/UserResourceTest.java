package com.compremelhor.ws.resource.impl;

import java.util.logging.Level;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.User;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class UserResourceTest extends TestResource<User>{

	public String currentAddress = "";
	
	public UserResourceTest() {
		super(User.class, "users");
	}

	@Test
	@Order(order = 1)
	public void shouldCreateUser() {
		currentResource = createUser();
	}
	
	@Test
	@Order(order = 2)
	public void shouldGetAnUser() {
		getResource();
	}
	
	@Test
	@Order(order = 3)
	public void shouldUpdateUser() {
		String json = "{\"username\": \"Adriano de Jesus do Nascimento\", "
				+ "\"document\": \"427618444\","
				+ "\"documentType\" : \"CPF\","
				+ "\"password\": \"teste123\","
				+ "\"addresses\" : []}";
		updateResorce(json);
	}
	
	@Test
	@Order(order = 4)
	public void shouldAddAnAddress() {
		addAddress();
	}
	
	@Test
	@Order(order = 5)
	public void shouldDeleteAddress() {
		deleteResource(currentAddress);
		logger.log(Level.INFO, "DELETE " + currentAddress);
	}
	
	@Test
	@Order(order = 6)
	public void shouldDeleteUser() {
		deleteResource(currentResource);
		logger.log(Level.INFO, "DELETE /users/" + currentId);
	}
	public void addAddress() {
		String json = "{  "
				+ "\"street\": \" OUTR TESTE\", "
				+ "\"number\": \"49\", "
				+ "\"zipcode\": \"08738290\", "
				+ "\"quarter\": \"Vila Brasileira\", "
				+ "\"city\": \"Mogi das Cruzes\","
				+ "\"state\": \"Sao Paulo\"}";
		currentAddress = createResource(json, currentResource.concat("/addresses"));
		logger.log(Level.WARNING, currentAddress);
	}

	public String createUser() {
		String json = "{\"username\": \"Adriano de Jesus\", "
				+ "\"document\": \"427610576\","
				+ "\"documentType\" : \"CPF\","
				+ "\"password\": \"teste123\","
				+ "\"addresses\" : []}";
		return createResource(json);
		
	}
}
