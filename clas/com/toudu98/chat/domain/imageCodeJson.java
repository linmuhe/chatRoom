package com.toudu98.chat.domain;

import java.util.ArrayList;

public class imageCodeJson{
	private final static  int code =401;
	ArrayList<ImageUploadUrl> imgs;
	public ArrayList<ImageUploadUrl> getImgs() {
		return imgs;
	}
	public void setImgs(ArrayList<ImageUploadUrl> imgs) {
		this.imgs = imgs;
	}
	public int getCode() {
		return code;
	}
	
}