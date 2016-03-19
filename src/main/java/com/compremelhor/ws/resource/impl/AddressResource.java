package com.compremelhor.ws.resource.impl;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import com.compremelhor.model.entity.Address;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/addresses")
public class AddressResource extends AbstractResource<Address>{
	public AddressResource() throws NamingException {
		super(Address.class, "addresses");
	}
	
	@SuppressWarnings("unchecked")
	public EJBRemote<Address> lookupService() throws NamingException {
		return (EJBRemote<Address>) context
				.lookup("ejb:/compre_melhor_ws/AddressEJB!com.compremelhor.model.remote.EJBRemote");
	}
}
