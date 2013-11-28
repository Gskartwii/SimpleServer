package com.myzillawr.simpleserver.http;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.myzillawr.simpleserver.HttpRequestHandler;
import com.myzillawr.simpleserver.SimpleServer;

public class HttpRequest {
	private HttpRequestHandler handler;
	private File file;

	public HttpRequest(HttpRequestHandler handler, File page){
		this.handler = handler;
		this.file = page;
		if(page.exists()){
			try{
				ContentType.handlePage(this);
			}catch(Exception e){
				e.printStackTrace();
				handleError(500, "Internal Server Error");
				close();
			}
		}else{
			handleError(404, "Not Found");
			close();
		}
	}

	public HashMap<String, Object> getServer(){
		return handler._server;
	}
	
	public void close(){
		handler.close();
		Thread.currentThread().interrupt();
	}
	
	public void out(String string){
		if(!handler.client.isClosed()){
			try{
				handler.out.write(string);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	public void handleError(int statusCode, String status){
		String htmlContent = "<!DOCTYPE HTML><html><head><title>" + statusCode + " " + status + "</title></head><body><h1>" + status + "</h1><p>The requested file was not found on this server.</p><hr><address>SimpleServer Port 80</address></body></html>";
		out("HTTP/1.0 " + statusCode + " " + status + "\r\n");
		standardHeaders();
		out("Content-Type: text/html\r\n");
		out("Content-Length: " + htmlContent.length() + "\r\n");
		out("\r\n");
		out(htmlContent);
	}

	public void standardHeaders(){
		Date date = new Date();
		out("Date: " + DateFormat(date) + "\r\n");
		out("Server: SimpleServer/" + SimpleServer.VERSION + "\r\n");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		out("Expires: " + DateFormat(cal.getTime()) + "\r\n");
	}

	public String DateFormat(Date date){
		return handler.dateFormat(date);
	}

	public File getFile(){
		return file;
	}
}