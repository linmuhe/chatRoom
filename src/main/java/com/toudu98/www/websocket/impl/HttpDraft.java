package com.toudu98.www.websocket.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.toudu98.www.websocket.LaWebSocket;
import com.toudu98.www.websocket.utils.Base64;
import com.toudu98.www.websocket.utils.Charsetfunctions;
import com.toudu98.www.websocket.utils.QueryParameters;

public class HttpDraft {
	private QueryParameters Parameter;

	public QueryParameters getParameter() {
		return Parameter;
	}

	private class Hkv {
		private String key;
		private String value;
	}

	private final Hkv kv = new Hkv();

	private Hkv resolveKey(String line) {
		String k[] = line.split(":", 2);
		kv.key = k[0];
		kv.value = k[1].trim();
		return kv;
	}

	public static HttpDraft parseHttpDraft(byte[] b, int i, int len) {

		HttpDraft hd = new HttpDraft();
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(b,i,len)));// new BufferedInputStream(is)
		// DataInputStream br=new DataInputStream(is);//readLine();
		
		LaWebSocket.logs.debug("准备读客户发来的数据-这是一个HTTP头");
		try {
			line = br.readLine();

			if (line == null) {
				// System.out.println("ddddddd");
				return null;
			}
			LaWebSocket.logs.debug(line);
			String[] line1 = line.split(" ");
			boolean flag = false;
			if (line1.length == 3) {
				if (line1[0].toUpperCase().equals("GET")
						&& line1[1].startsWith("/")) {
					if (line1[2].toUpperCase().startsWith("HTTP/")) {

						hd.setUrl(line1[1]);
						flag = true;
						QueryParameters qp = new QueryParameters(line1[1]);
						hd.setParameter(qp);
					}
				}
			}
			if (!flag) {
				return null;
			}
			flag = false;// connnection
			boolean Upgrade = false, key = false;
			while ((line = br.readLine()) != null) {
				if ("".equals(line)) {
					break;
				}
				LaWebSocket.logs.debug(line);

				String k = hd.resolveKey(line).key;
				if ("Host".equals(k)) {
					hd.setHost(hd.kv.value);
				} else if ("Connection".equals(k)
						&& (hd.kv.value.contains("Upgrade"))) {
					flag = true;
				} else if ("Upgrade".equals(k)
						&& "websocket".toLowerCase().equals(
								hd.kv.value.toLowerCase())) {
					Upgrade = true;
				} else if ("Origin".equals(k)) {
					hd.setOrigin(hd.kv.value);
				} else if ("Sec-WebSocket-Key".equals(k)) {
					key = true;
					hd.setSeckey(hd.kv.value);
				} else if ("Cookie".equals(k)) {
					String s[] = hd.kv.value.split(";");
					HashMap<String, String> hsc = new HashMap<String, String>();
					for (String cookie : s) {
						cookie = cookie.trim();
						String[] cookiekv = cookie.split("=", 2);
						hsc.put(cookiekv[0], cookiekv[1]);
					}
					hd.setCookie(hsc);
				} else if ("Sec-WebSocket-Protocol".equals(k)) {
					hd.setProtocol(hd.kv.value);
				}
			}
			// br.close();
			if (flag && key && Upgrade) {
				return hd;
			} else {
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LaWebSocket.logs.warn("read http header had a error");
			e.printStackTrace();
			
		}
		return null;
	}

	public void setParameter(QueryParameters parameter) {
		Parameter = parameter;
	}

	private String url;
	private String Host;
	private String Origin;
	private String seckey;
	private String protocol;
	private HashMap<String, String> cookie=new HashMap<String, String>();
	static public String PROTOCAL_NORMAL = "ws://";
	static public String PROTOCAL_SSL = "wss://";

	public enum CONNECT_STATE {
		CLOSED(403), OPEN(101);
		public int status;

		CONNECT_STATE(int v) {
			this.status = v;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.valueOf(status);
		}
	}

	@Override
	public String toString() {
		return "HttpDraft [Parameter=" + Parameter + ", url=" + url + ", Host="
				+ Host + ", Origin=" + Origin + ", seckey=" + seckey
				+ ", protocol=" + protocol + ", cookie=" + cookie + "]";
	}

	public String getSeckey() {
		return seckey;
	}

	public void setSeckey(String seckey) {
		this.seckey = seckey;
	}

	public HashMap<String, String> getCookie() {
		return cookie;
	}

	public String getOrigin() {
		return Origin;
	}

	public void setOrigin(String origin) {
		Origin = origin;
	}

	public String getHost() {
		return Host;
	}

	public void setHost(String host) {
		Host = host;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param Seckey
	 * @return webSocket要返回的协议密钥
	 */
	private String getAcceptKey(String Seckey) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
			md.update((Seckey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
					.getBytes());
			LaWebSocket.logs.debug(Seckey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
			byte[] b = md.digest();
			String s = Base64.encode(b);
			return s;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
		/* 258EAFA5-E914-47DA-95CA-C5AB0DC85B11 */
	}

	private String writeCookie = "";;// 需要向浏览器写的COOKIE//

	private String cookie_header() {

		if (Host == null || "".equals(Host)) {
			Host = "localhost";
		}
		String hostname = Host.replaceAll("(\\w+)(:\\d+)?", "$1");
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis((c.getTimeInMillis()+LaWebSocket.SessionSaveTime));
		String expires = c.getTime().toGMTString();
		return " domain=" + hostname + "; expires=" + expires + "; path=/";
	}

	public void addCookie(String key, String val) {
		// System.out.println(hostname); header name is Set-Cookie
		this.writeCookie += (key + "=" + val + "; ");
		// return null;
	}

	public byte[] createHandShakeResponseHeader(CONNECT_STATE status) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.1 " + status.status + " Switching Protocols");
		sb.append("\r\n");
		sb.append("Upgrade: WebSocket");
		sb.append("\r\n");
		sb.append("Connection: Upgrade");
		sb.append("\r\n");
		if (!("".equals(protocol)) && protocol != null) {
			sb.append("Sec-WebSocket-Protocol: " + protocol);
			sb.append("\r\n");
		}
		sb.append("Sec-WebSocket-Accept: " + getAcceptKey(getSeckey()));
		sb.append("\r\n");
		sb.append("Sec-WebSocket-Origin: " + Origin);
		sb.append("\r\n");
		sb.append("Sec-WebSocket-Location: " + PROTOCAL_NORMAL + Host + url);
		sb.append("\r\n");
		sb.append("Server:LaServer OpenSSL/1.0.1c webSocket");
		sb.append("\r\n");
		if (!this.writeCookie.isEmpty()) {
			sb.append("Set-Cookie: " + this.writeCookie + cookie_header());
			sb.append("\r\n");
		}
		sb.append("Access-Control-Allow-Credentials: true");
		sb.append("\r\n");
		sb.append("\r\n");
		// System.out.println(sb.toString());
		return Charsetfunctions.asciiBytes(sb.toString());
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @param hsc
	 *            存放从浏览器拿到的COOKIE
	 */
	public void setCookie(HashMap<String, String> hsc) {
		// TODO Auto-generated method stub
		this.cookie = hsc;
	}


}
