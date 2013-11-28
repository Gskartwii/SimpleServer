package com.myzillawr.simpleserver.http;

import java.util.Date;

import org.luaj.vm2.LuaError;

import com.myzillawr.simpleserver.Util;
import com.myzillawr.simpleserver.lua.LuaPageHelper;
import com.myzillawr.simpleserver.lua.SimpleServerLua;

public abstract class LuaPage extends Page{
	private String htmlContent;
	
	public LuaPage(HttpRequest req){
		super(req);
		htmlContent = "";
	}

	@Override
	protected void handleContent(){
		if(htmlContent == null){
			htmlContent = "";
		}
		try{
			SimpleServerLua lua = new LuaPageHelper(this).setupEnv();
			String content = Util.fileToString(req);
			handleScript(lua, content);
			lua.get("exit").call();
		}catch(LuaError e){
			int line = e.getErrorLine();
			String msg = e.getErrorMessage();
			write("Fatal Error: <b>" + msg + "</b> on line <b>" + line + "</b>");
			handleHTMLContent();
			close();
		}catch(Exception e){
			e.printStackTrace();
			req.handleError(500, "Internal Server Error");
		}
	}
	
	public abstract void handleScript(SimpleServerLua lua, String contents) throws LuaError;
	
	public void handleHTMLContent(){
		req.out("HTTP/1.0 200 OK\r\n");
		req.standardHeaders();
		req.out("Content-Type: text/html\r\n");
		req.out("Content-Length: " + htmlContent.length() + "\r\n");
		req.out("Last-modified: " + req.DateFormat(new Date(req.getFile().lastModified())) + "\r\n");
		req.out("\r\n");
		req.out(htmlContent);
	}
	
	public void write(String string){
		htmlContent = htmlContent + string;
	}
}