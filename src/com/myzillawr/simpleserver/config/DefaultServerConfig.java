package com.myzillawr.simpleserver.config;

import java.io.File;
import java.util.ArrayList;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import com.myzillawr.luabase.lua.BaseGlobals;
import com.myzillawr.luabase.lua.LuaVM;

public class DefaultServerConfig extends ServerConfig{
	private int port;
	private boolean threaded;
	private String documentRoot;
	private String[] indexNames;
	
	private Integer tport = null;
	private Boolean tthreaded = null;
	private String tdocumentRoot = null;
	private ArrayList<String> tindexNames = null;

	public DefaultServerConfig(){
		this(80, false, "/www");
	}
	
	public DefaultServerConfig(File configFile){
		tindexNames = new ArrayList<String>();
		if(configFile.exists()){
			try{
				final LuaVM lvm = new LuaVM();
				lvm.set("setPort", new OneArgFunction(){
					@Override
					public LuaValue call(LuaValue arg){
						if(!arg.isint()){
							throw new RuntimeException("Argument 1 expected integer, got " + arg.typename());
						}else{
							tport = arg.toint();
						}
						return NIL;
					}
				});
				lvm.set("setThreaded", new OneArgFunction(){
					@Override
					public LuaValue call(LuaValue arg){
						if(!arg.isboolean()){
							throw new RuntimeException("Argument 1 expected boolean, got " + arg.typename());
						}else{
							tthreaded = arg.toboolean();
						}
						return NIL;
					}
				});
				lvm.set("setDocumentRoot", new OneArgFunction(){
					@Override
					public LuaValue call(LuaValue arg){
						if(!arg.isstring() || arg.isuserdata(com.myzillawr.luabase.noninstance.io.File.class)){
							throw new RuntimeException("Argument 1 expected string or File, got " + arg.typename());
						}else{
							if(arg.isuserdata(com.myzillawr.luabase.noninstance.io.File.class)){
								tdocumentRoot = ((com.myzillawr.luabase.noninstance.io.File)CoerceLuaToJava.coerce(arg, com.myzillawr.luabase.noninstance.io.File.class)).GetAbsolutePath();
							}else{
								tdocumentRoot = arg.tojstring();
							}
						}
						return NIL;
					}
				});
				lvm.set("addDirectoryIndex", new OneArgFunction(){
					@Override
					public LuaValue call(LuaValue arg){
						if(!arg.isstring()){
							throw new RuntimeException("Argument 1 expected string, got " + arg.typename());
						}else{
							tindexNames.add(arg.tojstring());
						}
						return NIL;
					}
				});
				lvm.doFile(configFile);
				BaseGlobals _G = (BaseGlobals)lvm.getGlobals();
				if(_G.lbl.hasDelays()){
					_G.lbl.OnComplete.waitFunc();
				}
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