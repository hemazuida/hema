package com.serialport.apis;

import java.io.UnsupportedEncodingException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PortAnalyse {

	private Context mContext;

	public PortAnalyse(Context ctx) {
		mContext = ctx;
	}

	public void freePortAnalyse() {
		mContext = null;
	}

	public void AnalyseRecvData(byte[] buffer, int size) {

		Intent intent = new Intent("CUS.OPEN");
		String s = "";
		try {
			s = new String(buffer, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		intent.putExtra("buffer", s);
		mContext.sendBroadcast(intent);
		Log.d("test", "AnalyseRecvData :size=" + size + ",buffer=" + buffer);
	}

	public static int bytes2Int(byte[] b) {
		int ret = 0;
		for (int i = 0; i < b.length; i++) {
			ret += b[i] & 0xFF;
		}
		return ret;
	}

	private byte[] HexString2Bytes(String src) {

		byte[] tmp = src.getBytes();
		int bytes = tmp.length / 2;

		byte[] ret = new byte[bytes];

		for (int i = 0; i < bytes; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	private byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}
}
