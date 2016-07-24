package com.compremelhor.ws.resource.impl;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import com.compremelhor.model.entity.SyncronizeMobile;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/sync")
@TokenAuthenticated
public class SyncMobResource extends AbstractResource<SyncronizeMobile> {

	public SyncMobResource()
			throws NamingException {
		super(SyncronizeMobile.class, "sync");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EJBRemote<SyncronizeMobile> lookupService()
			throws NamingException {
		return (EJBRemote<SyncronizeMobile>) context
				.lookup("ejb:/compre_melhor_ws/SyncMobEJB!com.compremelhor.model.remote.EJBRemote");	}
	
}
