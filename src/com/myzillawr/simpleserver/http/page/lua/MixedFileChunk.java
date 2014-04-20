package com.myzillawr.simpleserver.http.page.lua;

import com.myzillawr.luabase.lua.LuaVM;
import com.myzillawr.simpleserver.lua.SimpleServerGlobals;

public class MixedFileChunk {
	private String content;
	private boolean isLua;
	
	public MixedFileChunk(String content, boolean isLua){
		this.content = content;
		this.isLua = isLua;
	}
	
	public void handleContent(LuaVM lua, String chunkName){
		if(isLua){
			SimpleServerGlobals ssg = (SimpleServerGlobals)lua.getGlobals();
			ssg.loadstring(chunkName, content).call();
		}else{
			lua.get("write").call(content);
		}
	}
}