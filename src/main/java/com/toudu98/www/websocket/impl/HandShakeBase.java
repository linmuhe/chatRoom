package com.toudu98.www.websocket.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class HandShakeBase {
	public HttpDraft getHttp(InputStream is) throws IOException{
		byte[] b=new byte[1024];
		int len =is.read(b, 0, b.length);
		return HttpDraft.parseHttpDraft(b,0,len);
	}
}
