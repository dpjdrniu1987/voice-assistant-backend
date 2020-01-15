package com.mingdutech.voiceassistant.thread;

import com.mingdutech.voiceassistant.service.SpeechTranscriberDemo;

import static com.mingdutech.voiceassistant.service.SpeechTranscriberDemo.asrResult;

public class AsrThread extends Thread {
    public  SpeechTranscriberDemo service;
    public static volatile boolean exit = false;
    public AsrThread(SpeechTranscriberDemo service){this.service=service;}
    @Override
    public  void  run(){
        //String filepath = "C:\\Users\\Administrator\\Downloads\\nls-sdk-java-demo\\新建文本文档 (2)1.wav";
        //service.process(filepath);
        while (true) {
            service.micProcess();
            System.out.println("连接断开");
            asrResult.setError("未找到麦克风");
            break;
        }
        service.shutdown();
        service = null;
        this.interrupt();
    }
}
