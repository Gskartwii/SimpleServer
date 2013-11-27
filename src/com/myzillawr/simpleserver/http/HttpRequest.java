package com.myzillawr.simpleserver.http;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.myzillawr.simpleserver.HttpRequestHandler;
import com.myzillawr.simpleserver.SimpleServer;
import com.myzillawr.simpleserver.lua.SimpleServerLua;

public class HttpRequest {
	private HttpRequestHandler handler;
	
	private String htmlContent;
	
	private File page;

	public HttpRequest(HttpRequestHandler handler, File page){
		this.handler = handler;
		this.page = page;
		htmlContent = "";
		if(page.exists()){
			if(page.getName().toLowerCase().endsWith(".lua")){
				SimpleServerLua lua = new SimpleServerLua();
				setupEnv(lua);
				try{
					lua.dofile(page);
					lua.get("exit").call();
				}catch(LuaError e){
					int line = e.getErrorLine();
					String msg = e.getErrorMessage();
					write("Fatal Error: <b>" + msg + "</b> on line <b>" + line + "</b>");
					handleHTMLContent();
					close();
				}
			}else{
				//TODO: Handle other file types.
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
	
	public void write(String string){
		htmlContent = htmlContent + string;
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
	
	private void handleHTMLContent(){
		out("HTTP/1.0 200 OK\r\n");
		SimpleDateFormat f = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
		Date date = new Date();
		out("Date: " + f.format(date) + "\r\n");
		out("Server: SimpleServer/" + SimpleServer.VERSION + "\r\n");
		out("Content-Type: text/html\r\n");
		out("Content-Length: " + htmlContent.length() + "\r\n");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		out("Expires: " + f.format(cal.getTime()) + "\r\n");
		out("Last-modified: " + f.format(page.lastModified()) + "\r\n");
		out("\r\n");
		out(htmlContent);
	}

	private void handleError(int statusCode, String status){
		htmlContent = "<!DOCTYPE HTML><html><head><title>" + statusCode + " " + status + "</title></head><body><h1>" + status + "</h1><p>The requested file was not found on this server.</p><hr><address>SimpleServer Port 80</address></body></html>";
		
		out("HTTP/1.0 " + statusCode + " " + status + "\r\n");
		SimpleDateFormat f = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
		Date date = new Date();
		out("Date: " + f.format(date) + "\r\n");
		out("Server: SimpleServer/" + SimpleServer.VERSION + "\r\n");
		out("Content-Type: text/html\r\n");
		out("Content-Length: " + htmlContent.length() + "\r\n");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		out("Expires: " + f.format(cal.getTime()) + "\r\n");
		//bw.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
		out("\r\n");
		out(htmlContent);
	}
	
	private void setupEnv(SimpleServerLua lua) {
		lua.set("die", new OneArgFunction(){
			@Override
			public LuaValue call(LuaValue arg){
				write(arg.tojstring());
				handleHTMLContent();
				close();
				return NIL;
			}
		});
		lua.set("write", new OneArgFunction(){
			@Override
			public LuaValue call(LuaValue arg){
				write(arg.tojstring());
				return NIL;
			}
		});
		lua.set("exit", new ZeroArgFunction(){
			@Override
			public LuaValue call(){
				handleHTMLContent();
				close();
				return NIL;
			}
		});
		lua.set("wait", new OneArgFunction(){
			@Override
			public LuaValue call(LuaValue arg){
				double waitTime = arg.optdouble(0.032);
				long fullWaitTime = (long)(waitTime * 1000.0);
				try{
					Thread.sleep(fullWaitTime);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				return NIL;
			}
		});
	}
}