package com.toudu98.www.websocket.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Session  {
	private  ArrayList<SocketInfo> socketInfo=new ArrayList<SocketInfo>(); //操作这个SESSION 都要作同步( 添加 删除 或是拿里面的数据来作判断)
	private String userOnlyId="";//session id
	private Date date=new Date();//上次访问时间
	private ConcurrentHashMap<String, Object> sessionData=new ConcurrentHashMap<String, Object>(new HashMap<String, Object>());
	private ReentrantLock rl=new ReentrantLock();
	public void addData(String s,Object o){
		sessionData.put(s, o);
	}
	public Date getDate(){
		return date;
	}
	public void setDate(Date da){
		this.date=da;
	}
	public Object getData(String s){
		return sessionData.get(s);
	}
	/**
	 * @return 放在该SESSION里的 所有数据 
	 */
	public Map<String, Object> getSessionData(){
		return sessionData;
	}
	private ServerContent content;
	{
		content=ServerContent.getInstance();
	}
	public boolean isOpen(){
		return content.getSocketInfo().isOpened;
	}
	/**
	 * @return 当前连接的一个SOCKET INFO
	 */
	public SocketInfo getSocketInfo(){
		return content.getSocketInfo();
	}
	public String getSessionId(){
		return this.userOnlyId;
	}
	public void removeSocketInfo(SocketInfo si){
			rl.lock();
				socketInfo.remove(si);
				rl.unlock();
	}
	public Session(String indexid) {
		this.userOnlyId=indexid;
	}
	/**
	 * @return 当前会话的用户连了几个SOCKET
	 */
	public int getConnectCount(){
		return socketInfo.size();
	}
	public void addSocketInfo(SocketInfo si){
		rl.lock();
		socketInfo.add(si);
		rl.unlock();
	}
	
	public ArrayList<SocketInfo> getAllSocketInfo(){
		return socketInfo;
	}
	@Override
	public String toString() {
		return "Session [socketInfo=" + socketInfo + ", userOnlyId="
				+ userOnlyId + ", date=" + date + ", sessionData="
				+ sessionData + "]";
	}
	
}
