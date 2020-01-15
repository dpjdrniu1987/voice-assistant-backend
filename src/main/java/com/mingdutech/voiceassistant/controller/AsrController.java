package com.mingdutech.voiceassistant.controller;

import com.mingdutech.voiceassistant.service.SpeechTranscriberDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AsrController {
    @RequestMapping("/asr")
    public String asr(){
        SpeechTranscriberDemo service = new SpeechTranscriberDemo("EYC3jBEPhZpNAQYI", "LTAI4FrV5FvnThRvw3sLVKXp", "1AWGdkVpXojce3ltXTFEx3yjpYWiib", "");
        String filepath = "C:\\Users\\Administrator\\Downloads\\nls-sdk-java-demo\\新建文本文档 (2)1.wav";
        service.process(filepath);
        service.shutdown();
        String a = service.asrResult.getText() + "test";
        return a;
        //service.shutdown();

    }
}
