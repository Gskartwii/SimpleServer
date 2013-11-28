package com.myzillawr.simpleserver.http.page.lua;

import org.luaj.vm2.LuaError;

import com.myzillawr.simpleserver.http.HttpRequest;
import com.myzillawr.simpleserver.lua.SimpleServerLua;

public class FullLuaPage extends LuaPage{
	public FullLuaPage(HttpRequest req){
		super(req);
	}

	@Override
	public void handleScript(SimpleServerLua lua, String contents) throws LuaError {
		lua.dostring(req.getFile().getAbsolutePath(), contents);
	}
}