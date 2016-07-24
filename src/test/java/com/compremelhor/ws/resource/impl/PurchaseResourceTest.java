package com.compremelhor.ws.resource.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.compremelhor.model.entity.Freight;
import com.compremelhor.model.entity.Purchase;
import com.compremelhor.model.entity.Purchase.Status;
import com.compremelhor.model.entity.PurchaseLine;
import com.compremelhor.ws.annotation.Order;
import com.compremelhor.ws.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
public class PurchaseResourceTest extends TestResource<Purchase>{

	public static Purchase purchase;
	public static PurchaseLine purchaseLine;
	public static Freight freight;
	
	public static StockResourceTest stockResourceTest = new StockResourceTest();
	public static UserResourceTest userResourceTest = new UserResourceTest();
	
	public PurchaseResourceTest() {
		super(Purchase.class, "purchases/");
	}
	
	@Test
	@Order(order = 1)
	public void shoudlCreatePurchase() {
		createPurchaseAndBackWard();
	}
	
	@Test
	@Order(order = 2)
	public void shoudlCreatePurchaseLine() {
		createPurchaseLine();
	}

	@Test
	@Order(order = 3)
	public void shouldCreateFreight() {
		freight = new Freight();
		
		freight.setPurchase(purchase);
		
		/*purchase.getUser()
				.getOptionalAddresses()
				.ifPresent(ads -> freight.setShipAddress(ads.get(0)));*/
		
		userResourceTest.addAddress();
				
		freight.setShipAddress(UserResourceTest.address);
		
		Assert.assertNotNull(freight.getShipAddress());
		freight.setRideValue(50.00);
		
		String uri = APPLICATION_ROOT.concat("purchases/")
				.concat(String.valueOf(purchase.getId()))
				.concat("/freight");
		
		freight = getResource(createResource(myGson.toJson(freight), uri), 
				Freight.class);
		
		Assert.assertNotNull(freight);
	}
	
	@Test
	@Order(order = 4)
	public void shouldDeleteFreight() {
		String uri = APPLICATION_ROOT.concat("purchases/")
				.concat(String.valueOf(purchase.getId()))
				.concat("/freight/")
				.concat(String.valueOf(freight.getId()));
		deleteResource(uri);
		
		Assert.assertNull(getResource(uri, Freight.class));
	}
	
	@Test
	@Order(order = 5)
	public void shoudlDeletePurchaseLine() {
		deletePurchaseLineAndStock();
	}
	
	@Test
	@Order(order = 6)
	public void shoudlDeletePurchase() {
		deletePurchaseAndBackWard();
	}
	
	public void createPurchaseAndBackWard() {
		userResourceTest.createUser();
		
		purchase = new Purchase();
		purchase.setStatus(Status.OPENED);
		purchase.setUser(UserResourceTest.user);
		purchase.setTotalValue(0.0);
		
		createResource(myGson.toJson(purchase));
		purchase = getResource();
		
		Assert.assertNotNull(purchase);
	}
	
	public void createPurchaseLine() {
		stockResourceTest.createStockAndSkuPartner();
		
		purchaseLine = new PurchaseLine();
		purchaseLine.setPurchase(purchase);
		purchaseLine.setQuantity(3.00);
		purchaseLine.setUnitPrice(StockResourceTest.stock.getUnitPrice());
		purchaseLine.setStock(StockResourceTest.stock);
		
		String uri = APPLICATION_ROOT
				.concat("purchases/")
				.concat(String.valueOf(purchase.getId()))
				.concat("/lines");
		
		purchaseLine = 
				getResource(createResource(myGson.toJson(purchaseLine), uri), 
						PurchaseLine.class);		
		Assert.assertNotNull(purchaseLine);
	}
	
	public void deletePurchaseLineAndStock() {
		String uri = APPLICATION_ROOT
				.concat("purchases/")
				.concat(String.valueOf(purchase.getId()))
				.concat("/lines/")
				.concat(String.valueOf(purchaseLine.getId()));
		
		deleteResource(uri);
		Assert.assertNull(getResource(uri, PurchaseLine.class));
		
		stockResourceTest.deleteStockAndBackward();
	}
	
	public void deletePurchaseAndBackWard() {
		currentResource = APPLICATION_ROOT
				.concat("purchases/")
				.concat(String.valueOf(purchase.getId()));
		
		deleteResource(currentResource);
		purchase = getResource();
		Assert.assertNull(purchase);
		
		userResourceTest.deleteUser();
	}
}
