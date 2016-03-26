package com.compremelhor.ws.resource.impl;

import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.compremelhor.model.entity.Sku;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/skus")
@TokenAuthenticated
public class SkuResource extends AbstractResource<Sku> {

	public SkuResource() throws NamingException {
		super(Sku.class, "skus");
	}

	@SuppressWarnings("unchecked")
	@Override
	public EJBRemote<Sku> lookupService() throws NamingException {
		return (EJBRemote<Sku>) context
				.lookup("ejb:/compre_melhor_ws/SkuEJB!com.compremelhor.model.remote.EJBRemote");
	}
	
	@Override
	public Sku getResource(@PathParam("id") int id) {
		Set<String> fetches = new HashSet<String>();
		fetches.add("categories");
		return service.find(id, fetches);
	}

}
