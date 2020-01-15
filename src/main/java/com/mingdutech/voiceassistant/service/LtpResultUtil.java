package com.mingdutech.voiceassistant.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LtpResultUtil {
    public static Map<String, String> resultProcess(String wordResult, String posResult ){
        Map<String, String> result = new LinkedHashMap<>();
        String[] words = wordResult.replaceAll("\"","") .split("word:\\[")[1].split("]},desc")[0].split(",");
        String[] poss = posResult.replaceAll("\"","") .split("pos:\\[")[1].split("]},desc")[0].split(",");

        for (int i = 0; i < words.length; i++){
            switch (poss[i]){
                case "r":
                poss[i] = "代词"; break;
                case "n":
                    poss[i] = "名词"; break;
                case "ns":
                    poss[i] = "地名"; break;
                case "wp":
                    poss[i] = "标点"; break;
                case "k":
                    poss[i] = "后缀"; break;
                case "h":
                    poss[i] = "前缀"; break;
                case "u":
                    poss[i] = "助词"; break;
                case "c":
                    poss[i] = "连词"; break;
                case "v":
                    poss[i] = "动词"; break;
                case "p":
                    poss[i] = "介词"; break;
                case "d":
                    poss[i] = "副词"; break;
                case "q":
                    poss[i] = "量词"; break;
                case "nh":
                    poss[i] = "人名"; break;
                case "m":
                    poss[i] = "数次"; break;
                case "e":
                    poss[i] = "语气词"; break;
                case "b":
                    poss[i] = "状态词"; break;
                case "a":
                    poss[i] = "形容词"; break;
                case "nd":
                    poss[i] = "方位词"; break;
                case "nl":
                    poss[i] = "处所词"; break;
                case "o":
                    poss[i] = "拟声词"; break;
                case "nt":
                    poss[i] = "时间词"; break;
                case "nz":
                    poss[i] = "其他专名"; break;
                case "no":
                    poss[i] = "机构团体"; break;
                case "i":
                    poss[i] = "成语"; break;
                case "j":
                    poss[i] = "缩写词"; break;
                case "ws":
                    poss[i] = "外来词"; break;
                case "g":
                    poss[i] = "词素"; break;
                case "x":
                    poss[i] = "非词位"; break;

            }
            result.put(words[i], poss[i]);
        }
        return  result;
    }
}
