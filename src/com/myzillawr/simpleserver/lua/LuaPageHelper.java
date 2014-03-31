package com.myzillawr.simpleserver.lua;

import java.util.HashMap;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.myzillawr.simpleserver.http.HttpRequestHandler.NoEntry;
import com.myzillawr.simpleserver.http.page.lua.LuaPage;

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
		LuaTable SERVER = new LuaTable();
		HashMap<String, Object> SERVERMap = page.req.getSERVER();
		Object[] SERVERKeys = SERVERMap.keySet().toArray();
		for(int i = 0; i < SERVERKeys.length; i++){
			Object keyVal = SERVERMap.get(SERVERKeys[i]);
			if(keyVal instanceof String){
				SERVER.set(SERVERKeys[i].toString(), LuaString.valueOf((String)keyVal));
			}else if(keyVal instanceof Integer){
				SERVER.set(SERVERKeys[i].toString(), LuaNumber.valueOf((int)keyVal));
			}else if(keyVal instanceof Double){
				SERVER.set(SERVERKeys[i].toString(), LuaNumber.valueOf((double)keyVal));
			}else if(keyVal instanceof Boolean){
				SERVER.set(SERVERKeys[i].toString(), LuaBoolean.valueOf((boolean)keyVal));
			}else{
				SERVER.set(SERVERKeys[i].toString(), CoerceJavaToLua.coerce(keyVal));
			}
		}
		lua.set("SERVER", SERVER);
		
		LuaTable GET = new LuaTable();
		HashMap<String, Object> GETMap = page.req.getGET();
		Object[] GETKeys = GETMap.keySet().toArray();
		for(int i = 0; i < GETKeys.length; i++){
			Object keyVal = GETMap.get(GETKeys[i]);
			if(keyVal instanceof NoEntry){
				GET.insert(GET.length() + 1, LuaValue.valueOf(GETKeys[i].toString()));
			}else if(keyVal instanceof String){
				GET.set(GETKeys[i].toString(), LuaString.valueOf((String)keyVal));
			}else if(keyVal instanceof Integer){
				GET.set(GETKeys[i].toString(), LuaNumber.valueOf((int)keyVal));
			}else if(keyVal instanceof Double){
				GET.set(GETKeys[i].toString(), LuaNumber.valueOf((double)keyVal));
			}else if(keyVal instanceof Boolean){
				GET.set(GETKeys[i].toString(), LuaBoolean.valueOf((boolean)keyVal));
			}else{
				GET.set(GETKeys[i].toString(), CoerceJavaToLua.coerce(keyVal));
			}
		}
		lua.set("GET", GET);
		return lua;
	}
}