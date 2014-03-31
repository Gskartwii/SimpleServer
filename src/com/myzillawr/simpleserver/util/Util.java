package com.myzillawr.simpleserver.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.myzillawr.simpleserver.http.HttpRequest;

public class Util {
	public static String fileToString(HttpRequest req) throws Exception{
		String content = null;
		try{
			content = new String(fileToBytes(req), "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		return content;
	}
	
	public static byte[] fileToBytes(HttpRequest req) throws Exception{
		File file = req.getFile();
		byte[] bytes = new byte[(int)file.length()];
		DataInputStream dataIs = new DataInputStream(new FileInputStream(file));
		dataIs.readFully(bytes);
		dataIs.close();
		return bytes;
	}
	
	public static int lineCount(String content){
		return content.split("\n").length;
	}
}