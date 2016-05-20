package com.qingwenwei.eslpodcaster.util;

import com.qingwenwei.eslpodcaster.entity.Word;
import com.qingwenwei.eslpodcaster.entity.WordDefinition;
import com.qingwenwei.eslpodcaster.entity.WordEntry;

public class WordHtmlUtil {
    public static String constructHtml(Word word){
        String html = "";
        for(WordEntry entry : word.getEntries()){
            if(entry.getWav() == null
                    && entry.getPronunciation() == null
                    && entry.getPartOfSpeech() == null)
                continue;

            if(entry.getWord() != null)
                html += "<h2>" + entry.getWord() + "</h2>";

            if(entry.getPronunciation() != null)
                html += "<i>  |" + entry.getPronunciation() + "|</i><br><br>";

            if(entry.getPartOfSpeech() != null)
                html += "<b>" + entry.getPartOfSpeech() + "</b><br><br>\n";

            for(WordDefinition def : entry.getDefinitions()){
                html += "<b>" + def.getDefinition() + "</b>\n";

                for(String example : def.getExamples()){
                    html += "<p> - " + example + "</p>\n";
                }
            }
        }

        return html;
    }
}
