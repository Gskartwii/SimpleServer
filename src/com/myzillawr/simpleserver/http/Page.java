package com.myzillawr.simpleserver.http;

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