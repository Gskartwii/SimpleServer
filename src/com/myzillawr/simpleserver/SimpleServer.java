package com.myzillawr.simpleserver;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import com.myzillawr.simpleserver.config.ServerConfig;

public class SimpleServer implements Runnable{
	private ServerConfig config;
	
	private Thread thread;
	private boolean running;
	
	private ServerSocket serv;
	
	public SimpleServer(){
		running = false;
	}
	
	public void start(){
		running = true;
		if(config.isThreaded()){
			thread = new Thread(this);
			thread.setName("Main Server Thread");
			thread.start();
		}else{
			run();
		}
	}

	public void setConfig(ServerConfig config) throws Exception{
		if(!running){
			this.config = config;
		}else{
			throw new Exception("Attempt to set config while running.");
		}
	}
	
	private static String getIp(){
		String ipAddress = null;
		Enumeration<NetworkInterface> net = null;
		try{
			net = NetworkInterface.getNetworkInterfaces();
		}catch(SocketException e){
			throw new RuntimeException(e);
		}
		while(net.hasMoreElements()){
			NetworkInterface element = net.nextElement();
			Enumeration<InetAddress> addresses = element.getInetAddresses();
			while(addresses.hasMoreElements()){
				InetAddress ip = addresses.nextElement();
				if(ip instanceof Inet4Address){
					if(ip.isSiteLocalAddress()){
						ipAddress = ip.getHostAddress();
					}
				}else if(ip instanceof Inet6Address){
					if(ip.isSiteLocalAddress()){
						ipAddress = ip.getHostAddress();
					}
				}
			}
		}
		return ipAddress;
	}

	@Override
	public void run(){
		try{
			serv = new ServerSocket(config.getPort());
		}catch(IOException e){
			e.printStackTrace();
		}
		HashMap<String, Object> serverDefaults = new HashMap<String, Object>();
		serverDefaults.put("SERVER_ADDR", getIp());
		serverDefaults.put("SERVER_SOFTWARE", "Myzilla Web Resources SimpleServer");
		while(true){
			try{
				Socket client = serv.accept();
				new HttpRequestHandler(client, serverDefaults).start();
			}catch (IOException e){
				e.printStackTrace();
			}
			
		}
	}
}