package com.compremelhor.ws.util;

import java.io.InputStream;
import java.util.Scanner;

import com.compremelhor.model.entity.EntityModel;
import com.google.gson.Gson;

public class JsonUtil<T extends EntityModel> {
	private Class<T> clazz;
	private Gson g = new Gson();
	public T getEntityFromInputStream(InputStream is) {
		String json = "";
		try (Scanner s = new Scanner(is)) {
			json = s.useDelimiter("\\A").next();
		}
		return g.fromJson(json, clazz);
	}
}
