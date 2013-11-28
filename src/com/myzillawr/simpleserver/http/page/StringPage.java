package com.myzillawr.simpleserver.http.page;

import com.myzillawr.simpleserver.http.HttpRequest;

public class StringPage extends Page{
	private String content;
	
	public StringPage(HttpRequest req, String content){
		super(req);
		System.out.println(content);
		this.content = content;
	}

	@Override
	protected void handleContent(){
		try{
			req.out("HTTP/1.0 200 OK\r\n");
			req.standardHeaders();
			req.out("Content-Type: text/html\r\n");
			req.out("Content-Length: " + content.length() + "\r\n");
			req.out("\r\n");
			req.out(content);
		}catch(Exception e){
			e.printStackTrace();
			req.handleError(500, "Internal Server Error");
		}
		close();
	}
}