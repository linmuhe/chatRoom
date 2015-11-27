package com.toudu98.www.websocket.inter;

import com.toudu98.www.websocket.impl.UnkownSendDataTypeException;
import com.toudu98.www.websocket.impl.FrameDraft.FRAME_OPCODE;

public interface FrameData {
	
	 /**
	 * @return
	 * 这是一个真实数据 发送前要经过WEBSOCKET协议的处理
	 */
	byte[] getData();
	
	 /**
	 * @return
	 * 将这个数据打包 
	 * @throws UnkownSendDataTypeException 
	 */
	byte[] getPacketData() ;
	void setFirstSend(boolean b);
	 void setText();
	 void setBinary() ;
	
	 void write(byte[] b);
	 void write(String text);
	 void setOnce(boolean b);
	void clearData();
}
