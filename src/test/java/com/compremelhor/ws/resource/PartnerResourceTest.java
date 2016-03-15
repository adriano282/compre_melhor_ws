package com.compremelhor.ws.resource;

import java.util.logging.Level;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Partner;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class PartnerResourceTest extends TestResource<Partner>{

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
	public void testDeletePartner() {
		deletePartner(currentResource);
		logger.log(Level.INFO, "DELETE /partners/" + currentId);
	}
	
	
	public void deletePartner(String resourceURI) {
		deleteResource(resourceURI);
	}
	
	public String createPartner() {
		String json = "{\"name\":\"Partner test\"}";
		return createResource(json);
	}
	
	
}
