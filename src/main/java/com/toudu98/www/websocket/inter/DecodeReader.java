package com.toudu98.www.websocket.inter;

import com.toudu98.www.websocket.impl.FrameDataImpl;

public interface DecodeReader<T extends Message>{
	T decode(byte[] b);
	T decode(FrameDataImpl f);
}
