package com.compremelhor.ws.resource.impl;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/manufacturers")
@TokenAuthenticated
public class ManufacturerResource extends AbstractResource<Manufacturer> {		
	
	public ManufacturerResource() throws NamingException {
		super(Manufacturer.class, "manufacturers");
	}
	
	@SuppressWarnings("unchecked")
	public EJBRemote<Manufacturer> lookupService() throws NamingException {
		return (EJBRemote<Manufacturer>) context
				.lookup("ejb:/compre_melhor_ws/ManufacturerEJB!com.compremelhor.model.remote.EJBRemote");
	}
}
