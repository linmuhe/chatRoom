package com.toudu98.www.websocket.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.toudu98.www.websocket.LaWebSocket;
import com.toudu98.www.websocket.impl.FrameDraft.FRAME_OPCODE;
import com.toudu98.www.websocket.inter.FrameData;
import com.toudu98.www.websocket.utils.Hex;

public class FrameDataImpl  extends FrameDraft implements FrameData{
	private FrameDraft fd;
	//private boolean first=true;
	private ByteArrayOutputStream os=null;
	{
		init();
	}
	public FrameDataImpl(FrameDraft fd) {
		this.fd=fd;
	}
	@Override
	public int getFin() {
		// TODO Auto-generated method stub
		return fd.getFin();
	}
	@Override
	public void setText() {
		if(getOpcode()!=FRAME_OPCODE.PENDING){
		fd.setText();
		}
	}
	@Override
	public void setBinary() {
		if(getOpcode()!=FRAME_OPCODE.PENDING){		fd.setBinary();}
	}
	@Override
	public FRAME_OPCODE getOpcode() {
		// TODO Auto-generated method stub
		return fd.getOpcode();
	}
	private void init(){
		os=new ByteArrayOutputStream();
	}
	public void write(byte[] d){
		if(os==null){
			init();
		}
		try {
			BufferedOutputStream bos=new BufferedOutputStream(os);
			bos.write(d);
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void clearData(){
		os=null;
	}
	/* (non-Javadoc)
	 * @see com.toudu98.www.websocket.inter.FrameData#getData()
	 * 
	 */
	public byte[] getData(){
		return os.toByteArray();
	}


	/* (non-Javadoc)
	 * @see com.toudu98.www.websocket.inter.FrameData#getPacketData()
	 */
	/* (non-Javadoc)
	 * @see com.toudu98.www.websocket.inter.FrameData#getPacketData()
	 * after call setText/setBinary setOnce and setFirstSend
	 * if has error will return null
	 */
	public byte[] getPacketData()  {
		/*try {
			System.out.println(new String(getData(),"utf-8"));
		
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	
		// TODO Auto-generated method stub
		if(getOpcode()==FRAME_OPCODE.UNKOWN){
			throw new UnkownSendDataTypeException("未知的数据类型");
		}
		String FirstByteString=(getFin()==1 ? "1" :"0" ) + "000" ;
		
		if(getOpcode() != FRAME_OPCODE.PENDING){
			FirstByteString+=isBinary(getOpcode()) ? "0002": "0001";
		}else{
			FirstByteString+="0000";
			
		}
		ByteArrayOutputStream bb=new ByteArrayOutputStream();
		bb.write(Hex.int2OneByte(Integer.parseInt(FirstByteString, 2)));
		
		int m=0;
		int dlen=getData().length;
		LaWebSocket.logs.debug("要发送的数据长度："+dlen);
		if(dlen>0 && dlen <=125){
			bb.write(Hex.int2OneByte(m << 8 | dlen));
		}else if (dlen < Integer.MAX_VALUE >> 16 ){
			bb.write(Hex.int2OneByte(126));
			bb.write(Hex.int2OneByte( (dlen & 0xff00) >>8 ) );
			bb.write( Hex.int2OneByte(dlen & 0xff ));
		}else{
			bb.write(Hex.int2OneByte(127));
			try {
				bb.write(Hex.long2Bytes(dlen));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	
		try {
			bb.write(getData());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return bb.toByteArray();
	}
	/**
	 * 是否一次就能传输完 不要分次
	 */
	public void setOnce(boolean b){
		fd.setFin(b ? 1:0);
	}
	public void setFirstSend(boolean b) {
		// TODO Auto-generated method stub
		if(!b){
			fd.setOpcode(OPCODE_PENDING);
		}
	}
	public void write(String text) {
		// TODO Auto-generated method stub
		try {
			write(text.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
