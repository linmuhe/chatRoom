package com.toudu98.chat.message;

import java.util.List;

import com.toudu98.chat.domain.ImageUpload;
import com.toudu98.chat.socket.ChatC.AllCode;


public class ChatImage extends ChatText {
	@Override
	public String toString() {
		return "ChatImage [Text=" +  Text + "]" + "code : "+getCode();
	}
	private List<ImageUpload> Text;

	public List<ImageUpload> getText() {
		return Text;
	}

	public void setText(List<ImageUpload> images) {
		Text = images;
	}
	{
		setCode(AllCode.getImgUrl);
	}
	protected void setCode(int code) {
		// TODO Auto-generated method stub
		super.setCode(code);
	}
	protected void name() {
		
	}
}
