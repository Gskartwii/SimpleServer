package com.myzillawr.simpleserver.config;

import java.io.File;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.myzillawr.simpleserver.lua.SimpleServerLua;

public class DefaultServerConfig extends ServerConfig{
	private int port;
	private boolean threaded;
	private String documentRoot;
	
	Integer tport = null;
	Boolean tthreaded = null;
	String tdocumentRoot = null;
	
	public DefaultServerConfig(){
		this(80, false, "/www");
	}
	
	public DefaultServerConfig(File configFile){
		if(configFile.exists()){
			try{
				final SimpleServerLua lua = new SimpleServerLua();
				lua.set("setPort", new OneArgFunction(){
					@Override
					public LuaValue call(LuaValue arg){
						if(!arg.isint()){
							throw new RuntimeException("setPort: Argument 1 must be an integer.");
						}else{
							tport = new Integer(arg.toint());
							lua.set("setPort", NIL);
						}
						return NIL;
					}
				});
				lua.set("setThreaded", new OneArgFunction(){
					@Override
					public LuaValue call(LuaValue arg){
						if(!arg.isboolean()){
							throw new RuntimeException("setThreaded: Argument 1 must be a boolean.");
						}else{
							tthreaded = arg.toboolean();
							lua.set("setThreaded", NIL);
						}
						return NIL;
					}
				});
				lua.set("setDocumentRoot", new OneArgFunction(){
					@Override
					public LuaValue call(LuaValue arg){
						if(!arg.isstring()){
							throw new RuntimeException("setDocumentRoot: Argument 1 must be a string.");
						}else{
							tdocumentRoot = arg.tojstring();
							lua.set("setDocumentRoot", NIL);
						}
						return NIL;
					}
				});
				lua.dolocalfile(configFile);
				if(tport != null){
					port = tport;
				}else{
					port = 80;
				}
				if(tthreaded != null){
					threaded = tthreaded;
				}else{
					threaded = false;
				}
				if(tdocumentRoot != null){
					documentRoot = tdocumentRoot;
				}else{
					documentRoot = "www/";
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}else{
			port = 80;
			threaded = false;
			documentRoot = "/www";
		}
	}
	
	public DefaultServerConfig(int port, boolean threaded, String documentRoot){
		this.port = port;
		this.threaded = threaded;
		this.documentRoot = documentRoot;
	}
	
	@Override
	public int getPort(){
		return port;
	}

	@Override
	public boolean isThreaded(){
		return threaded;
	}

	@Override
	public String getDocumentRoot(){
		return documentRoot;
	}
}