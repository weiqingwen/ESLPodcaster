package com.qingwenwei.eslpodcaster.entity;

import java.util.ArrayList;
import java.util.List;

public class Word {
    private List<WordEntry> entries = new ArrayList<>();

    public void addEntry(WordEntry entry){
        if (entries != null)
            entries.add(entry);
    }

    public List<WordEntry> getEntries() {
        return entries;
    }

    public String toString(){
        String toStr = "";

        for (WordEntry entry : entries){
            toStr += "\n==============[Entry]==============\n";
            toStr += "[word]:" + entry.getWord() + "\n";
            toStr += "[wav]:" + entry.getWav() + "\n";
            toStr += "[pron]:" + entry.getPronunciation() + "\n";
            toStr += "[part]:" + entry.getPartOfSpeech() + "\n";

            for (WordDefinition definition : entry.getDefinitions()){
                toStr += "[definition]:" + definition.getDefinition() + "\n";

                for (String example : definition.getExamples()){
                    toStr += "[exp]:" + example + "\n";
                }
            }
        }

        return toStr;
    }
}
