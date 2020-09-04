package com.lml.talkbot.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lml.talkbot.Activity.EditActivity;
import com.lml.talkbot.Activity.MainActivity;
import com.lml.talkbot.Adapter.DebugAdapter;
import com.lml.talkbot.Class.Device;
import com.lml.talkbot.Class.FileStream;
import com.lml.talkbot.Class.ImageDB;
import com.lml.talkbot.Class.List;
import com.lml.talkbot.Class.Log;
import com.lml.talkbot.Class.Message;
import com.lml.talkbot.Class.Utils;
import com.lml.talkbot.R;
import com.lml.talkbot.Class.Debug;
import com.lml.talkbot.Service.AccessibilityServiceManager;
import com.lml.talkbot.Service.TalkBotListener;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;
import org.jsoup.*;
import org.mozilla.javascript.commonjs.module.Require;


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
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.lml.talkbot.Activity.EditActivity.pos;

public class DebugFragment extends Fragment {

    public static Context ctx;
    public static FragmentActivity ac;
    public static String name;
    public static RecyclerView recyclerView;
    public static DebugAdapter adapter;
    public static HashMap<String, Function> res = new HashMap<>();
    public static HashMap<String, ScriptableObject> scope = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ctx = getActivity();
        ac = getActivity();
        name = new EditActivity().getStr();
        View view = inflater.inflate(R.layout.fragment_debug, container, false);
        final EditText input = view.findViewById(R.id.input);
        ImageButton send = view.findViewById(R.id.send);
        recyclerView = view.findViewById(R.id.debugview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DebugAdapter();
        adapter.removeAll();
        recyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Device.init(getActivity());
                String text = input.getText().toString();
                input.setText("");
                adapter.addItem(new Debug("",text,"둠피스트"));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                execOnMessage(name, "Debug",text,"sender",false);
            }
        });

        return view;

    }



    public static Script Reload(String name) {
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
        res.put(name,(Function) escope.get("onMessage", escope));
        org.mozilla.javascript.Context.exit();
        return script_real;
    }


    public void execOnMessage(final String name, final String room, final String msg, final String sender, final Boolean groupchat) {
        class sendThread implements Runnable {
            public void run() {
                try {
                    if (res == null || scope == null) {
                    }
                    if (res.get(name) != null) {
                        org.mozilla.javascript.Context parseContext = org.mozilla.javascript.Context.enter();
                        parseContext.setOptimizationLevel(-1);
                        Resources r = getActivity().getResources();
                        BitmapDrawable bd = (BitmapDrawable) r.getDrawable(R.drawable.lovely_doomfist);
                        Bitmap bitmap = bd.getBitmap();
                        Message message = new Message();
                        message.setContext(ctx);
                        message.setDebug(true);
                        //message.setBitmap(bitmap);
                        message.setMessage(msg);
                        message.setRoom(room);
                        message.setSender(sender);
                        message.setGroupChat(groupchat);
                        message.channel.name;

                        res.get(name).call(parseContext, scope.get(name), scope.get(name), new Object[]{message});

                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity(), "컴파일을 해주세요.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                        public void run() {
                            SharedPreferences pref = getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE);
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                            LogFragment.adapter.addItem(new Log(e.toString(),true));
                            LogFragment.adapter.notifyDataSetChanged();
                            String error;
                            error = e.getMessage().split(":")[0];
                            if(error.length()>30) {
                                error = error.substring(0,35) + "...";
                            }
                            MainActivity.adapter.setItem(pos ,new List(name, getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE).getBoolean(name+"on",false), isReload(name), error,pref.getString(name+"create","2005-9-17") +" - "+pref.getString(name+"last","")));
                            MainActivity.adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
        new Thread(new sendThread(),name).start();
    }
    public static class Api extends ScriptableObject {

        @Override
        public String getClassName() {
            return "Api";
        }
        @JSStaticFunction
        public static void makeToast(String str) {
            ac.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ctx, str, Toast.LENGTH_LONG).show();
                }
            });
        }
        @JSStaticFunction
        public static android.content.Context getContext(){
            return ctx;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @JSStaticFunction
        public static Boolean reload(String str) {
            try {
                if(str.indexOf(".js")!=-1) {
                    Reload(str);
                    TalkBotListener.Reload(str);
                    return true;
                } else {
                    String[] list = new java.io.File("/sdcard/TalkBot/").list();
                    for (int i = 0; i < list.length; i++) {
                        String name = list[i];
                        Reload(name);
                        TalkBotListener.Reload(name);
                    }

                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        @JSStaticFunction
        public static int distance(String a, String b) {
            a = a.toLowerCase();
            b = b.toLowerCase();
            // i == 0
            int [] costs = new int [b.length() + 1];
            for (int j = 0; j < costs.length; j++)
                costs[j] = j;
            for (int i = 1; i <= a.length(); i++) {
                // j == 0; nw = lev(i - 1, j)
                costs[0] = i;
                int nw = i - 1;
                for (int j = 1; j <= b.length(); j++) {
                    int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                    nw = costs[j];
                    costs[j] = cj;
                }
            }
            return costs[b.length()];
        }


        @JSStaticFunction
        public static void replyRoom(String room, String str) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.addItem(new Debug(str,"",name));
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            },0);
        }
        @JSStaticFunction
        public static void autoTouch(int x,int y) {
            AccessibilityServiceManager manager = AccessibilityServiceManager.getInstance();
            manager.dispatch(x,y);
        }



    }
    public static class Bridge extends ScriptableObject {
        @Override
        public String getClassName() {
            return "Bridge";
        }

        @JSStaticFunction
        public static ScriptableObject getScopeOf(String str) {
            try {
                if (scope.get(str) == null) Reload(str);
                return scope.get(str);
            } catch (Exception e) {
                return null;
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Boolean isReload(String name) {
        SharedPreferences data = getActivity().getSharedPreferences("pref", getActivity().MODE_PRIVATE);
        if(TalkBotListener.resp.get(name)==null||TalkBotListener.scope.get(name)==null) {
            return false;
        } else if(data.getBoolean(name+"reload",true)==false) {
            return false;
        } else{
            return true;
        }
    }

}

