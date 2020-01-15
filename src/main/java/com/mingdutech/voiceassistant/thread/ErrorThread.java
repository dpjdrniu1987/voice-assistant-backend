package com.mingdutech.voiceassistant.thread;

import com.mingdutech.voiceassistant.service.SpeechTranscriberDemo;

import javax.websocket.Session;

import java.io.IOException;

import static com.mingdutech.voiceassistant.service.SpeechTranscriberDemo.asrResult;

public class ErrorThread extends Thread {
    public Session session;
    public SpeechTranscriberDemo service;
    public static volatile boolean exit = false;
    public ErrorThread(Session session, SpeechTranscriberDemo service){this.session=session; this.service=service;}
    @Override
    public  void  run(){
        while (!exit) {
            if(service.asrResult.getError() != null){
                try {
                    this.session.getBasicRemote().sendText(service.asrResult.getError() + " 请接入麦克风后重试");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        this.interrupt();
    }
}
