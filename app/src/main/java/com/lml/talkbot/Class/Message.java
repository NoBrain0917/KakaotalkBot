package com.lml.talkbot.Class;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import com.lml.talkbot.Fragment.DebugFragment;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.sax.TemplatesHandler;

import androidx.annotation.RequiresApi;

public class Message {
    private static Context ctx;
    private static Notification.Action session = null;
    private static String msg;
    private static String room;
    private static String sender;
    private static Bitmap profileImage = null;
    private static Boolean isDebug = false;
    private static Boolean isGroupChat;

    public Message() {
        super();
    }


    public void setSession(Notification.Action session) {
        this.session = session;
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setBitmap(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public void setDebug(Boolean b) {
        this.isDebug = b;
    }

    public void setGroupChat(Boolean b) {
        this.isGroupChat = b;

    }


    //메세지 보내기
    public static class channel {

        public static String name;

        public void send(String value) {
            if (isDebug) {
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DebugFragment.adapter.addItem(new Debug(value, "", DebugFragment.name));
                        DebugFragment.adapter.notifyDataSetChanged();
                        DebugFragment.recyclerView.scrollToPosition(DebugFragment.adapter.getItemCount() - 1);
                    }
                }, 0);
            } else {
                Intent sendIntent = new Intent();
                Bundle msg = new Bundle();
                for (RemoteInput inputable : session.getRemoteInputs())
                    msg.putCharSequence(inputable.getResultKey(), value);
                RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);
                try {
                    session.actionIntent.send(ctx, 0, sendIntent);

                } catch (PendingIntent.CanceledException e) {
                }
            }
        }

        }

        static class author {
            public static String name;
            public static String profile;

            public author() {
                this.name = sender;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                profileImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] ba = baos.toByteArray();
                this.profile = Base64.encodeToString(ba, 0);
            }
        }


}

