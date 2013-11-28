package com.myzillawr.simpleserver.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.myzillawr.simpleserver.http.LuaPage;

public class LuaPageHelper {
	private LuaPage page;
	
	public LuaPageHelper(LuaPage page){
		this.page = page;
	}
	
	public SimpleServerLua setupEnv(){
		SimpleServerLua lua = new SimpleServerLua();
		lua.set("die", new OneArgFunction(){
			@Override
			public LuaValue call(LuaValue arg){
				page.write(arg.tojstring());
				page.handleHTMLContent();
				page.close();
				return NIL;
			}
		});
		lua.set("write", new OneArgFunction(){
			@Override
			public LuaValue call(LuaValue arg){
				page.write(arg.tojstring());
				return NIL;
			}
		});
		lua.set("exit", new ZeroArgFunction(){
			@Override
			public LuaValue call(){
				page.handleHTMLContent();
				page.close();
				return NIL;
			}
		});
		lua.set("wait", new OneArgFunction(){
			@Override
			public LuaValue call(LuaValue arg){
				double waitTime = arg.optdouble(0.032);
				long fullWaitTime = (long)(waitTime * 1000.0);
				try{
					Thread.sleep(fullWaitTime);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				return NIL;
			}
		});
		return lua;
	}
}