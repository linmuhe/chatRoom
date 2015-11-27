package com.toudu98.chat.domain;
public class ImageUploadUrl{
	private String fh ;
	private String url ;
	public String getFh() {
		return fh;
	}
	public ImageUploadUrl(String fh, String url) {
		super();
		this.fh = fh;
		this.url = url;
	}
	public void setFh(String fh) {
		this.fh = fh;
	}
	@Override
	public String toString() {
		return "ImageUploadUrl [fh=" + fh + ", url=" + url + "]";
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
} 