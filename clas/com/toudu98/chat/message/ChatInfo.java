package com.toudu98.chat.message;

public class ChatInfo extends ChatText{
	/**
	 * ��ǰ��������
	 */
	private long onlineCount;

	public long getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(long onlineCount) {
		this.onlineCount = onlineCount;
	}
	{
		setCode(301);
	}
}
