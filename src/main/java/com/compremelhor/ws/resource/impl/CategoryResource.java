package com.compremelhor.ws.resource.impl;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import com.compremelhor.model.entity.Category;
import com.compremelhor.model.remote.EJBRemote;
import com.compremelhor.ws.annotation.TokenAuthenticated;
import com.compremelhor.ws.resource.AbstractResource;

@Path("/categories")
@TokenAuthenticated
public class CategoryResource extends AbstractResource<Category>{

	public CategoryResource()
			throws NamingException {
		super(Category.class, "categories");
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public EJBRemote<Category> lookupService() throws NamingException {
		return (EJBRemote<Category>) context
				.lookup("ejb:/compre_melhor_ws/CategoryEJB!com.compremelhor.model.remote.EJBRemote");
	}

}
