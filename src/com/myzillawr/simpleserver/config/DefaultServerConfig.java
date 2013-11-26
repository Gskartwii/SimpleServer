package com.myzillawr.simpleserver.config;

public class DefaultServerConfig extends ServerConfig{
	private int port;
	
	//TODO: Load default config from file, if file exists.
	public DefaultServerConfig(){
		port = 80;
	}
	
	@Override
	public int getPort(){
		return port;
	}

	@Override
	public boolean isThreaded(){
		return false;
	}
}