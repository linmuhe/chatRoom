package com.toudu98.www.websocket.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.toudu98.www.websocket.LaWebSocket;
import com.toudu98.www.websocket.utils.Hex;

public class FrameDraft {
	
	@Override
	public String toString() {
		return "FrameDraft [fin=" + fin + ", payloadDataLength="
				+ payloadDataLength + ", mask=" + mask + ", maskingKey="
				+ maskingKey + ", opcode=" + opcode + "]";
	}
	public static enum FRAME_OPCODE{
		BINARY,TEXT,CLOSEING,PENDING,UNKOWN;
	}
	/**
	 * 结束帧
	 */
	private int fin;
	/**
	 * 数据长度
	 */
	private Long payloadDataLength;
	/**
	 * 是否用掩码
	 */
	private int mask;
	/**
	 * 掩码值
	 */
	private int maskingKey;
	
	public int getFin() {
		return fin;
	}
	/**
	 * @param fin
	 * 设置是否这一次可以传完 相当于调用 setonce
	 */
	public void setFin(int fin) {
		this.fin = fin;
	}
	public boolean isPending(){
		return FrameDraft.isPending(this.opcode);
	}
	public static boolean isPending(FRAME_OPCODE f){
		
		return f==FRAME_OPCODE.PENDING;
	}
	public Long getPayloadDataLength() {
		return payloadDataLength;
	}
	public void setPayloadDataLength(Long payloadDataLength) {
		this.payloadDataLength = payloadDataLength;
	}
	public int getMask() {
		return mask;
	}
	public void setMask(int mask) {
		this.mask = mask;
	}
	public int getMaskingKey() {
		return maskingKey;
	}
	public void setMaskingKey(int maskingKey) {
		this.maskingKey = maskingKey;
	}
	public FRAME_OPCODE getOpcode() {
		return opcode;
	}
	public static final int OPCODE_TEXT=1;
	public static final int OPCODE_BINARY=2;
	public static final int OPCODE_CLOSEING=8;
	public static final int OPCODE_PENDING=0;
	public static final int OPCODE_UNKOWN=404;
	public void setOpcode(int opcode) {
		FRAME_OPCODE f = null;
		switch (opcode) {
		case OPCODE_TEXT:
			f=FRAME_OPCODE.TEXT;
			break;
		case OPCODE_BINARY:
			f=FRAME_OPCODE.BINARY;break;
		case OPCODE_CLOSEING:
			f=FRAME_OPCODE.CLOSEING;break;
		case OPCODE_PENDING:
			f=FRAME_OPCODE.PENDING;break;
		default:
			f=FRAME_OPCODE.UNKOWN;
			break;
		}
		this.opcode = f;
	}
	public static boolean isText(FRAME_OPCODE a){
		return a==FRAME_OPCODE.TEXT;
	}
	public static boolean isBinary(FRAME_OPCODE a){
		
		return a==FRAME_OPCODE.BINARY;
	}

	/**
	 * 相当于SetOpCode
	 */
	public void setText(){
		if(getOpcode()!=FRAME_OPCODE.PENDING){
		this.opcode=FRAME_OPCODE.TEXT;
		}
	}
	/**
	 * 相当于SetOpCode
	 */
	public void setBinary(){
		if(getOpcode()!=FRAME_OPCODE.PENDING){
		this.opcode=FRAME_OPCODE.BINARY;}
	}

	/**
	 * 数据类型
	 */
	private FRAME_OPCODE opcode=FRAME_OPCODE.UNKOWN;
	/**
	 * @param is
	 * @param fd
	 * @return 根据浏览器发来的信息获取数据
	 */
	/*public  static FrameDataImpl  readData(InputStream is,FrameDraft fd) throws IOException,MaxBinaryLengthException{
		FrameDataImpl data=new FrameDataImpl( fd);
		if(fd.getFin()==1 && fd.getOpcode()!=FRAME_OPCODE.PENDING){
			data.write(new SocketData().getForFinOne(is, fd));//可以一次性接收
		}else{
			byte[] bd= new SocketData().getForFinOne(is, fd);//可以一次性接收
			data.write(bd);
		}
		data.setText(FrameDraft.isText(fd.getOpcode()));
		return data;
	}*/
	/**
	 * @param is
	 * @param fd
	 * @return 根据浏览器发来的信息获取数据
	 */
	public FrameDataImpl readData(InputStream is) throws IOException, MaxBinaryLengthException{
		FrameDataImpl data=new FrameDataImpl(this);
		if(this.getFin()==1 && this.getOpcode()!=FRAME_OPCODE.PENDING){
			data.write(new SocketData().getForFinOne(is, this));//可以一次性接收
		}else{
			byte[] bd= new SocketData().getForFinOne(is, this);//可以一次性接收
			data.write(bd);
		}
		return data;
	}
	private class SocketData{
			public byte[] getForFinOne(InputStream is, FrameDraft fd) throws  IOException,MaxBinaryLengthException{
				LaWebSocket.logs.debug(fd);
				long len=fd.getPayloadDataLength();
				int readlen = 0;
				if(len >0 && len < Integer.MAX_VALUE && fd.getOpcode() != FRAME_OPCODE.UNKOWN){
					readlen = (int) len;
				}else{
					new MaxBinaryLengthException("data is too big ,more than the max");
				}

				
				byte[] k =Hex.int2Bytes(fd.getMaskingKey());
				
				byte[] b=new byte[readlen]; 
				BufferedInputStream bis=new BufferedInputStream(is);
				readlen=bis.read(b, 0, readlen);
				LaWebSocket.logs.debug("read data lines is :"+readlen);
				if(fd.getMask()==1){
					for(int i=0;i<readlen;i++){
						b[i]=(byte) ( b[i] ^ k[i%4] );
					}
				}
				return b;
			}
		}
}

