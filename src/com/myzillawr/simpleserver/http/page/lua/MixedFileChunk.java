package com.myzillawr.simpleserver.http.page.lua;

import com.myzillawr.simpleserver.lua.SimpleServerLua;

public class MixedFileChunk {
	private String content;
	private boolean isLua;
	
	public MixedFileChunk(String content, boolean isLua){
		this.content = content;
		this.isLua = isLua;
	}
	
	public void handleContent(SimpleServerLua lua, String chunkName){
		if(isLua){
			lua.dostring(chunkName, content);
		}else{
			lua.get("write").call(content);
		}
	}
}