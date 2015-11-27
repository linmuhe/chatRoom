package com.toudu98.www.websocket.utils;

public class Hex {
    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] fromHex(String string) {
        byte[] result = new byte[string.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(string.substring(i * 2, (i * 2) + 2), 16);
        }
        return result;
    }
    public static byte[] int2Bytes(int num) {
		byte[] byteNum = new byte[4];
		for (int ix = 0; ix < 4; ++ix) {
			int offset = 32 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}

	public static int bytes2Int(byte[] byteNum) {
		int num = 0;
		for (int ix = 0; ix < byteNum.length; ++ix) {
			num <<= 8;
			num |= (byteNum[ix] & 0xff);
			if(ix==3){
				break;
			}
		}
		return num;
	}

	public static byte int2OneByte(int num) {
		return (byte) (num & 0x000000ff);
	}
	public static int oneByte2Int(byte byteNum) {
		   return byteNum > 0 ? byteNum : (128 + (128 + byteNum));  
	}  
	  
	public static byte[] long2Bytes(long num) {  
	    byte[] byteNum = new byte[8];  
	    for (int ix = 0; ix < 8; ++ix) {  
	        int offset = 64 - (ix + 1) * 8;  
	        byteNum[ix] = (byte) ((num >> offset) & 0xff);  
	    }  
	    return byteNum;  
	}  
	  
	public static long bytes2Long(byte[] byteNum) {  
	    long num = 0;  
	    for (int ix = 0; ix < byteNum.length; ++ix) {  
	        num <<= 8;  
	        num |= (byteNum[ix] & 0xff);
	        if(ix==7){
	        	break;
	        }
	    }  
	    return num;  
	}  
	
}

	