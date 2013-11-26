package com.myzillawr.simpleserver;

import java.io.File;

import com.myzillawr.simpleserver.config.DefaultServerConfig;

public class Main {
	public static void main(String[] args) throws Exception{
		SimpleServer serv = new SimpleServer();
		serv.setConfig(new DefaultServerConfig(new File("server.config")));
		serv.start();
	}
}