package com.myzillawr.simpleserver;

import java.io.ByteArrayInputStream;

public class StringInputStream extends ByteArrayInputStream{
	public StringInputStream(String content){
		super(content.getBytes());
	}
}