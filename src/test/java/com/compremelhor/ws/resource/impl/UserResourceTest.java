package com.compremelhor.ws.resource.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Address;
import com.compremelhor.model.entity.User;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class UserResourceTest extends TestResource<User>{
	public static Address address;
	public static User user;
	
	public UserResourceTest() { super(User.class, "users"); }

	@Test
	@Order(order = 1)
	public void shouldCreateUser() { createUser(); }

	@Test
	@Order(order = 2)
	public void shouldUpdateUser() {		
		user.setUsername("Adriano de Jesus do Nascimento");
		user.setDocument("427618444");
		user.setDocumentType(User.DocumentType.CPF);
		
		updateResorce(myGson.toJson(user, User.class));
		user = getResource();
	}
	
	@Test
	@Order(order = 3)
	public void shouldAddAnAddress() {
		addAddress();
	}
	
	@Test
	@Order(order = 4)
	public void shouldDeleteAddress() {
		deleteAddress();
	}
	
	@Test
	@Order(order = 5)
	public void shouldDeleteUser() {
		deleteUser();
	}
	
	public void deleteUser() {
		currentResource = APPLICATION_ROOT.concat("users/").concat(String.valueOf(user.getId()));
		deleteResource(currentResource);
	}
	
	public void addAddress() {
		address = new Address();
		address.setStreet("Outro test");
		address.setNumber("49");
		address.setZipcode("08738290");
		address.setCity("Mogi das Cruzes");
		address.setState("Sao Paulo");
		address.setQuarter("Vila Brasileira");
		address.setUser(user);
		
		String uri = APPLICATION_ROOT.concat("addresses");
		address = getResource(createResource(myGson.toJson(address, Address.class), uri), Address.class);
	}
	
	public void deleteAddress() {
		String uri = APPLICATION_ROOT.concat("addresses/").concat(String.valueOf(address.getId()));
		deleteResource(uri);
	}
	
	public void createUser() {
		user = new User();
		user.setUsername("Adriano de Jesus");
		user.setDocument("427610576");
		user.setDocumentType(User.DocumentType.CPF);
		user.setPasswordHash("teste123");
		
		// Create the user
		createResource(myGson.toJson(user, User.class));
		user = getResource();
		
		// Verify if has been created
		Assert.assertNotNull(user);
		
		// Verify data persisted
		Assert.assertEquals("Adriano de Jesus", user.getUsername());
		Assert.assertEquals("427610576", user.getDocument());		
		Assert.assertEquals(User.DocumentType.CPF, user.getDocumentType());
	}
}
