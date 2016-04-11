package com.qingwenwei.eslpodcaster.util;

import android.util.Log;
import android.util.Xml;

import com.qingwenwei.eslpodcaster.entity.PodcastItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qingwenwei on 2016-04-09.
 */
public class XmlParser {

    private static String TAG = "@[XmlParser]";

    public List<PodcastItem> parse(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
//        readFeed(parser);
        return readFeed(parser);
    }



    private List<PodcastItem> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {

        List<PodcastItem> items = new ArrayList<>();

        int eventType = parser.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT){
            String tagName = parser.getName();
            if(tagName!=null
                    && tagName.equals("item")
                    && parser.getEventType() == XmlPullParser.START_TAG){

                items.add(readItem(parser));
            }

//            if(eventType == XmlPullParser.START_TAG){
//                Log.i(TAG," START_TAG " + parser.getName());
//            }else if(eventType == XmlPullParser.END_TAG){
//                Log.i(TAG," END_TAG "+ parser.getName());
//            }else if(eventType == XmlPullParser.START_DOCUMENT){
//                Log.i(TAG," START_DOCUMENT "+ parser.getName());
//            }else if(eventType == XmlPullParser.TEXT){
//                Log.i(TAG," TEXT " + parser.getText());
//            }

            eventType = parser.next();
        }
        Log.i(TAG, "Finished parsing.");
        return items;
    }


    private PodcastItem readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        String title = null;
        String subtitle = null;
        String pubDate = null;
        String audiFileUrl = null;

//        parser.require(XmlPullParser.START_TAG, null, "item");

        String tagName;
        while(parser.next() != XmlPullParser.END_DOCUMENT){
            tagName = parser.getName();

            if(tagName != null && tagName.equals("item") && parser.getEventType() == XmlPullParser.END_TAG){
//                Log.i(TAG,"break...");
                break;
            }
            if(tagName == null || parser.getEventType() == XmlPullParser.END_TAG){
//                Log.i(TAG,"continue..." + tagName);
                continue;
            }

            if(tagName.equals("title")){
                title = readTitle(parser);
//                Log.i(TAG, "@title: " + readTitle(parser));
//            }else if(tagName.equals("itunes:summary")){
//                Log.i(TAG, "@sumry: " + readSummary(parser));
            }else if(tagName.equals("pubDate")){
                pubDate = readPubDate(parser);
//                Log.i(TAG, "@pubda: " + readPubDate(parser));
            }else if(tagName.equals("enclosure")){
                audiFileUrl = readAudioFileUrl(parser);
//                Log.i(TAG,"!!!!!" + audiFileUrl);
            }else if(tagName.equals("itunes:subtitle")){
                subtitle = readSubtitle(parser);
//                Log.i(TAG, "@subtl: " + readSubtitle(parser));
            }

        }


//        Log.i(TAG,"Finished readItem() " + title);
        return new PodcastItem(title,subtitle,"",pubDate,audiFileUrl);
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        return parser.getText();
    }

    private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        return parser.getText();
    }

    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        return parser.getText();
    }

    private String readSubtitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.next();
        return parser.getText();
    }

    private String readAudioFileUrl(XmlPullParser parser){
        return parser.getAttributeValue(0);
    }

//    private Map<String,String> readEnclosure(XmlPullParser parser){
//        Map<String,String> mp3 = new HashMap<>();
//        mp3.put("url",parser.getAttributeValue(0));
//        mp3.put("length",parser.getAttributeValue(1));
//        mp3.put("type",parser.getAttributeValue(2));
//        return mp3;
//    }
}
