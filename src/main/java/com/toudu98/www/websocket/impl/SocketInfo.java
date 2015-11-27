package com.toudu98.www.websocket.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.toudu98.Lencode.Lencode81;
import com.toudu98.www.websocket.ProtocalSocketCallBack;
import com.toudu98.www.websocket.inter.FrameData;
import com.toudu98.www.websocket.inter.MessageHandler;

public class SocketInfo implements Comparable<SocketInfo> {
	private Socket socket ;
	public boolean isOpened;
	private String SessionId;
	private HttpDraft RequestInfo;
	public HttpDraft getRequestinfo(){
		return RequestInfo;
	}
	private FrameData receiveData=null;
	private ServerContent content;
	{
		content=ServerContent.getInstance();
	}
	private MessageHandler getMessageHandler(){
		return content.getMessageHandler();
	}
	public void sendData(FrameData data) {
		OutputStream os;
		try {
			os = socket.getOutputStream();os.write(data.getPacketData());
		}
		 catch (IOException e) {
				// TODO Auto-generated catch block
			 MessageHandler	MessageCall =getMessageHandler();
			// if(MessageCall!=null){
			 	isOpened=false;
				 MessageCall.onError(e,content);
		//	 }
		 }
	}
	/**
	 * 一个存放数据的容器 存在于SOCKETINFO里 也就是每个人连接后都有这一个空间呢
	 */
	private ConcurrentHashMap<String, Object> connectedData=new ConcurrentHashMap<String, Object>(new HashMap<String, Object>());
	public void addData(String s,Object o){
		connectedData.put(s, o);
	}
	
	public FrameData getReceiveData() {
		return receiveData;
	}

	public void setReceiveData(FrameData receiveData) {
		this.receiveData = receiveData;
	}

	/**
	 * @return
	 * 拿到他请求连接时候的cookie 
	 */
	public  HashMap<String, String> getRequestCookies() {
		if(RequestInfo==null){return null;}
		return RequestInfo.getCookie();
	}
	public String getRequestPath(){
		if(RequestInfo==null){return null;}
		return RequestInfo.getUrl();
	}
	public void setRequestInfo(HttpDraft requestInfo) {
		RequestInfo = requestInfo;
	}

	public Object getData(String s){
		return connectedData.get(s);
	}
public String getSessionId() {
		return  this.SessionId ;
	}
public Map<String, Object> getconnectedData(){
	return connectedData;
}
	private void setSessionId(String sessionId) {

		SessionId = sessionId;
	}
	public SocketInfo(Socket s){
		this.socket=s;
	}
public SocketInfo(Socket s,String SessionId) {
	// TODO Auto-generated constructor stub
	this.socket=s;
	setSessionId(SessionId);
}
	public Socket getSocket() {
		return socket;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(this==obj){
			return true;
		}
		return obj.hashCode()== this.hashCode();
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return socket.hashCode();
	}
	@Override
	public String toString() {
		return "SocketInfo [socket=" + socket + ", isOpened=" + isOpened
				+ ", SessionId=" + getSessionId() + "]";
	}
	public int compareTo(SocketInfo o) {
		// TODO Auto-generated method stub
	if(this.socket.getPort()	> o.getSocket().getPort()){
		return 1;
	}else{
		return -1;
	}
	}
}
