package com.toudu98.www.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Set;

import com.toudu98.Lencode.Lencode81;
import com.toudu98.www.service.inter.SocketAcceptCallBackDefault;
import com.toudu98.www.websocket.impl.DefaultMessageHandler;
import com.toudu98.www.websocket.impl.FrameDataImpl;
import com.toudu98.www.websocket.impl.FrameDraft;
import com.toudu98.www.websocket.impl.HandShakeBase;
import com.toudu98.www.websocket.impl.HttpDraft;
import com.toudu98.www.websocket.impl.HttpDraft.CONNECT_STATE;
import com.toudu98.www.websocket.impl.MaxBinaryLengthException;
import com.toudu98.www.websocket.impl.RequestClassPath;
import com.toudu98.www.websocket.impl.ServerContent;
import com.toudu98.www.websocket.impl.Session;
import com.toudu98.www.websocket.impl.SocketInfo;
import com.toudu98.www.websocket.impl.StreamAction;
import com.toudu98.www.websocket.inter.DecodeReader;
import com.toudu98.www.websocket.inter.Message;
import com.toudu98.www.websocket.utils.Hex;

public class ProtocalSocketCallBack extends SocketAcceptCallBackDefault{
	private ServerContent ServerContent;
	/**
	 * 存放当前线程要调用的开发者接口 一个线程第一次创建的类 在线程没有结束前可以再次使用
	 */
	public static ThreadLocal<DefaultMessageHandler> threadMessageHandler=new ThreadLocal<DefaultMessageHandler>();
	
	{
		this.ServerContent=com.toudu98.www.websocket.impl.ServerContent.getInstance();
	}
	final public static String  NAME_STEING_PASSWD="linmuhe";
	/**
	 * 存放读取完流后 要完成的事件
	 */
	private ThreadLocal<StreamAction> threadLocalAction=new ThreadLocal<StreamAction>();
	private ThreadLocal<FrameDataImpl> threadFrameData=new ThreadLocal<FrameDataImpl>();
	
	private ConfigContext cc;
	private void SetThreadAction(StreamAction s){
		threadLocalAction.set(s);

	}
	/**
	 * @return
	 * 返回当前SOCKET的SOCKETINFO
	 */
	private Session getSession( ){
		
		return ServerContent.getSession(ServerContent.Sessid.get());
	
	}
	private SocketInfo getSocketInfo(){
		return ServerContent.getSocketInfo(getSocket());
	}
	private void removeAction(){
		threadLocalAction.remove();
	}
	private StreamAction getThreadAction(){
		return threadLocalAction.get();
	}
	public ProtocalSocketCallBack(ConfigContext cc) {
		this.cc=cc;
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see com.toudu98.www.task.inter.ReadAcceptByte#read(java.io.InputStream)
	 */
	/* (non-Javadoc)
	 * @see com.toudu98.www.task.inter.ReadAcceptByte#read(java.io.InputStream)
	 */
	 public byte[]  read(InputStream is) throws IOException {
		 LaWebSocket.logs.debug("entry "+"-from:"+Thread.currentThread().getName());
		// TODO Auto-generated method stub
		//读数据 完成handshake or closed  
		////1.看它的SOCKET的SOCKETINFO对象是不是已经完成WebSocket握手
		if(!ServerContent.findSocketInfo(getSocket())){
			//代表是一个新的SOCKET对象连接过来的
			HttpDraft hd=null;
			if((hd=new HandShakeBase().getHttp(is))!=null){
			//	System.out.println(hd);
				//决定是否可以让它访问
				SetThreadAction(StreamAction.ResponseHandShakeHeader);
				if(cc.matchDomain(hd.getOrigin())){
				
					//如果没有 就让浏览器创建SESSION COOKIE 
					String currentSessionId =hd.getCookie().get(cc.getSessionId());
					//由于是第一次握手 要把它加入到全局SOCKETINFO里
					if(currentSessionId==null){
						currentSessionId =  Lencode81.Lencoder(NAME_STEING_PASSWD+hd.getSeckey()) ;
						hd.addCookie(ConfigContext.getSessionId(), currentSessionId);
					}
					SocketInfo si=new SocketInfo(getSocket(),currentSessionId);

					si.setRequestInfo(hd);
					ServerContent.Sessid.set(currentSessionId);
					ServerContent.addSocketInfo(si);
					
					si.isOpened=true;
					//有就加到已经存在的当中 不存在就创建一个
					Session session_handler=ServerContent.getSession(currentSessionId);
					if(session_handler==null){
						//在服务器断保存SESSION 因为是SOCKET连接下次不会把COOKIE发过来
						// 本来想 再下次浏览器发东西来查到了再写的 但那样应该问题很多
						session_handler=new Session(currentSessionId);
						//session_handler 
						ServerContent.addSession(session_handler);
						ServerContent.AddOnlineOne(true);
					}
					session_handler.setDate(new Date());
					synchronized (session_handler) {
						session_handler.addSocketInfo(si);
			}
					
					LaWebSocket.logs.debug(ServerContent.getAllSession());
					
					return hd.createHandShakeResponseHeader(CONNECT_STATE.OPEN);

				}else{
					 run(hd.createHandShakeResponseHeader(CONNECT_STATE.CLOSED));
					 return null;
				}
			
			}else{
				return null;
			}
		}else{
		
			//已经握手的话就向当前线程里面放一个它的SESSION ID 这样每个线程都能通过这个去拿到别的数据
			if(getSocketInfo()!=null){
				ServerContent.Sessid.set(getSocketInfo().getSessionId());
			}else{
				return null;
			}
			
		
			
		
			/////
			LaWebSocket.logs.debug(getSocketInfo().getRequestPath()+"-from:"+Thread.currentThread().getName());
			
			byte[] b=null;
			int len =0;
		//	System.out.println("这是已经握手成功的来源数据--准备读取-- "+getCurrentThreadCount()+Thread.currentThread().getName());		    
			b= new byte[2];
			len=is.read(b);
			if(len!=2 || len==-1){
				return null;
			}
			//开发者接口
			getHandlerCall().onReceive(ServerContent);
			//开始解析协议
			FrameDraft frame=new FrameDraft();
			
			/*try {
				Thread.sleep(400000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			Long dataLength;
			int fin=(b[0] & 0x80 ) >> 7;
			int opcode=b[0] & 0xF;
			
			LaWebSocket.logs.info("opcode is :"+opcode+"SocketInfo" + getSocketInfo());
			if(opcode==FrameDraft.OPCODE_CLOSEING){
				getSocket().close();
				return null;
			}
			int mask=(b[1]  & 0x80 ) >> 7;
			int dataLengthCode =b[1] & 0x7F;
			frame.setFin(fin);
			frame.setMask(mask);
			
			frame.setOpcode(opcode);
			if(dataLengthCode==0){
				LaWebSocket.logs.info("数据长度为0 ---fin:"+fin+"--opcode"+opcode+"mask"+mask);
					//这情况不会发生 应该 防止浏览器发送的数据的长度标识为0
				return "".getBytes();
			}else if(dataLengthCode <=125){
				dataLength = (long) dataLengthCode;
			}else if (dataLengthCode ==126){
				len=0;
				len=is.read(b);
				if(len!=2 || len==-1){
					return null;
				}
				dataLength= (long) (((b[0] & 0xff) << 8 ) | (b[1] & 0xff))  ;
			}else{
				b=new byte[8];
				len=0;
				len=is.read(b);
				if(len!=8 || len==-1){
					return null;
				}
				dataLength=Hex.bytes2Long(b);
			
			}
			
			frame.setPayloadDataLength(dataLength);
			
			if(mask==1){
				//有掩码
				b=new byte[4];
				len=0;
				len=is.read(b);
				if(len!=4 && len!=-1){
					return null;
				}
				int maskingKey=Hex.bytes2Int(b);
				frame.setMaskingKey(maskingKey);
			}
			
			FrameDataImpl data=null;
			try{
				 data=frame.readData(is);
			
				
			//	System.out.println(frame);
				 LaWebSocket.logs.info("-----------------");
					
					/*
					 * 手动封装数据
					 * FrameData newdata =new FrameDataImpl(new FrameDraft());
					 * 
					 * newdata.write("我也爱你12");
					newdata.setText();
					newdata.setOnce(false);
					newdata.setFirstSend(true);
					
					getSocketInfo().sendData(newdata);
				//	newdata.clearData();
					newdata.write("你为什么要离开我");
					newdata.setText();
					newdata.setOnce(false);
					newdata.setFirstSend(false);
					
					newdata.write("那好吧 走就走了吧");
					newdata.setText();
					newdata.setOnce(true);
					newdata.setFirstSend(false);*/
					if(data!=null){
						getSocketInfo().setReceiveData(data);
						//开发者接口
						
						//收到数据后 要做什么
						SetThreadAction(StreamAction.CallHandler);
						threadFrameData.set(data);
						return data.getData();
					
						
					
					}else{
						LaWebSocket.logs.warn(" what happend .data of receive is null");//不会发生 除非受到非法HIT
						return null;
					}
				
			}catch(IOException e){
				throw e;
			} catch (MaxBinaryLengthException e1) {
				getSocket().close();
				e1.printStackTrace();
				return null;
				// TODO Auto-generated catch block
			
			}
			
		
		/*	try {
				Thread.sleep(40000000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			/*System.out.println(getSocketInfo());
			System.out.println(getSession());*/
			
		
	//		return "".getBytes();//不处理 也就是不要让框架调用RUN方法
		}
	
	}
	public void run(byte[] b) {
		if(getThreadAction()==StreamAction.ResponseHandShakeHeader){
			//握手
			LaWebSocket.logs.info("===========以下内容将被发到客户端=====WebSocket握手成功========");
			LaWebSocket.logs.info(new String(b));
				try {
					OutputStream os=getSocket().getOutputStream();
					os.write(b);os.flush();
					//开发者接口
				
				synchronized (getSession()) {
					getHandlerCall().onOpen(ServerContent);
				}
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}else if(getThreadAction()==StreamAction.CallHandler){
			//b can use getSocketInfo().getReceiveData().getData()
			LaWebSocket.logs.debug("will call handler");
synchronized (getSession()) {
	getHandlerCall().onMessage(getDecoder().decode(threadFrameData.get()),ServerContent);
		}
		Session sa=getSession() ;
		if(sa!=null){
			Date now=new Date();
			sa.setDate(now);
		}
		removeAction();
		}		
	}
	/**
	 * @return
	 * 获取要调用的对像
	 */
	private DefaultMessageHandler getHandlerCall(){
		DefaultMessageHandler mdh=threadMessageHandler.get();
		if(mdh!=null){
			
			return threadMessageHandler.get();
		}
		RequestClassPath requestpath =getRequestClassPath();
		try {
			mdh=(DefaultMessageHandler) requestpath.getMessageHandler().newInstance();
			threadMessageHandler.set(mdh);
			
			return mdh;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	/**
	 * @return
	 * 要调用的把字节转为可用对像信息
	 */
	public DecodeReader<Message> getDecoder(){
		RequestClassPath requestpath =getRequestClassPath();
		try {
			return  (DecodeReader<Message>) requestpath.getMessageEncoder().newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	private RequestClassPath getRequestClassPath(){
		String sp=getSocketInfo().getRequestPath();
		Set<String> key=cc.app.keySet();
		String us="";
		for (String ks : key) {
			sp.startsWith(ks);
			if(us.equals("")){
				us=ks;
			}else{
				us= ks.length() > us.length() ? ks : us;
			}
		}
		RequestClassPath requestpath=cc.app.get(us);
		return requestpath;
		
	}
	@Override
	public void connected() {
		// TODO Auto-generated method stub
		//写日志 有人连接
		LaWebSocket.logs.info("有人来了 亲爱的" + getSocket().getPort());
		super.connected();
	}
	
	@Override
	public void closed() {
		if(getSession() !=null && getSession().getConnectCount()==1){
			//Logs.getLogger().debug("remove session ");
			ServerContent.AddOnlineOne(false);
		//	sessions.remove(sess.getSessionId());
		}
		try{
		//开发者接口
synchronized (getSession()) {//这方法里要用到SESSION的所有SOCKINFO作判断 所以要同步 现没有完成之前 不能作修改 这个和上面的握手时添加SOCKINFO 一起起作用
	getHandlerCall().onClose(ServerContent);
}}catch( NullPointerException e){
	return ;
}
		// TODO Auto-generated method stub
		//cosket关闭之前 写日记
		LaWebSocket.logs.info("closed");
		removeAction();
		ServerContent.socketDestory(getSocket());
		super.closed();

	}

}
