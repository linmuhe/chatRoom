package com.toudu98.www.websocket.impl;


public class RequestClassPath {
	private Class<?> messageHandler;
	private String path;
	private Class<?> messageEncoder;
	public RequestClassPath(Class<?> msghander) {
		messageHandler=msghander;
		
		com.toudu98.www.websocket.anno.LaWebSocket ca=msghander.getAnnotation(com.toudu98.www.websocket.anno.LaWebSocket.class);
		messageEncoder=ca.decoder();
		path=ca.url().equals("/") ? ca.value() :ca.url();
	}
	
	
	public Class<?> getMessageHandler() {
		return messageHandler;
	}


	public Class<?> getMessageEncoder() {
		return messageEncoder;
	}
	
	@Override
	public String toString() {
		return "RequestClassPath [cls=" + messageHandler + ", path=" + path
				+ ", encodecls=" + messageEncoder + "]";
	}
	
	
}
