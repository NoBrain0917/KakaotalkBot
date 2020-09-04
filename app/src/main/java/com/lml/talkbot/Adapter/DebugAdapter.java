package com.lml.talkbot.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lml.talkbot.R;
import com.lml.talkbot.Class.Debug;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DebugAdapter extends RecyclerView.Adapter<DebugAdapter.ViewHolder> {
    private static ArrayList<Debug> items = new ArrayList<Debug>();
    private static DebugAdapter.ClickListener clickListener;

    @NonNull
    @Override
    public DebugAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.debug_item, viewGroup, false);
        return new DebugAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull DebugAdapter.ViewHolder viewHolder, int position) {
        Debug item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView debug;
        TextView user;
        CardView debugchat;
        CardView userchat;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            userchat = itemView.findViewById(R.id.right_chat);
            debugchat = itemView.findViewById(R.id.left_chat);
            debug = itemView.findViewById(R.id.left_send);
            user = itemView.findViewById(R.id.right_send);
            name = itemView.findViewById(R.id.sender);
            debugchat.setOnClickListener(this);
            debug.setOnClickListener(this);
            userchat.setOnClickListener(this);
            user.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*
            if(items.get(getAdapterPosition()).getBot().length()>500) {
                clickListener.onItemClick(getAdapterPosition(), v);
                MoreSeeActivity debugActivity = new MoreSeeActivity();
                debugActivity.setText(items.get(getAdapterPosition()).getBot());
            }

            if(items.get(getAdapterPosition()).getUsermsg().length()>500) {
                clickListener.onItemClick(getAdapterPosition(), v);
                MoreSeeActivity debugActivity = new MoreSeeActivity();
                debugActivity.setText(items.get(getAdapterPosition()).getUser());
            }

             */


        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }


        public void setItem(Debug item) {
            String text = item.getBot();
            String text2 = item.getUser();
            if(item.getBot().length()>500) {
                text = item.getBot().substring(0,500) + "\n. . . 전체보기";
            } else if(item.getUser().length()>500) {
                text2 = item.getUser().substring(0,500) + "\n. . . 전체보기";
            }

            debug.setText(text);
            user.setText(text2);
            if (item.getBot().replaceAll(" ", "").length() < 1) {
                debugchat.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
            } else {
                debugchat.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                name.setText(item.getName());
            }
            if (item.getUser().replaceAll(" ", "").length() < 1) {
                userchat.setVisibility(View.GONE);
            } else {
                userchat.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setOnItemClickListener(DebugAdapter.ClickListener clickListener) {
        DebugAdapter.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public void setItems(ArrayList<Debug> items) {
        this.items = items;
    }
    public void addItem(Debug item) {
        items.add(item);
    }
    public void removeAll(){items.clear();}


}