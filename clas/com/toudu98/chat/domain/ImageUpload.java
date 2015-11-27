package com.toudu98.chat.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.auth.BCSSignCondition;
import com.baidu.inf.iis.bcs.http.HttpMethodName;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.model.X_BS_ACL;
import com.baidu.inf.iis.bcs.request.GenerateUrlRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;

public class ImageUpload {
	static final String host = "bcs.duapp.com";
	static final String accessKey = "ZFxDNFhI3WE6hrRrpcFfXPjf";
	static final String secretKey = "VcuMcI99utyAE98EVBsM6DaG3lbH99IN";
	static final String bucket = "lacr-chatimage";
	static BaiduBCS baiduBCS;
	static {
		BCSCredentials credentials = new BCSCredentials(accessKey, secretKey);
		baiduBCS = new BaiduBCS(credentials, host);
		// baiduBCS.setDefaultEncoding("GBK");
		baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
	}
	private String ff;// 文件名的扩展名
	private String fh;// 文件名前缀名

	public String getFf() {
		return ff;
	}

	public void setFf(String ff) {
		this.ff = ff;
	}

	@Override
	public String toString() {
		return "ImageUpload [ff=" + ff + ", fh=" + fh + ", writeObject="
				+ writeObject + "]";
	}

	public String getFh() {
		return fh;
	}

	public void setFh(String fh) {
		this.fh = fh;
	}
	//取得返回值 fh : URL 
	public ImageUploadUrl getReault(String Channel){
		return new ImageUploadUrl(fh,getUrl(Channel));
	} 
	public String getUrl(String channelId) {
		this.writeObject = "/"+channelId + "/" + fh + ff;
		createObject();
		setPublicRead();
		return generateUrl();
	}

	private String writeObject = "";

	private void createObject() {
		InputStream bdsinputstream = new ByteArrayInputStream(new byte[0]);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(0);
		PutObjectRequest request = new PutObjectRequest(bucket,
				this.writeObject, bdsinputstream, objectMetadata);
		 baiduBCS.putObject(request);
	
	}

	private void setPublicRead() {
		baiduBCS.putObjectPolicy(bucket, this.writeObject, X_BS_ACL.PublicRead);
	}
	
	private  String  generateUrl() {
		GenerateUrlRequest generateUrlRequest = new GenerateUrlRequest(HttpMethodName.PUT, bucket, this.writeObject);
		generateUrlRequest.setBcsSignCondition(new BCSSignCondition());
		generateUrlRequest.getBcsSignCondition().setSize(5000000L);
		return baiduBCS.generateUrl(generateUrlRequest);
	}
}
