package com.compremelhor.ws.resource.impl;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Category;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class CategoryResourceTest extends TestResource<Category>{

	public CategoryResourceTest() {
		super(Category.class, "categories/");
		// TODO Auto-generated constructor stub
	}

	@Test
	@Order(order =1 )
	public void shouldCreateCategory() {
		String json = "{\"name\":\"Categoria Teste\"}";
		currentResource = createResource(json);
		
	}
	
	@Test
	@Order(order = 2 )
	public void shouldGetAnCategory() {
		getResource();
	}
	
	@Test
	@Order(order = 3 )
	public void updateManufacturer() {
		String json = "{\"name\": \"NOME CATEGORIA ALTERADO\"}";
		updateResorce(json);
	}
	
	@Test
	@Order(order = 4 )
	public void deleleCategory() {
		deleteResource(currentResource);
	}
}
