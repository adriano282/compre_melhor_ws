package com.compremelhor.ws.resource.impl;

import java.util.logging.Level;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Partner;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class PartnerResourceTest extends TestResource<Partner>{

	public String currentAddress = "";
	
	public PartnerResourceTest() {
		super(Partner.class, "partners/");
	}
	

	@Test
	@Order(order = 1)
	public void testCreatePartner() {
		currentResource = createPartner();
	}
	
	@Test
	@Order(order = 2)
	public void testGetPartner() {
		getResource();
	}
	
	@Test
	@Order(order = 3)
	public void testUpdatePartner() {
		String json = "{\"name\":\"Name Changed\"}";
		updateResorce(json);
	}
	
	@Test
	@Order(order = 4)
	public void testAddAddress() {
		addAddress();
	}
	
	@Test
	@Order(order = 5)
	public void testDeleteAddress() {
		deleteAddress();
		logger.log(Level.INFO, "DELETE " + currentAddress);
	}
	
	@Test
	@Order(order = 6)
	public void testDeletePartner() {
		deletePartner(currentResource);
		logger.log(Level.INFO, "DELETE /partners/" + currentId);
	}
	
	public void deleteAddress() {
		deleteResource(currentAddress);
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
	
	public void deletePartner(String resourceURI) {
		deleteResource(resourceURI);
	}
	
	public String createPartner() {
		String json = "{\"name\":\"Partner test\"}";
		return createResource(json);
	}
	
	
}
