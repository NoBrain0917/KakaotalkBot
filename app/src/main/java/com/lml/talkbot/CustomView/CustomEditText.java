package com.lml.talkbot.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.EditText;

//에딧텍스트 설정
public class CustomEditText extends EditText{
    private Rect rect;
    private Paint paint;

    //새 에딧텍스트 생성
    public CustomEditText(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }

    //새 에딧텍스트 생성2
    public CustomEditText(Context context){
        super(context);
        init();
    }

    //라인 설정
    private void init(){
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.argb(255,100,100,100));
        paint.setTextSize(40);
    }

    //라인 추가
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int baseline = getBaseline();
        for (int i=0;i<getLineCount()+1;i++){
            canvas.drawText(""+(i+1),rect.left,baseline,paint);
            baseline += getLineHeight();
        }
    }

}
