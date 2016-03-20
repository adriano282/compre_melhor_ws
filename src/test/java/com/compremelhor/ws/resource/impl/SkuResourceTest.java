package com.compremelhor.ws.resource.impl;

import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Category;
import com.compremelhor.model.entity.Code;
import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.model.entity.Sku;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;
import com.google.gson.Gson;

@RunWith(OrderedRunner.class)
public class SkuResourceTest extends TestResource<Sku> {

	private static ManufacturerResourceTest manufacturerResource = new ManufacturerResourceTest();
	
	private static Sku s;
	private static Manufacturer manufacturer;
	public String skuResource;
	public int skuId;
		
	public SkuResourceTest() {
		super(Sku.class, "skus/");
	}

	@Test
	@Order( order = 1)
	public void testCreateResource() {
		String json = "{"
				+ "\"name\" : \"Maionese\","
				+ "\"description\" : \"Maionese com baixa caloria\","
				+ "\"categories\" : "
				+ "["
				+ "{\"name\" : \"GELADOS\"},"
				+ "{ \"name\": \"DIET\""
				+ "],"
				+ "\"unit\" : \"UN\","
				+ "\"code\" : {\"type\" : \"BARCODE\", \"code\" : \"COD001\" },"
				+ "\"manufacturer\" : {\"name\" : \"HELLMANS\"}"
				+ "}";
		
		Gson g = new Gson();
		
		manufacturerResource.createManufacturer();
		manufacturer = manufacturerResource.getResource(); 
		Assert.assertNotEquals(0, manufacturer.getId());
		
		Category c = new Category();
		c.setName("GELADOS");
		
		Code code = new Code();
		code.setType(Code.CodeType.BARCODE);
		code.setCode("COD001");
		
		s = new Sku();
		s.setName("MAIONESE");
		s.setDescription("MAIONESE DE BAIXA CALORIA");
		s.setUnit(Sku.UnitType.UN);
		s.addCategory(c);
		s.setManufacturer(manufacturer);
		s.setCode(code);
		
		String json2 = g.toJson(s, Sku.class);
		logger.log(Level.WARNING, json2);
		skuResource =  currentResource = createResource(json2);
		skuId = s.getId();
		
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
		
		s.setName("TESTE PRODUTO ALTERADO");
		
		String jsonF = new Gson().toJson(s, Sku.class);
		logger.log(Level.INFO, jsonF);
		updateResorce(jsonF);
		Assert.assertEquals("TESTE PRODUTO ALTERADO", getResource().getName());
	}	

	@Test
	@Order(order = 4)
	public void testDeleteResource() {
		deleteResource(currentResource);
		manufacturerResource.deleteResource(manufacturerResource.manufacturerResource);
	}



}
