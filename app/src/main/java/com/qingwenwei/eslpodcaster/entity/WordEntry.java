package com.qingwenwei.eslpodcaster.entity;

import java.util.ArrayList;
import java.util.List;

public class WordEntry {
    private String word = null;
    private String wav = null;
    private String pronunciation = null;
    private String partOfSpeech = null;
    private List<WordDefinition> definitions;

    public WordEntry(){
        this.definitions = new ArrayList<>();
    }

    public void addDefinition(WordDefinition definition){
        this.definitions.add(definition);
    }

    public List<WordDefinition> getDefinitions() {
        return definitions;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        if (this.word == null)
            this.word = word;
    }

    public String getWav() {
        return wav;
    }

    public void setWav(String wav) {
        if (this.wav == null)
            this.wav = wav;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        if (this.partOfSpeech == null)
            this.partOfSpeech = partOfSpeech;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        if (this.pronunciation == null)
            this.pronunciation = pronunciation;
    }
}
