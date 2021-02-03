package com.lml.talkbot.Class;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.lml.talkbot.Fragment.DebugFragment;

public class Message {
    public Context ctx;
    public Notification.Action session = null;
    public String content;
    public Boolean isDebug = false;
    public Author author = new Author();
    public Channel channel = new Channel();
    public Attachment attachment = new Attachment();


    public class Author {
        public String name;
        public String id;
        public String profile;
    }

    public class Channel {
        public String name;
        public String id;
        public boolean group;
        public void send(String str) {
            if (isDebug) {
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DebugFragment.adapter.addItem(new Debug(str, "", DebugFragment.name));
                        DebugFragment.adapter.notifyDataSetChanged();
                        DebugFragment.recyclerView.scrollToPosition(DebugFragment.adapter.getItemCount() - 1);
                    }
                }, 0);
            } else {
                Intent sendIntent = new Intent();
                Bundle msg = new Bundle();
                for (RemoteInput inputable : session.getRemoteInputs())
                    msg.putCharSequence(inputable.getResultKey(), str);
                RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);
                try {
                    session.actionIntent.send(ctx, 0, sendIntent);
                } catch (PendingIntent.CanceledException ignored) {
                }
            }
        }
    }
    public class Attachment {
        public String image;
    }
}
