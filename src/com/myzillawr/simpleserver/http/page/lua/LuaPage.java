package com.myzillawr.simpleserver.http.page.lua;

import java.util.Date;

import org.luaj.vm2.LuaError;

import com.myzillawr.luabase.lua.LuaVM;
import com.myzillawr.simpleserver.http.HttpRequest;
import com.myzillawr.simpleserver.http.page.Page;
import com.myzillawr.simpleserver.lua.LuaPageHelper;
import com.myzillawr.simpleserver.lua.SimpleServerGlobals;
import com.myzillawr.simpleserver.util.Util;

public abstract class LuaPage extends Page{
	private static LuaVM mainVM = new LuaVM(new SimpleServerGlobals());
	
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
			String content = Util.fileToString(req);
			LuaPageHelper.setupEnv(this, mainVM);
			handleScript(mainVM, content);
			handleHTMLContent();
			close();
		}catch(LuaError e){
			e.printStackTrace();
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
	
	public abstract void handleScript(LuaVM lua, String contents) throws LuaError;
	
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