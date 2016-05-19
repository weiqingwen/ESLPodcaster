package com.qingwenwei.eslpodcaster.event;

import com.qingwenwei.eslpodcaster.entity.Word;

public class OnLoadWordDefinitionEvent {
    public final Word word;

    public OnLoadWordDefinitionEvent(Word word){
        this.word = word;
    }
}
