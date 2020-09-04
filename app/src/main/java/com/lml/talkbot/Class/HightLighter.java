package com.lml.talkbot.Class;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;

public class HightLighter {
    private ArrayList<Highlighter> data = new ArrayList<>();
    private int blue = Color.argb(255,29,138,205);
    private int orange = Color.argb(255,195,94,20);
    private int green = Color.argb(255,117,120,123);
    private int red = Color.argb(255,188,0,37);

    private class Highlighter{

        String value;
        int color;

        public Highlighter(String value, int color){
            this.value = value;
            this.color = color;
        }
    }

    private void initHighlightData(){
        String[] blueData = {"String", "FileStream","Utils","Device","Bridge", "java", "Array","context", "function", "return", "var", "let", "const", "if", "else", "switch", "for", "while", "do", "break", "continue", "case", "in", "true", "false", "new", "null", "undefined", "typeof", "delete", "try", "catch", "finally", "this", "Api"};
        for(int n = 0; n<blueData.length; n++){
            data.add(new Highlighter(blueData[n], -1));
        }
    }


    public void apply(Editable s){
        initHighlightData();
        String str = s.toString();
        if(str.length()==0) return;
        ForegroundColorSpan spans[] = s.getSpans(0, s.length(), ForegroundColorSpan.class);
        for(int n = 0; n<spans.length; n++){
            s.removeSpan(spans[n]);
        }
        int start = 0;
        while(start >= 0){
            int index = str.indexOf("/*", start);
            int end = str.indexOf("*/", index+2);
            if(index >= 0&&end >= 0){
                s.setSpan(new ForegroundColorSpan(green), index, end+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else{
                end = -5;
            }
            start = end+2;
        }

        start = 0;
        while(start >= 0){
            int index = str.indexOf("//", start);
            int end = str.indexOf("\n", index+1);
            if(index >= 0&&end >= 0){
                s.setSpan(new ForegroundColorSpan(green), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else{
                end = -1;
            }
            start = end;
        }

        start = 0;
        while(start >= 0){
            int index = str.indexOf("\"", start);
            while(index>0&&str.charAt(index-1)=='\\'){
                index = str.indexOf("\"", index+1);
            }
            int end = str.indexOf("\"", index+1);
            while(end>0&&str.charAt(end-1)=='\\'){
                end = str.indexOf("\"", end+1);
            }
            if(index >= 0&&end >= 0){
                ForegroundColorSpan[] span = s.getSpans(index, end+1, ForegroundColorSpan.class);
                if(span.length>0){
                    if(str.substring(index+1, end).contains("/*")&&str.substring(index+1, end).contains("*/")){
                        for(int n = 0; n<span.length; n++){
                            s.removeSpan(span[n]);
                        }
                        s.setSpan(new ForegroundColorSpan(orange), index, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if(str.substring(index+1, end).contains("//")){
                        span = s.getSpans(index, str.indexOf("\n", end), ForegroundColorSpan.class);
                        for(int n = 0; n<span.length; n++){
                            s.removeSpan(span[n]);
                        }
                        s.setSpan(new ForegroundColorSpan(orange), index, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                else{
                    s.setSpan(new ForegroundColorSpan(orange), index, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            else{
                end = -5;
            }
            start = end+1;
        }

        start = 0;
        while(start >= 0){
            int index = str.indexOf("'", start);
            while(index>0&&str.charAt(index-1)=='\\'){
                index = str.indexOf("'", index+1);
            }
            int end = str.indexOf("'", index+1);
            while(end>0&&str.charAt(end-1)=='\\'){
                end = str.indexOf("'", end+1);
            }
            if(index >= 0&&end >= 0){
                ForegroundColorSpan[] span = s.getSpans(index, end+1, ForegroundColorSpan.class);
                if(span.length>0){
                    if(str.substring(index+1, end).contains("/*")&&str.substring(index+1, end).contains("*/")){
                        for(int n = 0; n<span.length; n++){
                            s.removeSpan(span[n]);
                        }
                        s.setSpan(new ForegroundColorSpan(orange), index, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if(str.substring(index+1, end).contains("//")){
                        span = s.getSpans(index, str.indexOf("\n", end), ForegroundColorSpan.class);
                        for(int n = 0; n<span.length; n++){
                            s.removeSpan(span[n]);
                        }
                        s.setSpan(new ForegroundColorSpan(orange), index, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                else{
                    s.setSpan(new ForegroundColorSpan(orange), index, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            else{
                end = -5;
            }
            start = end+1;
        }

        for(int n = 0; n<data.size(); n++){
            start = 0;
            while(start >= 0){
                int index = str.indexOf(data.get(n).value, start);
                int end = index+data.get(n).value.length();
                if(index >= 0){
                    int color = data.get(n).color;
                    if(color==-1) color = blue;
                    if(s.getSpans(index, end, ForegroundColorSpan.class).length==0&&isSeperated(str, index, end-1))
                        s.setSpan(new ForegroundColorSpan(color), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else{
                    end = -1;
                }
                start = end;
            }
        }
        String[] redData = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "."};
        for(int n = 0; n<redData.length; n++){
            start = 0;
            while(start >= 0){
                int index = str.indexOf(redData[n], start);
                int end = index+1;
                if(index >= 0){
                    if(s.getSpans(index, end, ForegroundColorSpan.class).length==0&&checkNumber(str, index))
                        s.setSpan(new ForegroundColorSpan(red), index, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else{
                    end = -1;
                }
                start = end;
            }
        }
    }

    private boolean checkNumber(String str, int index){
        int start = getStartPos(str, index);
        int end = getEndPos(str, index);
        if(str.charAt(end-1)=='.') return false;
        if(start==0){
            if(str.charAt(start)=='.') return false;
            return isNumber(str.substring(start, end));
        }
        else{
            if(str.charAt(start+1)=='.') return false;
            return isNumber(str.substring(start+1, end));
        }
    }

    private boolean isSplitPoint(char ch){
        if(ch=='\n') return true;
        return " []{}()+-*/%&|!?:;,<>=^~".contains(ch+"");
    }

    private int getStartPos(String str, int index){
        while(index >= 0){
            if(isSplitPoint(str.charAt(index))) return index;
            index--;
        }
        return 0;
    }

    private int getEndPos(String str, int index){
        while(str.length()>index){
            if(isSplitPoint(str.charAt(index))) return index;
            index++;
        }
        return str.length();
    }

    private boolean isSeperated(String str, int start, int end){
        boolean front = false;
        char[] points = " []{}()+-*/%&|!?:;,<>=^~.".toCharArray();
        if(start==0){
            front = true;
        }
        else if(str.charAt(start-1)=='\n'){
            front = true;
        }
        else{
            for(int n = 0; n<points.length; n++){
                if(str.charAt(start-1)==points[n]){
                    front = true;
                    break;
                }
            }
        }
        if(front){
            try{
                if(str.charAt(end+1)=='\n'){
                    return true;
                }
                else{
                    for(int n = 0; n<points.length; n++){
                        if(str.charAt(end+1)==points[n]) return true;
                    }
                }
            }
            catch(Exception e){
                return true;
            }
        }
        return false;
    }

    private boolean isNumber(String value){
        try{
            double a = Double.valueOf(value);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}