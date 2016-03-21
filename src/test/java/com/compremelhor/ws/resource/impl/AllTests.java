package com.compremelhor.ws.resource.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CategoryResourceTest.class, ManufacturerResourceTest.class,
		PartnerResourceTest.class, SkuResourceTest.class,
		StockResourceTest.class, UserResourceTest.class })
public class AllTests {

}
