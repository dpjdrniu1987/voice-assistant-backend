package com.mingdutech.voiceassistant.server;

import com.alibaba.nls.client.protocol.InputFormatEnum;
import com.alibaba.nls.client.protocol.SampleRateEnum;
import com.alibaba.nls.client.protocol.asr.SpeechTranscriber;
import com.mingdutech.voiceassistant.service.HttpUtil;
import com.mingdutech.voiceassistant.service.LtpResultUtil;
import com.mingdutech.voiceassistant.service.SpeechTranscriberDemo;
import com.mingdutech.voiceassistant.service.ltp;
import com.mingdutech.voiceassistant.thread.AsrThread;
import com.mingdutech.voiceassistant.thread.ErrorThread;
import jdk.nashorn.internal.objects.ArrayBufferView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.mingdutech.voiceassistant.service.SpeechTranscriberDemo.*;

@Slf4j
//@ServerEndpoint("/websocket/{user}")
@ServerEndpoint(value = "/assistant")
@Component
public class WebSocketServerOk {
    // ltp webapi接口地址
    private static final String WEBTTS_URL1 = "http://ltpapi.xfyun.cn/v1/cws";
    private static final String WEBTTS_URL2 = "http://ltpapi.xfyun.cn/v1/pos";
    private static final String WEBTTS_URL3 = "http://ltpapi.xfyun.cn/v1/ner";
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServerOk> webSocketSet = new CopyOnWriteArraySet<WebSocketServerOk>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    public static volatile boolean sendExit = false;

    private SpeechTranscriberDemo service = null;
    private SpeechTranscriber transcriber = null;
    private ErrorThread errorThread;
    String middleText = "";


    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());


        try {
            sendMessage("智能语音助手连接成功,请开始说话！");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        log.info("来自客户端的消息:" + message);
        if (message.equals("结束")){
            transcriber.stop();
            service.shutdown();
            sendMessage("提示信息：转写结束");
            ErrorThread.exit = true;
            session.close();
        }
        if (message.equals("开始")){
            transcriber.start();
            sendMessage("提示信息：转写开始");
        }
    }

    @OnMessage
    public void onMessage(byte[] message) throws IOException, InterruptedException {
        log.info("来自客户端的消息:" + message);

        if (service == null && transcriber == null) {
            try {
                service = new SpeechTranscriberDemo("EYC3jBEPhZpNAQYI", "LTAI4FrV5FvnThRvw3sLVKXp", "1AWGdkVpXojce3ltXTFEx3yjpYWiib", "");
                transcriber = new SpeechTranscriber(service.client, getTranscriberListener());

                //开启监测转写服务错误的线程
                errorThread = new ErrorThread(this.session, this.service);
                errorThread.start();

                transcriber.setAppKey(service.appKey);
                // 输入音频编码方式
                transcriber.setFormat(InputFormatEnum.PCM);
                // 输入音频采样率
                transcriber.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
                // 是否返回中间识别结果
                transcriber.setEnableIntermediateResult(true);
                // 是否生成并返回标点符号
                transcriber.setEnablePunctuation(true);
                // 是否将返回结果规整化,比如将一百返回为100
                transcriber.setEnableITN(true);
                transcriber.start();
                sendMessage("提示信息：请开始说话！");
                middleText = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //向转写服务发送从前端收到的byte[]
        transcriber.send(message);
        int deltaSleep = getSleepDelta(message.length, 16000);
        //Thread.sleep(deltaSleep);

        if (service.asrResult.getText() != null && !service.asrResult.getText().equals(middleText)) {
            sendMessage("中间结果：" + service.asrResult.getText());
            middleText = service.asrResult.getText();
        }

        //Thread.sleep(200);

        if (service.asrResult.getFinalText() != null && !service.asrResult.getFinalText().equals("")) {
            sendMessage("最终结果：" +service.asrResult.getFinalText());
            //sendMessage("本次语音识别结束!");

            ltp ltpservice = new ltp();
            Map<String, String> header = ltpservice.buildHttpHeader();
            String result1 = HttpUtil.doPost1(WEBTTS_URL1, header, "text=" + URLEncoder.encode(service.asrResult.getFinalText(), "utf-8"));
            String result2 = HttpUtil.doPost1(WEBTTS_URL2, header, "text=" + URLEncoder.encode(service.asrResult.getFinalText(), "utf-8"));
            //String result3 = HttpUtil.doPost1(WEBTTS_URL3, header, "text=" + URLEncoder.encode(service.asrResult.getFinalText(), "utf-8"));
            System.out.println("ltp 接口调用结果（分词）：" + result1);
            System.out.println("ltp 接口调用结果（词性标注）：" + result2);
            //System.out.println("ltp 接口调用结果（实体识别）：" + result3);
            //LtpResultUtil.resultProcess(result1, result2);
            sendMessage("词法分析结果：" + LtpResultUtil.resultProcess(result1, result2).toString());
            //item.sendMessage(result3);
            //sendMessage("本次词法分析结束!");
            if (service.asrResult.getFinalText().equals("结束")) {
                transcriber.close();
                System.out.println("语音助手连接断开");
                try {
                    sendMessage("提示信息：转写结束");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //sendExit = true;
            }
            service.asrResult.setFinalText(null);
        }
        if (service.asrResult.getError() != null) {
            /*try {
                sendMessage(service.asrResult.getError());
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            //sendExit = true;
            sendMessage("提示信息：出现错误，"+ service.asrResult.getError() +"请连接麦克风后重试!");
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message) throws IOException {
        log.info(message);
        for (WebSocketServerOk item : webSocketSet) {
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
        WebSocketServerOk.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServerOk.onlineCount--;
    }

    /// 根据二进制数据大小计算对应的同等语音长度
    /// sampleRate 仅支持8000或16000
    public static int getSleepDelta(int dataSize, int sampleRate) {
        // 仅支持16位采样
        int sampleBytes = 16;
        // 仅支持单通道
        int soundChannel = 1;
        return (dataSize * 10 * 8000) / (160 * sampleRate);
    }
}

