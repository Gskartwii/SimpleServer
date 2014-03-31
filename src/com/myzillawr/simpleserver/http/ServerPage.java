package com.myzillawr.simpleserver.http;

public class ServerPage {
	public static String getHTML(String title, String body, HttpRequest req){
		return "<DOCTYPE html>" + '\n'
			+ "<html>" + '\n'
			+ "<head>" + '\n'
			+ '\r' + "<title>" + title + "</title>" + '\n'
			+ "</head>" + '\n'
			+ "<body>" + '\n'
			+ body + '\n'
			+ "<hr/>" + '\n'
			+ "SimpleServer HTTP Server on port " + req.getServerPort() + '\n'
			+ "</body>" + '\n'
			+ "</html>";
	}
}