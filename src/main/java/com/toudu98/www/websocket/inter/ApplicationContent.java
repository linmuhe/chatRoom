package com.toudu98.www.websocket.inter;

import java.util.HashMap;

import com.toudu98.www.websocket.impl.Session;
public interface ApplicationContent extends CurrentConnectInter{
	//在这个上下文里应该可以获取当前连接会话的所有信息 还可以向此会员写数据 这里的方法给开发者去用 
	Session getSession();//用户会话 代表当前用户会话信息 

	Session getSession(String id);
	HashMap<String,Session> getAllSession();
	FrameData newFrameData();
	long getOnlineCount();
}
