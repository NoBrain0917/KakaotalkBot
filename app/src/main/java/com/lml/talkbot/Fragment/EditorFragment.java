package com.lml.talkbot.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.lml.talkbot.Activity.EditActivity;
import com.lml.talkbot.Class.HightLighter;
import com.lml.talkbot.CustomView.CustomEditText;
import com.lml.talkbot.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;


public class EditorFragment extends Fragment {

    public static String name;
    public static CustomEditText customEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        customEditText = (CustomEditText) view.findViewById(R.id.editor);
        name = new EditActivity().getStr();

if(getActivity().getSharedPreferences("pref", MODE_PRIVATE).getBoolean("useHi",false)) {
    HightLighter hightLighter = new HightLighter();
    SpannableStringBuilder ssb = new SpannableStringBuilder(readFile("/sdcard/Talk Bot/"+name));
    hightLighter.apply(ssb);
    customEditText.setText(ssb);
} else {
    customEditText.setText(readFile("/sdcard/Talk Bot/"+name));
}
        customEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(customEditText.getLineCount()>99&&1000>customEditText.getLineCount()) {
                    customEditText.setPadding(80,0,0,16);
                }
                if(customEditText.getLineCount()>999&&10000>customEditText.getLineCount()) {
                    customEditText.setPadding(110,0,0,16);
                }
                if(customEditText.getLineCount()>9999) {
                    customEditText.setPadding(140,0,0,16);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().getSharedPreferences("pref", MODE_PRIVATE).getBoolean("useHi",false)) {
                            HightLighter hightLighter = new HightLighter();
                            hightLighter.apply(editable);
                        }
                    }
                }, 300);

            }
        });
        return view;

    }
    //파일읽기
    public String readFile(String path) {
        String s = null;
        try {
            File file = new java.io.File(path);
            if (!file.exists()) return "";
            InputStream fis = new java.io.FileInputStream(file);
            InputStreamReader isr = new java.io.InputStreamReader(fis);
            BufferedReader br = new java.io.BufferedReader(isr);
            s = br.readLine();
            String read = "";
            while ((read = br.readLine()) != null) s += "\n" + read;
            fis.close();
            isr.close();
            br.close();
            return s;
        } catch (Exception e) {
        }
        return s;
    }

}
