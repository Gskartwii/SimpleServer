package com.myzillawr.simpleserver;

import com.myzillawr.simpleserver.config.DefaultServerConfig;

public class Main {
	public static void main(String[] args){
		SimpleServer serv = new SimpleServer();
		serv.setConfig(new DefaultServerConfig());
	}
}