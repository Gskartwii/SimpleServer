package com.myzillawr.simpleserver.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import com.myzillawr.simpleserver.SimpleServer;

public class HttpRequestHandler extends Thread{
	public Socket client;
	public BufferedReader in;
	public BufferedWriter out;
	
	public HashMap<String, Object> _server;
	public HashMap<String, Object> _get;
	public HashMap<String, Object> _post;
	
	private SimpleServer server;
	
	private byte[] postData = null;
	public String postReturn = null;
	
	public HttpRequestHandler(Socket client, SimpleServer simpleServer){
		this.client = client;
		this.server = simpleServer;
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
		try{
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
			_server.put("DOCUMENT_ROOT", server.getServerDefaults().get("DOCUMENT_ROOT"));
			
			_get = new HashMap<String, Object>();
			_post = new HashMap<String, Object>();
			
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
				if(su.startsWith("CONTENT-LENGTH: ")){
					try{
						int contentLength = new Integer(su.substring(16)).intValue();
						if(contentLength > 0){
							postData = new byte[contentLength];
							client.getInputStream().read(postData);
						}
					}catch(Exception e){
						e.printStackTrace();
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
			if(((String)_server.get("QUERY_STRING")).length() > 0){
				parseQueryString((String)_server.get("QUERY_STRING"), _get);
			}
			if(postData != null){
				try{
					postReturn = new String(postData);
				}catch(Exception e){
					e.printStackTrace();
					postReturn = "";
				}
				if(_server.get("HTTP_CONTENT_TYPE") != null){
					String contentType = (String)_server.get("HTTP_CONTENT_TYPE");
					if(contentType.equalsIgnoreCase("application/x-www-form-urlencoded")){
						parseQueryString(postReturn, _post);
					}	
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		if(_server.get("REQUEST_URI") != null){
			File servePage = server.getDocumentRoot();
			String pageTemp = (String)_server.get("REQUEST_URI");
			int qPos = pageTemp.indexOf("?");
			if(qPos != -1){
				pageTemp = pageTemp.substring(0, qPos);
			}
			if(pageTemp.startsWith("/")){
				pageTemp = pageTemp.substring(1);
			}
			String[] pagePart = pageTemp.split("/");
			for(int i = 0; i < pagePart.length; i++){
				if(pagePart[i] != ".." && pagePart[i] != "../" && pagePart[i] != "..\\" && pagePart[i] != "/.." && pagePart[i] != "\\.." && pagePart[i] != "/../" && pagePart[i] != "\\..\\"){
					if(new File(servePage, pagePart[i]).exists()){
						servePage = new File(servePage, pagePart[i]);
					}else{
						break;
					}
				}
			}
			new HttpRequest(this, servePage);
		}
		if(!client.isClosed() || !client.isInputShutdown() || !client.isOutputShutdown()){
			close();
		}
	}
	
	private void parseQueryString(String qString, HashMap<String, Object> map){
		String[] getSplices = qString.split("\\&");
		for(int i = 0; i < getSplices.length; i++){
			int iOf = getSplices[i].indexOf("=");
			if(iOf != -1){
				String key = getSplices[i].substring(0, iOf);
				String val = getSplices[i].substring(iOf, getSplices[i].length());
				if(key.length() > 0){
					map.put(key, val);
				}
			}else{
				map.put(getSplices[i], new NoEntry());
			}
		}
	}

	public String dateFormat(Date date){
		return server.dateFormat(date);
	}

	public String[] getIndexNames(){
		return server.getIndexNames();
	}

	public int getServerPort(){
		return server.getServerPort();
	}
	
	public static final class NoEntry{}
}