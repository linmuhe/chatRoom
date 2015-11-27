package com.toudu98.chat.message;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toudu98.chat.domain.ImageUpload;
import com.toudu98.chat.socket.ChatC;
import com.toudu98.chat.socket.ChatC.AllCode;
import com.toudu98.www.websocket.LaWebSocket;
import com.toudu98.www.websocket.impl.FrameDataImpl;
import com.toudu98.www.websocket.inter.DecodeReader;
import com.toudu98.www.websocket.inter.Message;

public class ChatTextEncoder implements  DecodeReader<Message>{

	public ChatText decode(byte[] arg0) {
		// ��Ϣ���� 
		// TODO Auto-generated method stub
	
		ChatText ct=null;
		try {
			String sourtext=new String(arg0,"utf-8");
		
				//sourtext=JSONObject.quote(sourtext);
			//	LaWebSocket.logs.debug(j);
			
				
				JSONObject json=JSONObject.parseObject(sourtext);
			if( json.getIntValue(ChatText.CODE)!=0 ){
				int code=json.getIntValue(ChatText.CODE);
				LaWebSocket.logs.debug("receive code is :"+ code);
				LaWebSocket.logs.debug(json);
				if(code==AllCode.setNick){//�û������ǳ�
					 ct= JSONObject.parseObject(sourtext,ChatNick.class);
					 if( ((ChatNick)ct).getNick()==null  || ((ChatNick)ct).getNick().trim().length() < 1 || ((ChatNick)ct).getNick().trim().length() > 20){
						 throw new Exception();
					 }
				}else if(code==AllCode.receiveMsg){
					 ct = JSONObject.parseObject(sourtext,ChatMess.class);
					 if( ((ChatMess)ct).getMess()==null  || ((ChatMess)ct).getMess().trim().equals("")){
						 throw new Exception();
					 }
				}else if(code==AllCode.getImgUrl){
					ct=JSONArray.toJavaObject(json, ChatImage.class);
					LaWebSocket.logs.debug("upload image list ");
					LaWebSocket.logs.debug(ct);
				}
			}
			
			
		} catch (Exception e) {
			LaWebSocket.logs.debug("error paese json ");
			e.printStackTrace();
			if(ct==null){
				return new ChatText();
			}
		/*	String sourtext=new String(arg0,"utf-8");
			int code =JSONObject.parseObject(sourtext).getInteger(ChatText.CODE);*/
		
			if(ct.getCode()==AllCode.setNick){
				
				((ChatNick)ct).setNick("failed the nick ");
			}
			ct.setCode(1010);
			// TODO Auto-generated catch block
		}
		if(ct==null){
			ct= new ChatText();
			
		}
		
		return ct;
		
	}

	public ChatText decode(FrameDataImpl f) {
		if(f.getFin()==1){
			return decode(f.getData());
		}
		// TODO Auto-generated method stub
		ChatText ct=new ChatText();
		ct.setCode(1010);
		return ct;
	}

}
