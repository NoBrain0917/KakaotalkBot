package com.lml.talkbot.Service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.widget.Toast;

import com.lml.talkbot.Activity.MainActivity;
import com.lml.talkbot.Class.Device;
import com.lml.talkbot.Class.FileStream;
import com.lml.talkbot.Class.ImageDB;
import com.lml.talkbot.Class.List;
import com.lml.talkbot.Class.Log;
import com.lml.talkbot.Class.Message;
import com.lml.talkbot.Class.Utils;
import com.lml.talkbot.Fragment.DebugFragment;
import com.lml.talkbot.Fragment.LogFragment;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;
import org.jsoup.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import androidx.annotation.RequiresApi;

import static com.lml.talkbot.Activity.EditActivity.pos;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class TalkBotListener extends NotificationListenerService {

    private static android.content.Context ctx;
    private static Activity ac;
    public static HashMap<String, Function> resp = new HashMap<>();
    public static HashMap<String, ScriptableObject> scope = new HashMap<>();
    public static HashMap<String, Notification.Action> actions = new HashMap<>();


    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        super.onNotificationPosted(sbn);
        if (sbn.getPackageName().equals("com.kakao.talk")) {
            Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
            for (Notification.Action act : wExt.getActions())
                if (act.getRemoteInputs() != null && act.getRemoteInputs().length > 0)
                    if (act.title.toString().toLowerCase().contains("reply") ||
                            act.title.toString().toLowerCase().contains("Reply") ||
                            act.title.toString().toLowerCase().contains("답장")) {
                        Bundle data = sbn.getNotification().extras;
                        Boolean isGroupChat = data.get("android.text") instanceof SpannableString;
                        String room = "";
                        String sender = "";
                        String msg = "";
                        if (Build.VERSION.SDK_INT > 23) {
                            room = data.getString("android.summaryText");
                            if (room == null) {
                                isGroupChat = false;
                            } else {
                                isGroupChat = true;
                            }
                            sender = data.get("android.title").toString();
                            msg = data.get("android.text").toString();
                        } else {
                            room = data.getString("android.title");
                            if (isGroupChat) {
                                String html = Html.toHtml((Spanned) data.get("android.text"));
                                sender = Html.fromHtml(html.split("<b>")[1].split("</b>")[0]).toString();
                                msg = Html.fromHtml(html.split("</b>")[1].split("</p>")[0].substring(1)).toString();
                            } else {
                                sender = room;
                                msg = data.get("android.text").toString();
                            }
                        }
                        if (actions.get(room) == null) {
                            actions.put(room, act);
                        }
                        ctx = getApplicationContext();
                        String[] list = new java.io.File("/sdcard/Talk Bot/").list();
                        if (list != null) {
                            for (int i = 0; i < list.length; i++) {
                                String name = list[i];
                                if (getSharedPreferences("pref", MODE_PRIVATE).getBoolean(name + "on", false)) {
                                    execOnMessage(name, sender, msg.trim(), room, isGroupChat, act, sbn.getNotification().largeIcon);
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
        }

    }

    @Override
    public void onCreate() {
        super.onDestroy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        Toast.makeText(this, "이제 정상적으로 작동됩니다.", Toast.LENGTH_SHORT).show();
        ctx = getApplicationContext();
        Device.init(ctx);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        return;
    }






    public static Script Reload(String name){
        Script script_real = null;
        org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
        rhino.setOptimizationLevel(-1);
        rhino.setLanguageVersion(org.mozilla.javascript.Context.VERSION_ES6);
        try {
            script_real = rhino.compileReader(new FileReader("/sdcard/Talk Bot/" + name), name, 0, null);
        } catch (Exception e) {
        }
        ScriptableObject escope = new ImporterTopLevel(rhino);
        rhino.setInstructionObserverThreshold(3000);
        rhino.setMaximumInterpreterStackDepth(1000);
        scope.put(name, escope);
        try {
            ScriptableObject.defineClass(escope, Api.class);
            ScriptableObject.defineClass(escope, FileStream.class);
            ScriptableObject.defineClass(escope, Utils.class);
            ScriptableObject.defineClass(escope, Device.class);
            ScriptableObject.defineClass(escope, Bridge.class);


        } catch (Exception e) {
        }
        script_real.exec(rhino, escope);
        resp.put(name,(Function) escope.get("onMessage", escope));
        org.mozilla.javascript.Context.exit();
        return script_real;
    }
    public static class Bridge extends ScriptableObject {
        @Override
        public String getClassName() {
            return "Bridge";
        }
        @JSStaticFunction
        public static ScriptableObject getScopeOf(String name) {
            return scope.get(name);
        }
    }

    //실제 Api
    public static class Api extends ScriptableObject {
        @Override
        public String getClassName() {
            return "Api";
        }
        @JSStaticFunction
        public static void makeToast(String str) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, str, Toast.LENGTH_LONG).
                            show();
                }
            },0);

        }
        @JSStaticFunction
        public static android.content.Context getContext(){
            return ctx;
        }
        @JSStaticFunction
        public static Boolean isOn(String name){
            return ctx.getSharedPreferences("pref", MODE_PRIVATE).getBoolean(name+"on",false);
        }
        @JSStaticFunction
        public static Boolean isCompile(String name) {
            return ctx.getSharedPreferences("pref", MODE_PRIVATE).getBoolean(name+"reload",false);
        }
        //리로드하기
        @JSStaticFunction
        public static Boolean reload(String str) {
            try {
                if(str.indexOf(".js")!=-1) {
                    Reload(str);
                    DebugFragment.Reload(str);
                    return true;
                } else {
                    String[] list = new java.io.File("/sdcard/TalkBot/").list();
                    for (int i = 0; i < list.length; i++) {
                        String name = list[i];
                        Reload(name);
                        DebugFragment.Reload(name);
                    }
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }


        //원하는방에 보내기
        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        @JSStaticFunction
        public static Boolean replyRoom(String room,String str) {
            Notification.Action session = actions.get(room);
            if(session!=null) {
                Intent sendIntent = new Intent();
                Bundle msg = new Bundle();
                for (RemoteInput inputable : session.getRemoteInputs())
                    msg.putCharSequence(inputable.getResultKey(), str);
                RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);
                try {
                    session.actionIntent.send(ctx, 0, sendIntent);
                    return true;
                } catch (PendingIntent.CanceledException e) {
                    return false;
                }
            } else {
                return false;
            }
        }
        //자동터치
        @JSStaticFunction
        public static void autoTouch(int x,int y) {
            AccessibilityServiceManager manager = AccessibilityServiceManager.getInstance();
            manager.dispatch(x,y);
        }
    }

    public void execOnMessage(String name, String sender, String msg, String room, boolean isGroupChat, Notification.Action session, Bitmap profileImage) {
        class callRes implements Runnable {
            public void run() {
                synchronized (this) {
                    notify();
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    boolean isCheck = pref.getBoolean(name, true);
                    if (isCheck) {
                        Context parseContext = Context.enter();
                        parseContext.setOptimizationLevel(-1);
                        try {
                            if (resp.get(name) != null) {
                                Message message = new Message();
                                String profile = new ImageDB(profileImage).getProfileImage();
                                message.ctx = ctx;
                                message.isDebug = false;
                                message.content = msg;
                                message.session = session;
                                message.channel.name = room;
                                message.channel.id = String.valueOf(room.hashCode());
                                message.channel.group = isGroupChat;
                                message.author.name = sender;
                                message.author.id = String.valueOf(sender+profile.hashCode());
                                message.attachment.image = null;
                                message.author.profile = profile;
                                resp.get(name).call(parseContext, scope.get(name), scope.get(name), new Object[]{message});
                            }
                        } catch (Exception e) {
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                    LogFragment.adapter.addItem(new Log(e.toString(),true));
                                    LogFragment.adapter.notifyDataSetChanged();
                                    String error;
                                    error = e.getMessage().split(":")[0];
                                    if(error.length()>30) {
                                        error = error.substring(0,35) + "...";
                                    }
                                    MainActivity.adapter.setItem(pos ,new List(name, getSharedPreferences("pref", MODE_PRIVATE).getBoolean(name+"on",false), isReload(name), error,pref.getString(name+"create","2005-9-17") +" - "+pref.getString(name+"last","")));
                                    MainActivity.adapter.notifyDataSetChanged();
                                }
                            }, 0);
                        }
                    }
                }
            }
        }
        new Thread(new callRes(),name).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Boolean isReload(String name) {
        SharedPreferences data = ctx.getSharedPreferences("pref", ctx.MODE_PRIVATE);
        if(TalkBotListener.resp.get(name)==null||TalkBotListener.scope.get(name)==null) {
            return false;
        } else if(data.getBoolean(name+"reload",true)==false) {
            return false;
        } else{
            return true;
        }

    }

}
