package com.lml.talkbot.Class;

import android.os.StrictMode;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.IOException;

//Utils 라이브러리
public class Utils extends ScriptableObject {
    private static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    //jsoup에서 get
    @Override
    public String getClassName() {
        return "Utils";
    }
    @JSStaticFunction
    public static String getWebText(String adress){
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection conn = Jsoup.connect(adress).ignoreContentType(true).userAgent(USER_AGENT).timeout(999999999);
            Document doc = conn.get();
            return doc.toString();
        }
        catch(IOException e){
            return null;
        }
    }

    //뭐야 내 jsoup 돌려줘요
    @JSStaticFunction
    public static Connection parse(String adress){
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection conn = Jsoup.connect(adress).ignoreContentType(true).userAgent(USER_AGENT).timeout(999999999);
            return conn;
        }
        catch(Exception e){
            return null;
        }
    }

}