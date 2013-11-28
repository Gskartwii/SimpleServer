package com.myzillawr.simpleserver.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.myzillawr.simpleserver.http.HttpRequest;

public class Util {
	public static String fileToString(HttpRequest req) throws Exception{
		String content = "";
		String s = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(req.getFile())));
		while((s = br.readLine()) != null){
			content = content + s;
			if(!s.isEmpty()){
				content = content + "\n";
			}
		}
		if(content.endsWith("\n")){
			content = content.substring(0, content.length() - 1);
		}
		br.close();
		return content;
	}
	
	public static int lineCount(String content){
		return content.split("\n").length;
	}
}