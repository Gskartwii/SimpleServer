package com.myzillawr.simpleserver.config;

public abstract class ServerConfig {
	public abstract boolean isThreaded();
	public abstract String getDocumentRoot();
	public abstract int getPort();
	public abstract String[] getIndexNames();
}