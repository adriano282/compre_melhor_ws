package com.compremelhor.ws.resource.impl;

import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Sku;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;
import com.google.gson.Gson;

@RunWith(OrderedRunner.class)
public class SkuResourceTest extends TestResource<Sku> {

	private static ManufacturerResourceTest manufacturerResource = new ManufacturerResourceTest();
	private static CategoryResourceTest categoryResource = new CategoryResourceTest();
	public static Sku sku;
		
	public SkuResourceTest() { super(Sku.class, "skus/"); }

	@Test
	@Order( order = 1)
	public void testCreateResource() {
		createSkuAndBackward();
	}
	
	@Test
	@Order(order = 2)
	public void testGetResource() {
		logger.log(Level.WARNING, currentResource);
		getResource();
	}

	@Test
	@Order(order = 3)
	public void testUpdateResource() {
		
		sku.setName("TESTE PRODUTO ALTERADO");
		
		String jsonF = new Gson().toJson(sku, Sku.class);
		logger.log(Level.INFO, jsonF);
		updateResorce(jsonF);
		Assert.assertEquals("TESTE PRODUTO ALTERADO", getResource().getName());
	}	

	@Test
	@Order(order = 4)
	public void testDeleteResource() {
		deleteSkuAndBackward();
	}
	
	public void deleteSkuAndBackward() {
		currentResource = APPLICATION_ROOT.concat("skus/").concat(String.valueOf(sku.getId()));
		deleteResource(currentResource);
		
		// Verify if SKU has been deleted
		Assert.assertNull(getResource());
		
		// Delete Manufacturer
		manufacturerResource.deleteManufacturer();
		
		// Delete Category
		categoryResource.deleleCategory();
	}
	
	public void createSkuAndBackward() {
		
		// Create Manufacturer
		manufacturerResource.createManufacturer();
		
		// Create Category
		categoryResource.createCategory();
		
		sku = new Sku();
		sku.setCode("1234567899999");
		sku.setName("MAIONESE");
		sku.setDescription("MAIONESE DE BAIXA CALORIA");
		sku.setUnit(Sku.UnitType.UN);
		sku.setCategory(CategoryResourceTest.category);
		sku.setManufacturer(ManufacturerResourceTest.manufacturer);
		
		currentResource = createResource(myGson.toJson(sku, Sku.class));
		sku = getResource();
	}

}
