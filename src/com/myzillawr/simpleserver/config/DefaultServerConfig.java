package com.myzillawr.simpleserver.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DefaultServerConfig extends ServerConfig{
	private int port;
	private boolean threaded;
	private String documentRoot;
	
	//TODO: Load default config from file, if file exists.
	public DefaultServerConfig(){
		this(80, false, "/www");
	}
	
	public DefaultServerConfig(File configFile){
		if(configFile.exists()){
			try{
				BufferedReader br = new BufferedReader(new FileReader(configFile));
				String s = null;
				Integer tport = null;
				Boolean tthreaded = null; 
				String tdocumentRoot = null;
				try{
					while((s = br.readLine()) != null){
						if(s.startsWith("port:")){
							s = s.substring(5);
							if(s.startsWith(" ")){
								s = s.substring(1);
							}
							tport = Integer.parseInt(s);
						}
						if(s.startsWith("threaded:")){
							s = s.substring(9);
							if(s.startsWith(" ")){
								s = s.substring(1);
							}
							tthreaded = Boolean.parseBoolean(s);
						}
						if(s.startsWith("documentroot:")){
							s = s.substring(13);
							if(s.startsWith(" ")){
								s = s.substring(1);
							}
							tdocumentRoot = s;
						}
					}
				}catch (IOException e){
					e.printStackTrace();
				}
				if(tport != null){
					port = tport;
				}else{
					port = 80;
				}
				if(tthreaded != null){
					threaded = tthreaded;
				}else{
					threaded = false;
				}
				if(tdocumentRoot != null){
					documentRoot = tdocumentRoot;
				}else{
					documentRoot = "/www";
				}
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}else{
			port = 80;
			threaded = false;
			documentRoot = "/www";
		}
	}
	
	public DefaultServerConfig(int port, boolean threaded, String documentRoot){
		this.port = port;
		this.threaded = threaded;
		this.documentRoot = documentRoot;
	}
	
	@Override
	public int getPort(){
		return port;
	}

	@Override
	public boolean isThreaded(){
		return threaded;
	}

	@Override
	public String getDocumentRoot(){
		return documentRoot;
	}
}