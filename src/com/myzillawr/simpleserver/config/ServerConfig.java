package com.myzillawr.simpleserver.config;

public abstract class ServerConfig {
	public abstract boolean isThreaded();
	public abstract int getPort();
}