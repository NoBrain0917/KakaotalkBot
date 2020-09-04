package com.lml.talkbot.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lml.talkbot.Class.Log;
import com.lml.talkbot.R;
import com.lml.talkbot.Class.Debug;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    ArrayList<Log> items = new ArrayList<Log>();

    @NonNull
    @Override
    public LogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.log_item, viewGroup, false);
        return new LogAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log item = items.get(position);
        holder.setItem(item);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        TextView name;
        ImageView error;


        public ViewHolder(View itemView) {
            super(itemView);
            error = itemView.findViewById(R.id.err);
            name = itemView.findViewById(R.id.type);

        }



        public void setItem(Log item) {
            name.setText(item.getName());
            if(!item.getError()) {
                error.setImageResource(R.drawable.ic_check_circle_black_24dp);
            }

        }
    }

    public void setItems(ArrayList<Log> items) {
        this.items = items;
    }
    public void addItem(Log item) {
        items.add(item);
    }


}