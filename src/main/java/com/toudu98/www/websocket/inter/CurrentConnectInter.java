package com.toudu98.www.websocket.inter;

import java.net.Socket;

public interface CurrentConnectInter {
	 void writeData(FrameData framedata);
	 void flush();
}
