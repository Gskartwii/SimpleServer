package com.myzillawr.simpleserver.http;

import java.util.ArrayList;

import org.luaj.vm2.LuaError;

import com.myzillawr.simpleserver.Util;
import com.myzillawr.simpleserver.lua.SimpleServerLua;

public class MixedLuaPage extends LuaPage{
	public MixedLuaPage(HttpRequest req){
		super(req);
	}

	@Override
	public void handleScript(SimpleServerLua lua, String contents) throws LuaError {
		ArrayList<MixedFileChunk> chunks = new ArrayList<MixedFileChunk>();
		int indexOf = -1;
		int currentLine = 0;
		while((indexOf = contents.indexOf("<?lua")) != -1){
			String contentBefore = contents.substring(0, indexOf);
			if(contentBefore.length() > 0){
				chunks.add(new MixedFileChunk(contentBefore, false));
			}
			contents = contents.substring(indexOf);
			int end = contents.indexOf("?>");
			if(end == -1){
				end = contents.length();
			}else{
				end = end + 2;
			}
			String chunkContent = contents.substring(0, end);
			chunkContent = chunkContent.substring(5);
			chunkContent = chunkContent.substring(0, chunkContent.length() - 2);
			currentLine += Util.lineCount(contentBefore);
			contents = contents.substring(end);
			int chunkLines = Util.lineCount(chunkContent);
			for(int i = 0; i < (currentLine - 1); i++){
				chunkContent = "\n" + chunkContent;
			}
			currentLine += chunkLines;
			chunks.add(new MixedFileChunk(chunkContent, true));
		}
		if(contents.length() > 0){
			chunks.add(new MixedFileChunk(contents, false));
		}
		for(int i = 0; i < chunks.size(); i++){
			chunks.get(i).handleContent(lua, req.getFile().getAbsolutePath());
		}
	}
}