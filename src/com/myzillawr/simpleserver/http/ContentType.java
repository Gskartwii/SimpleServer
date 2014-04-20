package com.myzillawr.simpleserver.http;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.myzillawr.simpleserver.http.page.ContentPage;
import com.myzillawr.simpleserver.http.page.ImagePage;
import com.myzillawr.simpleserver.http.page.Page;
import com.myzillawr.simpleserver.http.page.lua.FullLuaPage;
import com.myzillawr.simpleserver.http.page.lua.MixedLuaPage;

public class ContentType {
	private static HashMap<String, String> cTypes;
	static{
		cTypes = new HashMap<String, String>();
		//Lua (Can be changed using the header() function.)
		cTypes.put(".lua", "text/html");
		cTypes.put(".lhtm", "text/html");
		cTypes.put(".lhtml", "text/html");
		
		//HTML
		cTypes.put(".html", "text/html");
		cTypes.put(".htm", "text/html");
		
		cTypes.put(".txt", "text/plain");
		cTypes.put(".css", "text/css");
		cTypes.put(".js", "text/javascript");
		cTypes.put(".xml", "text/xml");
		
		//IMAGES
		cTypes.put(".gif", "image/gif");
		cTypes.put(".jpeg", "image/jpeg");
		cTypes.put(".jpg", "image/jpeg");
		cTypes.put(".png", "image/png");
		cTypes.put(".svg", "image/svg");
		
		cTypes.put("DEFAULT CONTENT TYPE", "text/plain");
	}
	
	private static HashMap<String, Class<? extends Page>> pageTypes;
	static{
		pageTypes = new HashMap<String, Class<? extends Page>>();
		//Lua
		pageTypes.put(".lua", FullLuaPage.class);
		pageTypes.put(".lhtm", MixedLuaPage.class);
		pageTypes.put(".lhtml", MixedLuaPage.class);
		
		//HTML
		pageTypes.put(".html", ContentPage.class);
		pageTypes.put(".htm", ContentPage.class);
		
		pageTypes.put(".txt", ContentPage.class);
		pageTypes.put(".css", ContentPage.class);
		pageTypes.put(".js", ContentPage.class);
		pageTypes.put(".xml", ContentPage.class);
		
		//IMAGES
		pageTypes.put(".gif", ImagePage.class);
		pageTypes.put(".jpeg", ImagePage.class);
		pageTypes.put(".jpg", ImagePage.class);
		pageTypes.put(".png", ImagePage.class);
		pageTypes.put(".svg", ImagePage.class);
		
		pageTypes.put("DEFAULT TYPE", ContentPage.class);
	}
	
	public static String getContentType(File page){
		String[] keys = cTypes.keySet().toArray(new String[cTypes.keySet().size()]);
		for(int i = 0; i < keys.length; i++){
			if(page.getName().toLowerCase().endsWith(keys[i])){
				return cTypes.get(keys[i]);
			}
		}
		return cTypes.get("DEFAULT CONTENT TYPE");
	}

	public static Page handlePage(HttpRequest httpRequest) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		String[] keys = cTypes.keySet().toArray(new String[cTypes.keySet().size()]);
		for(int i = 0; i < keys.length; i++){
			if(httpRequest.getFile().getName().toLowerCase().endsWith(keys[i])){
				return pageTypes.get(keys[i]).getConstructor(HttpRequest.class).newInstance(httpRequest);
			}
		}
		return pageTypes.get("DEFAULT TYPE").getConstructor(HttpRequest.class).newInstance(httpRequest);
	}
}