package com.compremelhor.ws.resource.impl;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import com.compremelhor.model.entity.Partner;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/partners")
public class PartnerResource extends AbstractResource<Partner>{
	
	public PartnerResource() throws NamingException { super(Partner.class, "partners"); }
	
	@SuppressWarnings("unchecked")
	public EJBRemote<Partner> lookupService() throws NamingException {
		return (EJBRemote<Partner>) context
				.lookup("ejb:/compre_melhor_ws/PartnerEJB!com.compremelhor.model.remote.EJBRemote");
	}
	
	
}
