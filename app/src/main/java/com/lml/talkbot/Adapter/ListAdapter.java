package com.lml.talkbot.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.lml.talkbot.R;
import com.lml.talkbot.Class.List;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    ArrayList<List> items = new ArrayList<List>();
    private static ClickListener clickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        List item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name;
        Switch swit;
        FrameLayout fram;
        TextView error;
        ImageView errorimage;
        TextView Date;

        public ViewHolder(View itemView) {
            super(itemView);
            Switch sw = itemView.findViewById(R.id.On);
            CardView cardView = itemView.findViewById(R.id.cardview);
            cardView.setOnClickListener(this);
            sw.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            Date = itemView.findViewById(R.id.reloadtime);
            swit = itemView.findViewById(R.id.On);
            fram = itemView.findViewById(R.id.isReload);
            name = itemView.findViewById(R.id.Scriptname);
            error = itemView.findViewById(R.id.error);
            errorimage = itemView.findViewById(R.id.imageView4);
        }

        @Override
        public void onClick(View v) {

            clickListener.onItemClick(getAdapterPosition(), v);
        }
        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return true;
        }

        public void setItem(List item) {
            name.setText( item.getName() );
            swit.setChecked(item.getChecked());
            error.setText(item.getError());
            Date.setText(item.getDate());
            if(item.getReload()) {
                fram.setBackgroundColor(Color.argb(100,0,153,204));
                errorimage.setImageResource(R.drawable.ic_check_circle_black_24dp);
            }
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
    public void setItems(ArrayList<List> items) {
        this.items = items;
    }
    public List getItems(int position) {
        return items.get(position);
    }
    public void removeItem(int position) {
        items.remove(position);
    }
    public void addItem(List item) {
        items.add(item);
    }
    public void setItem(int position, List item) { items.set(position,item);}

}
