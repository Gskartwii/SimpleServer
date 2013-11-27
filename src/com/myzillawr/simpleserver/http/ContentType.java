package com.myzillawr.simpleserver.http;

import java.io.File;
import java.util.HashMap;

public class ContentType {
	private static HashMap<String, String> cTypes;
	static{
		cTypes = new HashMap<String, String>();
		//Lua
		cTypes.put(".lua", "text/html"); //This one can be changed using the header() function.
		
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
	
	public static String getContentType(File page){
		String flower = page.getName().toLowerCase();
		String[] keys = cTypes.keySet().toArray(new String[cTypes.keySet().size()]);
		for(int i = 0; i < keys.length; i++){
			if(flower.endsWith(keys[i])){
				return cTypes.get(keys[i]);
			}
		}
		return cTypes.get("DEFAULT CONTENT TYPE");
	}
}