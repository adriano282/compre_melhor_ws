package com.compremelhor.ws.resource.impl;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class ManufacturerResourceTest extends TestResource<Manufacturer>{

	public String manufacturerResource;
	public String manufacturerId;
	
	public ManufacturerResourceTest() {
		super(Manufacturer.class, "manufacturers/");
	}
		
	@Test
	@Order(order = 1)
	public void testCreateManufacturer() {
		currentResource = createManufacturer();
		manufacturerResource = currentResource;
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
		String json = "{ \"name\" : \"FABRICANTE TESTE\" }";
		manufacturerResource = createResource(json);
		return manufacturerResource;
	}
	
}
