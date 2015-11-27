package com.toudu98.chat.message;

import com.toudu98.chat.socket.ChatC.AllCode;

public class ChatNick extends ChatText {
//	public final static String NICK="nick";
	private String nick;
	{
		setCode(AllCode.setNick);
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
}
