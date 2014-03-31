package com.myzillawr.simpleserver.config;

import java.io.File;
import java.util.ArrayList;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.myzillawr.simpleserver.lua.SimpleServerLua;

public class DefaultServerConfig extends ServerConfig{
	private int port;
	private boolean threaded;
	private String documentRoot;
	private String[] indexNames;
	
	private Integer tport = null;
	private Boolean tthreaded = null;
	private String tdocumentRoot = null;
	private ArrayList<String> tindexNames = null;
	private boolean ranLua = false;
	
	public DefaultServerConfig(){
		this(80, false, "/www");
	}
	
	public DefaultServerConfig(File configFile){
		tindexNames = new ArrayList<String>();
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
				lua.set("addDirectoryIndex", new OneArgFunction(){
					@Override
					public LuaValue call(LuaValue arg){
						if(!arg.isstring()){
							throw new RuntimeException("addDirectoryIndex: Argument 1 must be a string.");
						}else{
							tindexNames.add(arg.tojstring());
						}
						return NIL;
					}
				});
				lua.dolocalfile(configFile);
				lua.set("exitConfig", new ZeroArgFunction(){
					@Override
					public LuaValue call(){
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
						indexNames = tindexNames.toArray(new String[tindexNames.size()]);
						ranLua = true;
						return NIL;
					}
				});
				lua.get("exitConfig").call();
				while(!ranLua){
					System.out.println("Waiting");
					Thread.sleep(5);
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

	@Override
	public String[] getIndexNames(){
		return indexNames;
	}
}