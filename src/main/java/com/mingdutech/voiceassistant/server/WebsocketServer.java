/*
package com.mingdutech.voiceassistant.server;

import com.mingdutech.voiceassistant.service.HttpUtil;
import com.mingdutech.voiceassistant.service.LtpResultUtil;
import com.mingdutech.voiceassistant.service.SpeechTranscriberDemo;
import com.mingdutech.voiceassistant.service.ltp;
import com.mingdutech.voiceassistant.thread.AsrThread;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.mingdutech.voiceassistant.service.SpeechTranscriberDemo.asrResult;
import static com.mingdutech.voiceassistant.service.SpeechTranscriberDemo.transcriberExit;
import static com.mingdutech.voiceassistant.service.ltp.buildHttpHeader;

@Slf4j
//@ServerEndpoint("/websocket/{user}")
@ServerEndpoint(value = "/assistant")
@Component
public class WebsocketServer {
    // ltp webapi接口地址
    private static final String WEBTTS_URL1 = "http://ltpapi.xfyun.cn/v1/cws";
    private static final String WEBTTS_URL2 = "http://ltpapi.xfyun.cn/v1/pos";
    private static final String WEBTTS_URL3 = "http://ltpapi.xfyun.cn/v1/ner";
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebsocketServer> webSocketSet = new CopyOnWriteArraySet<WebsocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    public static volatile boolean sendExit = false;

    */
/**
     * 连接建立成功调用的方法*//*

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage("智能语音助手连接成功");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
        //群发消息
        //for (WebsocketServer item : webSocketSet) {

        //}
    }
    //	//连接打开时执行
    //	@OnOpen
    //	public void onOpen(@PathParam("user") String user, Session session) {
    //		currentUser = user;
    //		System.out.println("Connected ... " + session.getId());
    //	}

    */
/**
     * 连接关闭调用的方法
     *//*

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());

    }

    */
/**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*//*

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("来自客户端的消息:" + message);
        */
/*if(message.equals("end")){
            transcriberExit = true;
            System.out.println("语音助手连接断开");
            try {
                sendMessage("转写结束。");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendExit = true;
        }*//*

        if (message.equals("start")) {
            try {
                SpeechTranscriberDemo service = new SpeechTranscriberDemo("EYC3jBEPhZpNAQYI", "LTAI4FrV5FvnThRvw3sLVKXp", "1AWGdkVpXojce3ltXTFEx3yjpYWiib", "");
                sendMessage("转写开始");
                AsrThread asrThread = new AsrThread(service);
                asrThread.start();
                String middleText = "";
                sendExit = false;
                while (!sendExit) {
                    if (service.asrResult.getText() != null && !service.asrResult.getText().equals(middleText)) {
                        sendMessage(service.asrResult.getText());
                        middleText = service.asrResult.getText();
                    }
                    Thread.sleep(100);
                    if (service.asrResult.getFinalText() != null) {
                        sendMessage(service.asrResult.getFinalText());
                        sendMessage("本次语音识别结束!");

                        ltp ltpservice = new ltp();
                        Map<String, String> header = ltpservice.buildHttpHeader();
                        String result1 = HttpUtil.doPost1(WEBTTS_URL1, header, "text=" + URLEncoder.encode(service.asrResult.getFinalText(), "utf-8"));
                        String result2 = HttpUtil.doPost1(WEBTTS_URL2, header, "text=" + URLEncoder.encode(service.asrResult.getFinalText(), "utf-8"));
                        //String result3 = HttpUtil.doPost1(WEBTTS_URL3, header, "text=" + URLEncoder.encode(service.asrResult.getFinalText(), "utf-8"));
                        System.out.println("ltp 接口调用结果（分词）：" + result1);
                        System.out.println("ltp 接口调用结果（词性标注）：" + result2);
                        //System.out.println("ltp 接口调用结果（实体识别）：" + result3);
                        //LtpResultUtil.resultProcess(result1, result2);
                        sendMessage(LtpResultUtil.resultProcess(result1, result2).toString());
                        //item.sendMessage(result3);
                        sendMessage("本次词法分析结束!");
                        if (service.asrResult.getFinalText().equals("结束")) {
                            transcriberExit = true;
                            System.out.println("语音助手连接断开");
                            try {
                                sendMessage("转写结束");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sendExit = true;
                        }
                        service.asrResult.setFinalText(null);
                    }
                    if(service.asrResult.getError() != null ){
                        try {
                            sendMessage(service.asrResult.getError());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sendExit = true;
                        sendMessage("请连接麦克风后重试");
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }

    */
/**
     *
     * @param session
     * @param error
     *//*

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    */
/**
     * 群发自定义消息
     * *//*

    public static void sendInfo(String message) throws IOException {
        log.info(message);
        for (WebsocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebsocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebsocketServer.onlineCount--;
    }
}

*/
