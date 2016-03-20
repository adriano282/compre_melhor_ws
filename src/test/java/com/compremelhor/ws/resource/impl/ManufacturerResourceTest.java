package com.compremelhor.ws.resource.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class ManufacturerResourceTest extends TestResource<Manufacturer>{

	public static Manufacturer manufacturer;
	
	public ManufacturerResourceTest() {
		super(Manufacturer.class, "manufacturers/");
	}
		
	@Test
	@Order(order = 1)
	public void testCreateManufacturer() {
		createManufacturer();
	}
	
	@Test
	@Order(order = 2)
	public void testGetManufacturer() {
		getResource();
	}
	
	@Test
	@Order(order = 3)
	public void testUpdateManufacturer() {
		String json = "{\"name\":\"Name Changed\"}";
		updateResorce(json);
		manufacturer = getResource();
	}
	
	@Test
	@Order(order = 4)
	public void testDeleteManufacturer() {
		deleteManufacturer(currentResource);
	}
	
	public void deleteManufacturer(String resourceURI) {
		deleteResource(resourceURI);
	}

	public String createManufacturer() {
		manufacturer = new Manufacturer();
		manufacturer.setName("Coca-Cola");
		
		// Create Manufacturer
		createResource(myGson.toJson(manufacturer, Manufacturer.class));
		manufacturer = getResource();
		
		// Verify if Manufacturer was created
		Assert.assertNotNull(manufacturer);
		return currentResource;
	}
	
	public void deleteManufacturer() {
		currentResource = APPLICATION_ROOT.concat("manufacturers/").concat(String.valueOf(manufacturer.getId()));
		deleteResource(currentResource);
		
		// Verify if partner has been deleted
		Assert.assertNull(getResource());
	}
	
}
