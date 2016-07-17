package com.compremelhor.ws.resource.impl;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import com.compremelhor.model.entity.FreightType;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/freight_types")
@TokenAuthenticated
public class FreightTypeResource extends AbstractResource<FreightType>{

	public FreightTypeResource()
			throws NamingException {
		super(FreightType.class, "freight_types");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EJBRemote<FreightType> lookupService() throws NamingException {
		return (EJBRemote<FreightType>) context
				.lookup("ejb:/compre_melhor_ws/FreightTypeEJB!com.compremelhor.model.remote.EJBRemote");
	}
	
}
