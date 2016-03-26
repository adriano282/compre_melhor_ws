package com.compremelhor.ws.resource.impl;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import com.compremelhor.model.entity.Purchase;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/purchases")
@TokenAuthenticated
public class PurchaseResource extends AbstractResource<Purchase>{

	public PurchaseResource() throws NamingException {
		super(Purchase.class, "purchases");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EJBRemote<Purchase> lookupService() throws NamingException {
		return (EJBRemote<Purchase>) context
				.lookup("ejb:/compre_melhor_ws/PurchaseEJB!com.compremelhor.model.remote.EJBRemote");
	}
}
