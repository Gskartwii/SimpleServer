package com.myzillawr.simpleserver.http.page.lua;

import org.luaj.vm2.LuaError;

import com.myzillawr.luabase.lua.LuaVM;
import com.myzillawr.simpleserver.http.HttpRequest;
import com.myzillawr.simpleserver.lua.SimpleServerGlobals;

public class FullLuaPage extends LuaPage{
	public FullLuaPage(HttpRequest req){
		super(req);
	}

	@Override
	public void handleScript(LuaVM lua, String contents) throws LuaError{
		SimpleServerGlobals ssg = (SimpleServerGlobals)lua.getGlobals();
		ssg.loadstring(req.getFile().getAbsolutePath(), contents).call();
	}
}