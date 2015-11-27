package com.toudu98.www.websocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.toudu98.www.service.LaServer;
import com.toudu98.www.service.inter.SocketAcceptCallBack;
import com.toudu98.www.websocket.impl.RequestClassPath;
import com.toudu98.www.websocket.inter.DecodeReader;
import com.toudu98.www.websocket.inter.Message;
import com.toudu98.www.websocket.utils.LoadClass;
import com.toudu98.www.websocket.utils.Logs;

public class LaWebSocket {
	public static long SessionSaveTime =3600*24*1000*7;
	public static Log logs;
	static{
		logs=Logs.getLogger();
	}
//	public ApplicationContent appcontent;
private	static ConfigContext cc;
	public static void main(String[] args) {
		cc=new ConfigContext();
		LaWebSocket lws=new LaWebSocket(406);
		
		/* 读完配制信息 后 启动 */
		lws.bootstrap(cc);
	}
	LaServer laserver;
	
	LaWebSocket(int port){
	
		try {
			Logs.getLogger().debug(Thread.currentThread().getContextClassLoader().getResource("").getPath());
			Logs.getLogger().debug(ClassLoader.getSystemClassLoader().getResource("").getPath());
			Logs.getLogger().debug(getClass().getClassLoader().getResource("").getPath());
			Logs.getLogger().debug(getClass().getClassLoader().getResourceAsStream("log4j.properties"));
			
Logs.getLogger().debug(new File(".").getAbsolutePath());//new file 是从当前执行目录找的文件 比如你在/a/c目录下运行 那么相对文件就在里面找
//用CLASSLOADER的GETRESOURCE 是从classpath目录下找的 也就是存放生成的类的目录 如果打成JAR 会先从JAR里找 然后如果在从配制了MANIFESR的CLASSPATH里找

			Properties p=new Properties();
			p.load(getClass().getClassLoader().getResourceAsStream("./LaWebSocket_lib/la.properties"));
			LoadClass.addLoadClassPath("clas");
			//扫描所有的包 反射注解
			Set<Class<?>> c=LoadClass.getClasses(p.getProperty("packageName"));
			
			for (Class<?> class1 : c) {
				logs.debug(class1);
				com.toudu98.www.websocket.anno.LaWebSocket ca=class1.getAnnotation(com.toudu98.www.websocket.anno.LaWebSocket.class);
				
			
			
				if(ca!=null){
					logs.debug(ca.value() + "--"+ ca.url());
					String requestPath=ca.url().isEmpty() ? ca.value() : ca.url();
					if(!requestPath.isEmpty()){
						cc.app.put(requestPath, new RequestClassPath(class1));
					}
				}
			}
			
			logs.debug(c.size()+" we will need "+cc.app.size());
			
			logs.debug(cc.app);
			//开启开源域名限制
			String[] domains=p.getProperty("domainList").split(",");
			for (String string : domains) {
				cc.addOriginDomain(string.trim());
			}
			cc.setDomainOpened(p.getProperty("domainOpen").trim().equals("1") ? true :false);
			cc.setDomainWhite(p.getProperty("domainWhite").trim().equals("1") ? true :false);
			logs.debug(cc.domainSet);
//			cc.properties=p;
			port = Integer.parseInt(p.getProperty("port").trim());
			LaWebSocket.SessionSaveTime=Integer.parseInt(p.getProperty("sessionday").trim())*3600*24*1000 ;
			this.laserver=LaServer.bind(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	private SocketAcceptCallBack sac;
	void bootstrap(ConfigContext cc){
		//这个方法会执行在主线程里完成后程序不会往下走 需要把它放在新线程里
		sac=new ProtocalSocketCallBack(cc);
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				laserver.Start(sac);
			}
		}.start();
	}
	
}
