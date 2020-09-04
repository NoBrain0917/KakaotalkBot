package com.lml.talkbot.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lml.talkbot.Adapter.DebugAdapter;
import com.lml.talkbot.Adapter.LogAdapter;
import com.lml.talkbot.Class.Log;
import com.lml.talkbot.R;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LogFragment extends Fragment  {

    public static RecyclerView recyclerView;
    public static LogAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        recyclerView = view.findViewById(R.id.rec);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LogAdapter();
        recyclerView.setAdapter(adapter);
        return view;

    }
}
