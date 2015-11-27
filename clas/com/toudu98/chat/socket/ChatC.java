package com.toudu98.chat.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;

import com.alibaba.fastjson.JSONObject;
import com.toudu98.chat.domain.ImageUpload;
import com.toudu98.chat.domain.ImageUploadUrl;
import com.toudu98.chat.domain.imageCodeJson;
import com.toudu98.chat.message.ChatImage;
import com.toudu98.chat.message.ChatInfo;
import com.toudu98.chat.message.ChatMess;
import com.toudu98.chat.message.ChatNick;
import com.toudu98.chat.message.ChatText;
import com.toudu98.chat.message.ChatTextEncoder;
import com.toudu98.www.websocket.anno.LaWebSocket;
import com.toudu98.www.websocket.impl.DefaultMessageHandler;
import com.toudu98.www.websocket.impl.ServerContent;
import com.toudu98.www.websocket.impl.Session;
import com.toudu98.www.websocket.impl.SocketInfo;
import com.toudu98.www.websocket.inter.ApplicationContent;
import com.toudu98.www.websocket.inter.FrameData;
import com.toudu98.www.websocket.inter.Message;
import com.toudu98.www.websocket.utils.Logs;

@LaWebSocket(url = "/chat", decoder = ChatTextEncoder.class)
public class ChatC extends DefaultMessageHandler {
	public static class AllCode {
		public static int setNick = 101;
		public static int receiveMsg = 201;
		public static int getImgUrl=401;
	}
protected void name() {
	
}

/**
 * 存放用户名的KEY in session
 */
	private static final String NICK = "chatNick";
	/**
	 * 存放频道的KEY in socketinfo 
	 */
private static final String CHANNEL ="channel";
	private static Log log = com.toudu98.www.websocket.LaWebSocket.logs;
	private static HashMap<String, Set<SocketInfo>> people; //key 是频道 
	private static HashMap<String, Long> grouptCount;//key 是频道 
	private static ReentrantLock groupLock = new ReentrantLock();
	private static ScheduledExecutorService ses;// // 定时任务 
	static {
		people = new HashMap<String, Set<SocketInfo>>();
		grouptCount = new HashMap<String, Long>();
		ses = Executors.newScheduledThreadPool(0);
		//.清徐SESSION
		ses.scheduleAtFixedRate(new Runnable() {
			public void run() {
				log.debug("a clearSession fixed Runnable is boot");
				ServerContent.clearSession();
			}
		}, 0,com.toudu98.www.websocket.LaWebSocket.SessionSaveTime,TimeUnit.MILLISECONDS);
	}
	private FrameData getFrameData(ApplicationContent c){
		FrameData data=c.newFrameData();
		data.setText();data.setFirstSend(true);data.setOnce(true);
		return data;
	}
	@Override
	 public void onClose(ApplicationContent c) {
		// TODO Auto-generated method stub
		Session s= c.getSession();
		SocketInfo si =s.getSocketInfo();
		if(si==null){
			log.debug("sockinfo had be removed ");
			return ;}
		si.isOpened = false;
//		log.debug(grouptCount.keySet());
		
		int has=0;
		ArrayList<SocketInfo> alls=s.getAllSocketInfo();
		for (SocketInfo socketInfo : alls) {
			if(getChannelName(c).equals((String)socketInfo.getData(CHANNEL))){
					has++;
			}
		}
		if(has ==1 ){
			Long count=grouptCount.get(si.getData(CHANNEL));
			if(count > 0) {
				log.debug(count);
				count--;//锟斤拷前锟斤拷锟斤拷锟斤拷锟斤拷-1
				grouptCount.put((String) si.getData(CHANNEL), count);
				sendGroupInfo(c);
			}
		}
		
	}
	/**
	 * 向这个连接自己 发送频道人数
	 * @param c
	 */
	private void sendGroupInfoToSelf(ApplicationContent c){

		FrameData data=getFrameData(c);
		ChatInfo chatinfo=new ChatInfo();
		Long count=grouptCount.get(c.getSession().getSocketInfo().getData(CHANNEL));
		chatinfo.setOnlineCount(count);
		data.write(JSONObject.toJSONBytes(chatinfo));
		c.getSession().getSocketInfo().sendData(data);
	}
	/**
	 * 锟斤拷锟酵碉拷前锟斤拷锟叫讹拷锟斤拷锟斤拷
	 * @param c
	 */
	private void sendGroupInfo(ApplicationContent c){

		FrameData data=getFrameData(c);
		ChatInfo chatinfo=new ChatInfo();
		Long count=grouptCount.get(c.getSession().getSocketInfo().getData(CHANNEL));
		chatinfo.setOnlineCount(count);
		data.write(JSONObject.toJSONBytes(chatinfo));
		sendToCurGroup(c, data, true);
	}

	/**	 得到当前用户频道的STRING
	 * @return
	 */
	private String getChannelName(ApplicationContent c){
		SocketInfo socketInfo = c.getSession().getSocketInfo();
		return (String)socketInfo.getData(CHANNEL);
	}
	/**
	 * @param c
	 * @return 锟斤拷前SOCKET锟斤拷锟节碉拷频锟斤拷 锟斤拷锟斤拷锟斤拷
	 */
	private Set<SocketInfo> getChannel(ApplicationContent c) {
		Session si=c.getSession();
		SocketInfo sf=si.getSocketInfo();
		if(si ==null || sf==null){
			return null;
		}else{
			String channel = (String) sf.getData(CHANNEL);
			return people.get(channel);
		}
		
	}

	@Override
	public void onError(Exception e, ApplicationContent c) {
	}

	@Override
	public void onMessage(Message msg, ApplicationContent c) {
		// 锟斤拷取锟矫伙拷锟斤拷锟斤拷锟斤拷浅锟�写锟斤拷锟斤拷应锟斤拷SOCKETINFO
		// TODO Auto-generated method stub 锟斤拷锟斤拷息锟斤拷锟斤拷 锟斤拷装锟斤拷锟斤拷 时锟斤拷 然锟斤拷转锟酵碉拷锟斤拷锟斤拷锟斤拷 JSON转锟斤拷
		if (msg == null) {
			log.debug("msg is null big error ");
			return;
		}
		ChatText ct = (ChatText) msg;
		
		int len = String.valueOf(ct.getCode()).length();
		log.debug("code len is "+len);
		if (len == 4) {
			FrameData data = c.newFrameData();
			data.setText();
			data.setFirstSend(true);
			data.setOnce(true);
			if (msg instanceof ChatNick) {
				log.debug("json :" + ((ChatNick) msg).getNick());
				data.write(JSONObject.toJSONString(msg));
				c.getSession().getSocketInfo().sendData(data);
			}
		} else if (len == 3) {
			if (msg instanceof ChatNick) {
				// 锟斤拷锟斤拷锟角筹拷
				String nickname = ((ChatNick) msg).getNick();
				log.debug("set nick ::" + nickname);
				c.getSession().addData(NICK, nickname);

			} else if (msg instanceof ChatMess) {
				SocketInfo 	si=c.getSession().getSocketInfo();
				
				if (c.getSession().getData(NICK) == null) {
					RequestNick(si, c);
					return ;
				}
			
				if(si==null){
					return ;
				}
				
				@SuppressWarnings("unused")
				Object lst=si.getData("lastTime");
			
				if(lst!=null){
					@SuppressWarnings("unused")
					long t = new Date().getTime() - (Long)si.getData("lastTime");
					log.debug(t+"  --delay "+"-from:"+Thread.currentThread().getName());
					if (t < 180) {
						log.debug("too fast ,the socket will be close" );
						try {
							si.getSocket().close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				//设置这次的访问时间 
				c.getSession().getSocketInfo().addData("lastTime", new Date().getTime());
				// 锟秸碉拷锟矫伙拷锟斤拷锟斤拷息 转锟斤拷
				ChatMess cm = (ChatMess) msg;

				cm.setUserNick((String) c.getSession().getData(NICK));

				FrameData data = c.newFrameData();
				data.setText();
				data.setFirstSend(true);
				data.setOnce(true);
				data.write(JSONObject.toJSONBytes(cm));

				sendToCurGroup(c, data, false);

			}else if (msg instanceof ChatImage){
				ChatImage ci = (ChatImage)msg;
				sendImageUploadUrl(ci,c);
			}
		}

	}
	/*发送请求的图片URL上传地址 */
	private void sendImageUploadUrl(ChatImage ci,ApplicationContent c){
		List<ImageUpload> images=ci.getText();
		String Channel =getChannelName(c);
		ArrayList<ImageUploadUrl> allimgs= new ArrayList<ImageUploadUrl>();
		for (ImageUpload imageUpload : images) {
			ImageUploadUrl imageurl  =imageUpload.getReault(Channel);
			allimgs.add(imageurl);
		}
		imageCodeJson imageJson=new imageCodeJson();
		imageJson.setImgs(allimgs);
		String resultImageUrls =JSONObject.toJSONString(imageJson);
		FrameData fd=getFrameData(c);
		fd.write(resultImageUrls);
		c.getSession().getSocketInfo().sendData(fd);
	}
	/**
	 * 锟斤拷前锟矫伙拷锟斤拷锟斤拷频锟斤拷转锟斤拷锟斤拷息
	 * @param c
	 * @param data
	 * @param self
	 */
	private void sendToCurGroup(ApplicationContent c, FrameData data,boolean self) {
		SocketInfo myinfo = c.getSession().getSocketInfo();
		
	
		Set<SocketInfo> curAll = getChannel(c);
		if(curAll==null){
			return ;
		}
		

		groupLock.lock();
		  Set<SocketInfo> needRemove= new HashSet<SocketInfo>();
			for (SocketInfo socketInfo : curAll) {
				if(!socketInfo.isOpened  ){
					needRemove.add(socketInfo);
				}
				if (  (socketInfo==myinfo && !self)) {
					continue;
				}
				socketInfo.sendData(data);
			}
			curAll.removeAll(needRemove);
		groupLock.unlock();
	}

	/**
	 * 锟矫伙拷锟斤拷锟斤拷锟斤拷锟斤拷锟酵硷拷录锟斤拷息
	 * 
	 * @param s
	 * @param group
	 */
	private void addPeopleToGroup(SocketInfo s, String group) {
		log.debug("some bady join :" + group);
		Set<SocketInfo> value = null;
		;
		if (!people.containsKey(group)) {
			value = new HashSet<SocketInfo>();
			people.put(group, value);
		}
		
		s.addData(CHANNEL, group);// 锟矫伙拷锟斤拷锟节碉拷频锟斤拷
		value = people.get(group);
		value.add(s);
	}

	private void RequestNick(SocketInfo s, ApplicationContent c) {
		ChatNick cn = new ChatNick();

		FrameData fd = c.newFrameData();
		fd.setText();
		fd.setFirstSend(true);
		fd.setOnce(true);
		log.debug(JSONObject.toJSONString(cn));
		fd.write(JSONObject.toJSONString(cn));
		s.sendData(fd);
	}

	@Override
	 public void onOpen(ApplicationContent c) {

		// ws://hostname:406/chat?85bdc5719ce26ae6
		Session s = c.getSession();
		// TODO Auto-generated method stub 通锟斤拷ID锟叫憋拷频锟斤拷 锟斤拷锟斤拷应锟斤拷SOCKET锟斤拷锟斤拷锟饺�

		String group = s.getSocketInfo().getRequestPath()
				.replaceAll(".+chat\\?(\\w{16})", "$1");
		if (group == null) {
			try {
				c.getSession().getSocketInfo().getSocket().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}
		addPeopleToGroup(c.getSession().getSocketInfo(), group);
		// 锟斤拷锟矫伙拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�锟斤拷锟饺硷拷锟斤拷锟矫伙拷锟斤拷锟斤拷锟�没锟叫的伙拷锟酵凤拷锟斤拷锟斤拷息 要锟酵伙拷锟斤拷锟斤拷锟斤拷锟街癸拷锟斤拷
		if (NickIsEmpty(c)) {
			RequestNick(s.getSocketInfo(), c);
		}
		Long count=0L ;
		if (!grouptCount.containsKey(group)) {//锟斤拷锟斤拷锟斤拷锟斤拷
			grouptCount.put(group, count);
		}
		log.debug(s.getConnectCount() +" contection in the session ");
		ArrayList<SocketInfo> alls=s.getAllSocketInfo();
		int has=0;
		for (SocketInfo socketInfo : alls) {
			log.debug(socketInfo.getData(CHANNEL).toString());
			if(((String)socketInfo.getData(CHANNEL)).equals(group)){
			has++;
			}
		}
		log.debug(has);
		if(has==1){
			count =grouptCount.get(group);
			count++;
			grouptCount.put(group, count);
			sendGroupInfo(c);
		}else{
			sendGroupInfoToSelf(c);
		}
	
	}
	/**
	 * @param c
	 * @return TRUE 代表NICK是空的
	 */
	private boolean NickIsEmpty(ApplicationContent c){
		return c.getSession().getData(NICK)==null;
	}

}
