package com.toudu98.www.websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.toudu98.www.websocket.impl.RequestClassPath;

public class ConfigContext {
	public Properties properties;
	@SuppressWarnings("rawtypes")
	public HashMap<String,RequestClassPath> app =new HashMap<String,RequestClassPath>();
	/**
	 * @param 可以连接的域名
	 */
	public void addOriginDomain(String domain){
		domainSet.add(domain);
	}
	private final static String SessionId="LaSerSid";
	
	public static String getSessionId() {
		return SessionId;
	}
	
	/**
	 * 来源主机白名单  或是黑名单
	 */
	public boolean domainWhite=false;
	/**
	 * 是否启用来源验证
	 */
	public boolean domainOpened=false;
	public boolean isDomainOpened() {
		return domainOpened;
	}
	public void setDomainOpened(boolean domainOpened) {
		this.domainOpened = domainOpened;
	}
	public HashSet<String> getDomainSet() {
		return domainSet;
	}
	public void setDomainSet(HashSet<String> domainSet) {
		this.domainSet = domainSet;
	}
	public boolean isDomainWhite() {
		return domainWhite;
	}
	/**
	 * @param domainWhite
	 * 	设置为白名单 F为黑名单 
	 */
	public void setDomainWhite(boolean domainWhite) {
		this.domainWhite = domainWhite;
	}
	/**
	 * 来源主机域名列表
	 */
	public HashSet<String> domainSet=new HashSet<String>();
	public boolean matchDomain(String origin){
		if(!isDomainOpened()){
			return true;
		}
		boolean flag=false;
		for (String domain : domainSet) {
			
			 domain="((http:\\/\\/)?(\\w*\\.)*)?"+Pattern.quote(domain);
			Pattern p=Pattern.compile(domain);
			Matcher m=p.matcher(origin);
			if(m.find()){
				flag=true;break;
			}
		}
		//黑名单 
		if(!domainWhite)
			{
				return flag? false: true ;
			}else{
				return flag ? true : false;
			}
	}

}
