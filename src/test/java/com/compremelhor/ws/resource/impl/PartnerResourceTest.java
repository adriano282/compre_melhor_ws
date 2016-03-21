package com.compremelhor.ws.resource.impl;

import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Address;
import com.compremelhor.model.entity.Partner;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class PartnerResourceTest extends TestResource<Partner>{

	public static Partner partner;
	public static Address address;
	public String currentAddress = "";
	
	public PartnerResourceTest() { super(Partner.class, "partners/"); }
	
	@Test
	@Order(order = 1)
	public void testCreatePartner() {
		currentResource = createPartner();
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
	}
	
	@Test
	@Order(order = 6)
	public void testDeletePartner() {
		deletePartner(currentResource);
	}
	
	public void deleteAddress() {
		String uri = APPLICATION_ROOT.concat("addresses/").concat(String.valueOf(address.getId()));
		deleteResource(uri);
	}
	
	public void addAddress() {
		address = new Address();
		address.setStreet("Outro test");
		address.setNumber("49");
		address.setZipcode("08738290");
		address.setCity("Mogi das Cruzes");
		address.setState("Sao Paulo");
		address.setQuarter("Vila Brasileira");
		address.setPartner(partner);
		
		String uri = APPLICATION_ROOT.concat("addresses");
		address = getResource(createResource(myGson.toJson(address, Address.class), uri), Address.class);
	}
	
	public void deletePartner(String resourceURI) {
		deletePartner();
	}
	
	public void deletePartner() {
		currentResource = APPLICATION_ROOT.concat("partners/").concat(String.valueOf(partner.getId()));
		deleteResource(currentResource);
		
		// Verify if partner has been deleted
		Assert.assertNull(getResource());
	}
	
	public String createPartner() {
		Partner p = new Partner();
		p.setName("Partner test");
		String result = createResource(myGson.toJson(p, Partner.class));
		partner = getResource();
		return result;
	}
	
	
}
