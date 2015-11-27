package com.toudu98.chat.message;

import com.toudu98.chat.socket.ChatC.AllCode;

public class ChatMess extends ChatText{
//	public  final static String  MESS="mess";
	private String userNick;
	public String getUserNick() {
		return userNick;
	}
	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}
	public String getMess() {
		return mess;
	}
	{
		super.setCode(AllCode.receiveMsg);
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	private String mess;
}
