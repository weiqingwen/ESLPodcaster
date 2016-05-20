package com.qingwenwei.eslpodcaster.event;

import com.qingwenwei.eslpodcaster.entity.Word;

public class OnLoadWordDefinitionEvent {
    public final Word word;
    public final String message;
    public static final String MSG_SUCCESS = "SUCCESS";
    public static final String MSG_FAIL = "FAIL";

    public OnLoadWordDefinitionEvent(Word word, String message){
        this.word = word;
        this.message = message;
    }
}
