package com.toudu98.www.websocket.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.sql.rowset.WebRowSet;

import com.toudu98.www.service.LaServer;
import com.toudu98.www.websocket.LaWebSocket;
import com.toudu98.www.websocket.ProtocalSocketCallBack;
import com.toudu98.www.websocket.inter.ApplicationContent;
import com.toudu98.www.websocket.inter.FrameData;
import com.toudu98.www.websocket.inter.MessageHandler;
import com.toudu98.www.websocket.utils.Logs;

public class ServerContent implements ApplicationContent {
	private static ServerContent serverContent_instance=null;
	private static Long OnlineCount=(long) 0;
	
	public void AddOnlineOne(boolean t){//true 加 false 减
		synchronized (OnlineCount) {
			long at =t ? ++OnlineCount : --OnlineCount;
			Logs.getLogger().debug("online number  is "+OnlineCount);
		}
	}
	public long getOnlineCount(){
		return OnlineCount;
	}
	public  static ServerContent  getInstance() {
		if(serverContent_instance==null){
			serverContent_instance=new ServerContent();
		}
		return serverContent_instance;
		// TODO Auto-generated constructor stub
	}
	public static HashMap<String, Session> getSessions() {
		return sessions;
	}
	
	private static  HashMap<String, Session > sessions=new HashMap<String,Session >();
	private Set<SocketInfo> allSocketInfo=  Collections.synchronizedSet(new HashSet<SocketInfo>());
	public ThreadLocal<String > Sessid=new ThreadLocal<String>();
	public MessageHandler getMessageHandler (){
		return ProtocalSocketCallBack.threadMessageHandler.get();
	} 
	private static WriteLock lock_write ;
	private static  ReadLock lock_read;
	{
		ReentrantReadWriteLock sessrl=new ReentrantReadWriteLock();
		 lock_write=sessrl.writeLock();
		lock_read=sessrl.readLock();
		
	}
	public boolean findSocketInfo(Socket s){
		return allSocketInfo.contains(new SocketInfo(s));
	}
	public void addSocketInfo(SocketInfo si){
		allSocketInfo.add(si);
	}
	private ServerContent() {
		super();
		//启动一条维护线程 指定时间执行
	}
	public SocketInfo getSocketInfo(){
		if(LaServer.get().getSocket()!=null){
		return getSocketInfo(LaServer.get().getSocket());
		}else{
			return null;
		}
	}
	public SocketInfo getSocketInfo(Socket s){
	
			SocketInfo mysi=new SocketInfo(s);
			for (SocketInfo si : allSocketInfo) {
				if(si.equals(mysi)){
					return si;
				}
			}
		
			return null;
	}
	public void addSession(Session s){
		lock_write.lock();	
		sessions.put(s.getSessionId(), s);
		lock_write.unlock();
	}
	/**
	 * @param s
	 * 将该SOCKET的所有信息删除
	 */
	public void socketDestory(Socket s){
		lock_write.lock();
		SocketInfo si=getSocketInfo(s);
		Session sess=getSession(si.getSessionId());
		Logs.getLogger().debug(sess);
				synchronized (getSession()) {
					if(sess!=null){sess.removeSocketInfo(si);
				}
				
				Logs.getLogger().debug("remove socketinfo ");
				
				}
		lock_write.unlock();
	
	
		allSocketInfo.remove(si);
	}
	public void writeData(FrameData fd) {
		// TODO Auto-generated method stub
	
			getSocketInfo().sendData(fd);
		
	}

	public void flush() {
		try {
			getSocketInfo().getSocket().getOutputStream().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			getMessageHandler().onError(e,this);
		}

	}


	public Session getSession(String id) {
		try{
			lock_read.lock();
			return sessions.get(id);
		}finally{
			lock_read.unlock();
		}
		// TODO Auto-generated method stub
	}

	public HashMap<String,Session> getAllSession() {
		// TODO Auto-generated method stub
		return sessions;
	}
	public Session getSession() {
		// TODO Auto-generated method stub
		return getSession(Sessid.get());
	}
	public FrameData newFrameData() {
		// TODO Auto-generated method stub
		return new FrameDataImpl(new FrameDraft());
	}
	public static void  clearSession(){
        //清除过期SESSION 				
		for (Entry<String, Session> sis : sessions.entrySet()) {
			lock_write.lock();
			Session ses =	sis.getValue();
			if(ses.getConnectCount() == 0){
			 LaWebSocket.logs.debug("remove session :" +sis.getKey() );
			 sessions.remove(sis.getKey());
			}
		 lock_write.unlock();
		}
	}
}
