package com.myzillawr.simpleserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import com.myzillawr.simpleserver.http.HttpRequest;

public class HttpRequestHandler extends Thread{
	public Socket client;
	public BufferedReader in;
	public BufferedWriter out;
	
	public HashMap<String, Object> _server;
	
	private SimpleServer server;
	
	public HttpRequestHandler(Socket client, SimpleServer simpleServer) throws IOException{
		this.client = client;
		this.server = simpleServer;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		
		//Init Server Variables
		_server = new HashMap<String, Object>();
		_server.put("SERVER_ADDR", server.getServerDefaults().get("SERVER_ADDR"));
		_server.put("SERVER_SOFTWARE", server.getServerDefaults().get("SERVER_SOFTWARE"));
		_server.put("REQUEST_TIME", (int)(System.currentTimeMillis() / 1000L));
		_server.put("REQUEST_TIME_FLOAT", System.currentTimeMillis());
		String[] remoteaddr = client.getRemoteSocketAddress().toString().substring(1).split(":");
		_server.put("REMOTE_ADDR", remoteaddr[0]);
		_server.put("REMOTE_PORT", remoteaddr[1]);
		
		String s;
		while((s = in.readLine()) != null){
			if(s.isEmpty()){
				break;
			}
			String su = s.toUpperCase();
			if(su.startsWith("GET") || su.startsWith("POST")){
				String[] req = s.split(" ");
				_server.put("REQUEST_METHOD", req[0]);
				_server.put("REQUEST_URI", req[1]);
				_server.put("SERVER_PROTOCOL", req[2]);
			}else{
				String[] brokenUp = s.split(": ");
				if(brokenUp.length > 0){
					String httpVal = "";
					if(brokenUp.length > 1){
						httpVal = brokenUp[1];
					}
					_server.put("HTTP_" + brokenUp[0].toUpperCase().replace("-", "_"), httpVal);
				}
			}
		}
		if(_server.get("REQUEST_URI") != null){
			String queryString = (String)_server.get("REQUEST_URI");
			if(queryString.indexOf("?") != -1){
				queryString = queryString.substring(queryString.indexOf("?") + 1);
				int indexOf = queryString.indexOf("/");
				if(indexOf != -1){
					queryString = queryString.substring(0, indexOf);
				}
				_server.put("QUERY_STRING", queryString);
			}
		}else{
			close();
		}
		if(!_server.containsKey("QUERY_STRING")){
			_server.put("QUERY_STRING", "");
		}
	}
	
	//Multi-Try/Catch in case out or in throws an exception.
	public void close(){
		try{
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		try{
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		try{
			client.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		if(_server.get("REQUEST_URI") != null){
			String page = "/";
			String pageTemp = (String)_server.get("REQUEST_URI");
			int qPos = pageTemp.indexOf("?");
			if(qPos != -1){
				pageTemp = pageTemp.substring(0, qPos);
			}
			if(pageTemp.startsWith("/")){
				pageTemp = pageTemp.substring(1);
			}
			String[] pagePart = pageTemp.split("/");
			String endBit = null;
			for(int i = 0; i < pagePart.length; i++){
				if(pagePart[i] != ".." && pagePart[i] != "../" && pagePart[i] != "..\\" && pagePart[i] != "/.." && pagePart[i] != "\\.." && pagePart[i] != "/../" && pagePart[i] != "\\..\\"){
					endBit = pagePart[i];
					if(new File(server.getDocumentRoot(), page + pagePart[i]).exists()){
						page = page + pagePart[i];
					}else{
						break;
					}
				}
			}
			page = page.replace("/", "\\");
			if(endBit != null){
				if(page.endsWith("\\")){
					page = page + endBit;
				}
			}
			File servePage = new File(server.getDocumentRoot().getAbsolutePath() + page);
			if(servePage.isDirectory()){
				servePage = new File(servePage, "index.lua");
			}
			new HttpRequest(this, servePage);
		}
		close();
	}

	public String dateFormat(Date date){
		return server.dateFormat(date);
	}
}