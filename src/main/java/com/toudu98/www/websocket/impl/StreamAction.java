package com.toudu98.www.websocket.impl;

/**
 * @author Muhe
 *
 */
public enum StreamAction {
	/**
	 * 完成握手的动作
	 */
	ResponseHandShakeHeader, 
	
	/**
	 * 调用开发者接口
	 */
	CallHandler,
	
	;
}
