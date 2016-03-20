package com.compremelhor.ws.resource.impl;

import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.SkuPartner;
import com.compremelhor.model.entity.Stock;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class StockResourceTest extends TestResource<Stock>{

	// Others Tests Resources necessary for this
	private static SkuResourceTest skuResourceTest = new SkuResourceTest();
	private static PartnerResourceTest partnerResourceTest = new PartnerResourceTest();
	
	public static Stock stock;
	public static SkuPartner skuPartner;
	
	public StockResourceTest() { super(Stock.class, "stock/");}
	
	@Test
	@Order(order = 1)
	public void shoudCreateSkuPartnerAndStock() {
		
		// Create Category, Manufacturer And SKU
		skuResourceTest.createSkuAndBackward();
		
		// Create Partner
		partnerResourceTest.createPartner();
		
		// Create Stock and SkuPartner
		createStockAndSkuPartner();		
	}
	
	@Test
	@Order(order = 2)
	public void shouldUpdateStock() {
		stock.setQuantity(100.00);
		stock.setUnitPrice(25.20);
		
		updateResorce(myGson.toJson(stock, Stock.class));
		
		stock = getResource();
		
		Assert.assertNotNull(stock);
		
		// Verify changes
		Assert.assertEquals(Double.valueOf(100.00), stock.getQuantity());
		Assert.assertEquals(Double.valueOf(25.20), stock.getUnitPrice());
		
		logger.log(Level.INFO, "Stock changed: "+ stock.getId());
	}
	
	@Test
	@Order(order = 3)
	public void shouldDeleteStock() {
		deleteStockAndBackward();
	}
	
	public void createStockAndSkuPartner() {
		skuPartner = new SkuPartner();
		skuPartner.setSku(SkuResourceTest.sku);
		skuPartner.setPartner(PartnerResourceTest.partner);
		
		stock = new Stock();		
		stock.setQuantity(10.00);
		stock.setUnitPrice(1.00);
		stock.setSkuPartner(skuPartner);
		
		createResource(myGson.toJson(stock, Stock.class));
		stock = getResource();
		skuPartner = stock.getSkuPartner();
		
		// Verify if are not null
		Assert.assertNotNull(stock);
		Assert.assertNotNull(skuPartner);
		
		// Verify if stock has been created
		Assert.assertNotEquals(0, stock.getId());
		logger.log(Level.INFO, "Stock created: " + stock.getId());
		
		// Verify if skuPartner has been created
		Assert.assertNotEquals(0, skuPartner.getId());
		logger.log(Level.INFO, "SkuPartner created: " + skuPartner.getId());
		
		// Verify data in stock created
		// Verify Quantity
		Assert.assertEquals(Double.valueOf(10.00), stock.getQuantity());
		
		// Verify Unit Price
		Assert.assertEquals(Double.valueOf(1.00), stock.getUnitPrice());
	}
	
	public void deleteStockAndBackward() {		
		currentResource = APPLICATION_ROOT.concat("stock/").concat(String.valueOf(stock.getId()));
		deleteResource(currentResource);
		
		// Verify if stock has been deleted
		Assert.assertNull(getResource());		
		
		// Delete partner:
		partnerResourceTest.deletePartner();
		
		// Delete SKU and Backward
		skuResourceTest.deleteSkuAndBackward();
	}

}
