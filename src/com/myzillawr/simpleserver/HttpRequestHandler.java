package com.myzillawr.simpleserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

public class HttpRequestHandler extends Thread{
	private Socket client;
	private BufferedReader in;
	private BufferedWriter out;
	
	//TODO: Add PATH_INFO
	private HashMap<String, Object> _server;
	
	public HttpRequestHandler(Socket client, HashMap<String, Object> serverDefaults) throws IOException{
		this.client = client;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		
		//Init Server Variables
		_server = new HashMap<String, Object>();
		_server.put("SERVER_ADDR", serverDefaults.get("SERVER_ADDR"));
		_server.put("SERVER_SOFTWARE", serverDefaults.get("SERVER_SOFTWARE"));
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
		close();
	}
}