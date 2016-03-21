package com.compremelhor.ws.resource.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Category;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class CategoryResourceTest extends TestResource<Category>{
	
	public static Category category;
	
	public CategoryResourceTest() {
		super(Category.class, "categories/");
		// TODO Auto-generated constructor stub
	}

	@Test
	@Order(order =1 )
	public void shouldCreateCategory() {
		createCategory();		
	}
	
	@Test
	@Order(order = 3 )
	public void updateCategory() {
		category.setName("Nome Alterado");
		updateResorce(myGson.toJson(category, Category.class));
	}
	
	@Test
	@Order(order = 4 )
	public void deleleCategory() {
		deleteCategory();
	}
	
	public void createCategory() {
		Category c = new Category();
		c.setName("Gelados");
		
		// Create category
		createResource(myGson.toJson(c, Category.class));
		category = getResource();
		
		// Verify if has been created
		Assert.assertNotNull(category);
		
		// Verify data
		Assert.assertEquals("Gelados", category.getName());
	}
	
	public void deleteCategory() {
		currentResource = APPLICATION_ROOT.concat("categories/").concat(String.valueOf(category.getId()));
		deleteResource(currentResource);
		
		Assert.assertNull(getResource());
	}
}
