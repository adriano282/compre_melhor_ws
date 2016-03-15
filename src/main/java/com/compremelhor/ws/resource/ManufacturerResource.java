package com.compremelhor.ws.resource;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import com.compremelhor.model.entity.Manufacturer;
import com.compremelhor.model.remote.EJBRemote;

@Path("/manufacturers")
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
