package com.myzillawr.simpleserver.http.page;

import java.util.Date;

import com.myzillawr.simpleserver.http.ContentType;
import com.myzillawr.simpleserver.http.HttpRequest;
import com.myzillawr.simpleserver.util.Util;

public class ContentPage extends Page{
	public ContentPage(HttpRequest req){
		super(req);
	}

	@Override
	protected void handleContent(){
		try{
			String content = Util.fileToString(req);
			req.out("HTTP/1.0 200 OK\r\n");
			req.standardHeaders();
			req.out("Content-Type: " + ContentType.getContentType(req.getFile()) + "\r\n");
			req.out("Content-Length: " + content.length() + "\r\n");
			req.out("Last-modified: " + req.DateFormat(new Date(req.getFile().lastModified())) + "\r\n");
			req.out("\r\n");
			req.out(content);
		}catch(Exception e){
			e.printStackTrace();
			req.handleError(500, "Internal Server Error");
		}
	}
}