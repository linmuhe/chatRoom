package com.toudu98.chat.message;

import com.toudu98.www.websocket.inter.Message;

public class ChatText implements Message {
	public static String CODE="code";
	 private int code=0;
	 public int getCode() {
		return code;
	}
	 protected void setCode(int code) {
		this.code = code;
	}

	 
}
