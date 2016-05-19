package com.qingwenwei.eslpodcaster.util;

import android.os.AsyncTask;
import android.util.Log;

import com.qingwenwei.eslpodcaster.entity.Word;
import com.qingwenwei.eslpodcaster.entity.WordDefinition;
import com.qingwenwei.eslpodcaster.entity.WordEntry;
import com.qingwenwei.eslpodcaster.event.OnLoadWordDefinitionEvent;

import org.greenrobot.eventbus.EventBus;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MerriamWebsterDictLookUpAsyncTask {
    public static final String TAG = "MerriamWebsterDictLookUpAsyncTask";

    private final String keyPrefix= "?key=";
    private String baseUrl = null;
    private String key = null;

    public MerriamWebsterDictLookUpAsyncTask(String url, String key){
        this.baseUrl = url;
        this.key = key;
    }

    public void parse(String targetWord){
        new AsyncTask<String, Void, Void>(){
            @Override
            protected Void doInBackground(String... params) {
                String targetWord = params[0];
                System.out.println("targetWord" + targetWord);
                String url = baseUrl + targetWord + keyPrefix + key;
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    InputStream input = new URL(url).openStream();
                    saxParser.parse(input, new SaxParserHandler(targetWord));
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Log.i(TAG, "Can't find the word");
                }
                return null;
            }
        }.execute(targetWord);
    }

    class SaxParserHandler extends DefaultHandler {
        final String targetWord;

        public SaxParserHandler(String targetWord){
            this.targetWord = targetWord;
        }

        private boolean isWord = false;
        private boolean isHw = false;
        private boolean isPr = false;
        private boolean isDt = false;
        private boolean isWav = false;
        private boolean isFl = false;
        private boolean isVi = false;

        String example = "";
        Word word = new Word();
        WordEntry entry = null;
        WordDefinition definition = null;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(qName.equalsIgnoreCase("entry")){
                String id = attributes.getValue("id");
                if(id.equalsIgnoreCase(targetWord)
                        ||id.toLowerCase().contains((targetWord+"[").toLowerCase())){
                    isWord = true;
                    entry = new WordEntry();
                }
            }

            if(qName.equalsIgnoreCase("hw")){
                isHw = true;
            }

            if(qName.equalsIgnoreCase("pr")){
                isPr = true;
            }

            if(qName.equalsIgnoreCase("dt")){
                isDt = true;
            }

            if(qName.equalsIgnoreCase("wav")){
                isWav = true;
            }

            if(qName.equalsIgnoreCase("fl")){
                isFl = true;
            }

            if(qName.equalsIgnoreCase("vi")){
                isVi = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(qName.equalsIgnoreCase("entry") && isWord){
                isWord = false;
                word.addEntry(entry);

                if (definition != null) {
                    entry.addDefinition(definition);
                    definition = null;
                }

//                System.out.println("=======end=======\n");
            }

            if(qName.equalsIgnoreCase("vi") && isWord){
                if (definition != null)
                    definition.addExample(example);
//                System.out.println("vi: " + example);
                isVi = false;
                example = "";
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isWord) {
                if (isWav) {
                    String wav = new String(ch, start, length);
                    entry.setWav(wav);
                    isWav = false;

//                    System.out.println("Wav: " + wav);
                }

                if (isHw) {
                    String hw = new String(ch, start, length).replace("*","");
                    entry.setWord(hw);
                    isHw = false;

//                    System.out.println("Hw: " + hw);
                }

                if (isPr) {
                    String pr = new String(ch, start, length);
                    entry.setPronunciation(pr);
                    isPr = false;

//                    System.out.println("Pr: " + pr);
                }

                if (isFl) {
                    String fl = new String(ch, start, length);
                    entry.setPartOfSpeech(fl);
                    isFl = false;

//                    System.out.println("Fl: " + fl);
                }

                if (isDt) {
                    String dt = new String(ch, start, length).replace(":","");
                    isDt = false;

                    if (definition != null) {
                        entry.addDefinition(definition);
                        definition = null;
                    }
                    definition = new WordDefinition();
                    definition.setDefinition(dt);

//                    System.out.println("Dt: " + dt);
                }

                if (isVi){
                    String vi = new String(ch, start, length);
                    example += vi;
                }
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
//            System.out.println("===endDocument=== size:" + word.getEntries().size());
//            System.out.println(word);
            EventBus.getDefault().post(new OnLoadWordDefinitionEvent(word));
        }
    }

//    public static void main(String[] args) {
//        String targetWord = "steak";
//        new MerriamWebsterDictLookUpAsyncTask(
//                Constants.MERRIAM_WEBSTER_DICTIONARY_URL,
//                Constants.MERRIAM_WEBSTER_DICTIONARY_LEARNER_KEY
//        ).parse(targetWord);
//    }
}
