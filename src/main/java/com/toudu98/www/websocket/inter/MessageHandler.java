package com.toudu98.www.websocket.inter;

import com.toudu98.www.websocket.impl.Session;

public interface MessageHandler {
	 /*
	  * onmessage 之前 消息还没有解析的时候 
	 * @param c
	 */
	void onReceive(ApplicationContent c);
	 /**
	  * 消息解析完成 后调用
	 * @param msg
	 * @param c
	 * 
	 */
	void onMessage(Message msg,ApplicationContent c);
	

	 /**
	 * SOCKEt关闭或断开之前
	 * @param session TODO
	 */
	void onClose(ApplicationContent c);
	 /**
	 * SOCKET连接成功后
	 * @param session TODO
	 */
	void onOpen(ApplicationContent c);
	void onError(Exception e,ApplicationContent c);

}
