package com.compremelhor.ws.resource;

import java.util.logging.Level;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class ManufacturerResourceTest extends TestResource<Manufacturer>{

	public ManufacturerResourceTest() {
		super(Manufacturer.class, "manufacturers/");
	}
		
	@Test
	@Order(order = 1)
	public void testCreateManufacturer() {
		currentResource = createManufacturer();
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
		logger.log(Level.INFO, "DELETE /manufacturers/" + currentId);
	}
	
	public void deleteManufacturer(String resourceURI) {
		deleteResource(resourceURI);
	}

	public String createManufacturer() {
		String json = "{ \"name\" : \"FABRICANTE TESTE\" }";
		return createResource(json);
	}
}
