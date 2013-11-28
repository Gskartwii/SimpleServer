package com.myzillawr.simpleserver.http.page;

import com.myzillawr.simpleserver.http.HttpRequest;

public abstract class Page{
	protected HttpRequest req;

	public Page(HttpRequest req){
		this.req = req;
		handleContent();
	}

	protected abstract void handleContent();
	
	public void close(){
		req.close();
	}
}