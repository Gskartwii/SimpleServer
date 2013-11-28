package com.myzillawr.simpleserver.http;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.myzillawr.simpleserver.SimpleServer;

public class HttpRequest {
	private HttpRequestHandler handler;
	private File file;

	public HttpRequest(HttpRequestHandler handler, File page){
		this.handler = handler;
		this.file = page;
		if(page.isDirectory()){
			String[] indexNames = handler.getIndexNames();
			for(int i = 0; i < indexNames.length; i++){
				if(new File(page, indexNames[i]).exists()){
					page = new File(page, indexNames[i]);
					break;
				}
			}
			if(page.isDirectory()){
				String fileList = "<li><a href=\"../\">Parent Directory</a></li>";
				File[] fileNames = page.listFiles();
				for(int i = 0; i < fileNames.length; i++){
					String fileName = fileNames[i].getName();
					if(fileNames[i].isDirectory()){
						fileName = fileName + "/";
					}
					fileList = fileList + "<li><a href=\"" + fileName + "\">" + fileName + "</a></li>";
					if(i != fileNames.length){
						fileList = fileList + '\n';
					}
				}
				serverPage("Index of " + (String)handler._server.get("REQUEST_URI"), "<h1>Index of " + (String)handler._server.get("REQUEST_URI") + "</h1><ul>" + fileList + "</ul>", "200 OK");
				
			}else{
				handlePage();
			}
		}else{
			if(page.isFile()){
				handlePage();
			}else{
				handleError(500, "Internal Server Error");
			}
		}
	}
	
	private void handlePage(){
		if(file.exists()){
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
	
	public void serverPage(String title, String body, String httpCode){
		String content = "<DOCTYPE html>" + '\n'
			+ "<html>" + '\n'
			+ "<head>" + '\n'
			+ '\r' + "<title>" + title + "</title>" + '\n'
			+ "</head>" + '\n'
			+ "<body>" + '\n'
			+ body + '\n'
			+ "<address>SimpleServer HTTP Server on port " + getServerPort() + ".</address>" + '\n'
			+ "</body>" + '\n'
			+ "</html>";
		out("HTTP/1.0 " + httpCode  +  "\r\n");
		standardHeaders();
		out("Content-Type: text/html\r\n");
		out("Content-Length: " + content.length() + "\r\n");
		out("\r\n");
		out(content);
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

	public int getServerPort(){
		return handler.getServerPort();
	}
}