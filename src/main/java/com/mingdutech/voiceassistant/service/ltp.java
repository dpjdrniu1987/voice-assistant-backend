package com.mingdutech.voiceassistant.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class ltp {
	// webapi接口地址
	private static final String WEBTTS_URL1 = "http://ltpapi.xfyun.cn/v1/cws";
	private static final String WEBTTS_URL2 = "http://ltpapi.xfyun.cn/v1/pos";
	private static final String WEBTTS_URL3 = "http://ltpapi.xfyun.cn/v1/ner";
	// 应用ID
	private static final String APPID = "5d7f2c5e";
	// 接口密钥
	private static final String API_KEY = "c807e5054c6f1b65944cfc634915e20a";
	// 文本
	//private static final String TEXT = "我就是试一试这个词法分析接口";
	

	private static final String TYPE = "dependent";

	/**
	 * 组装http请求头
	 */
	public static Map<String, String> buildHttpHeader() throws UnsupportedEncodingException {
		String curTime = System.currentTimeMillis() / 1000L + "";
		String param = "{\"type\":\"" + TYPE +"\"}";
		String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
		String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		header.put("X-Param", paramBase64);
		header.put("X-CurTime", curTime);
		header.put("X-CheckSum", checkSum);
		header.put("X-Appid", APPID);
		return header;
	}

	/*public static void main(String[] args) throws IOException {
		System.out.println(TEXT.length());
		Map<String, String> header = buildHttpHeader();
		String result1 = HttpUtil.doPost1(WEBTTS_URL1, header, "text=" + URLEncoder.encode(TEXT, "utf-8"));
		String result2 = HttpUtil.doPost1(WEBTTS_URL2, header, "text=" + URLEncoder.encode(TEXT, "utf-8"));
		String result3 = HttpUtil.doPost1(WEBTTS_URL3, header, "text=" + URLEncoder.encode(TEXT, "utf-8"));
		System.out.println("itp 接口调用结果（分词）：" + result1);
		System.out.println("itp 接口调用结果（词性标注）：" + result2);
		System.out.println("itp 接口调用结果（实体识别）：" + result3);
	}*/
}
