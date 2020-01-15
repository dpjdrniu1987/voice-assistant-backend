package com.mingdutech.voiceassistant.dto;

import lombok.Data;

@Data
public class AsrResult {
    private String text ;
    private String finalText ;
    private String error ;
}
