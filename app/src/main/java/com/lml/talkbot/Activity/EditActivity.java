package com.lml.talkbot.Activity;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lml.talkbot.Adapter.PageAdapter;
import com.lml.talkbot.Class.Debug;
import com.lml.talkbot.Class.FileStream;
import com.lml.talkbot.Class.HightLighter;
import com.lml.talkbot.Class.List;
import com.lml.talkbot.Class.Log;
import com.lml.talkbot.Class.Utils;
import com.lml.talkbot.CustomView.CustomEditText;
import com.lml.talkbot.Fragment.DebugFragment;
import com.lml.talkbot.Fragment.EditorFragment;
import com.lml.talkbot.Fragment.LogFragment;
import com.lml.talkbot.R;
import com.lml.talkbot.Service.TalkBotListener;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.lml.talkbot.Fragment.EditorFragment.customEditText;

public class EditActivity extends AppCompatActivity {

    ViewPager viewPager;
    public static String str;
    public static int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().show();
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getSupportActionBar().setElevation(0);
        str = getIntent().getStringExtra("ScriptName");
        pos = getIntent().getIntExtra("position",0);
        getSupportActionBar().setTitle(getIntent().getStringExtra("ScriptName"));

        viewPager = findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(4);
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addItem(new EditorFragment());
        pageAdapter.addItem(new DebugFragment());
        pageAdapter.addItem(new LogFragment());
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(0);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0) {
                    bottomNavigationView.setSelectedItemId(R.id.editor);
                } else if(position==1) {
                    bottomNavigationView.setSelectedItemId(R.id.debug);
                } else if(position==2) {
                    bottomNavigationView.setSelectedItemId(R.id.log);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editor:
                        viewPager.setCurrentItem(0);
                    return true;
                }
                switch (item.getItemId()) {
                    case R.id.debug:
                        viewPager.setCurrentItem(1);
                        return true;
                }
                switch (item.getItemId()) {
                    case R.id.log:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
        if(data.getBoolean("useHi",false)==false) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main2, menu);
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.save) {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Toast.makeText(getApplicationContext(),"저장되었습니다.",Toast.LENGTH_SHORT).show();
            editor.putBoolean(str+"reload",false);
            editor.commit();
            writeFile("/sdcard/Talk Bot/"+getIntent().getStringExtra("ScriptName"),((CustomEditText)findViewById(R.id.editor)).getText().toString());
            MainActivity.adapter.setItem(getIntent().getIntExtra("position",0), new List(str, getSharedPreferences("pref", MODE_PRIVATE).getBoolean(str+"on",false), isReload(str), "Please Compile", pref.getString(str+"create","2005-9-17") +" - "+pref.getString(str+"last","")));
            MainActivity.adapter.notifyDataSetChanged();

        } else if(id==R.id.savereload){
            try {

                writeFile("/sdcard/Talk Bot/"+getIntent().getStringExtra("ScriptName"),((CustomEditText)findViewById(R.id.editor)).getText().toString());
                Script script_real;
                Context rhino = Context.enter();
                rhino.setOptimizationLevel(-1);
                rhino.setLanguageVersion(Context.VERSION_1_8);
                script_real = rhino.compileReader(new FileReader("/sdcard/Talk Bot/" + str), str, 0, null);
                ScriptableObject scope = new ImporterTopLevel(rhino);
                ScriptableObject.defineClass(scope, DebugFragment.Api.class);
                ScriptableObject.defineClass(scope, FileStream.class);
                ScriptableObject.defineClass(scope, Utils.class);
                ScriptableObject.putProperty(scope, "context", this);
                script_real.exec(rhino, scope);
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                GregorianCalendar gc = new GregorianCalendar();
                editor.putBoolean(str+"reload",true);
                editor.putString(str+"last",gc.get(Calendar.YEAR)+"-"+(gc.get(Calendar.MONTH)+1)+"-"+gc.get(Calendar.DATE));
                editor.commit();
                LogFragment.adapter.addItem(new Log("컴파일 성공",false));
                LogFragment.adapter.notifyDataSetChanged();
                new DebugFragment().Reload(str);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    TalkBotListener.Reload(str);
                }

            MainActivity.adapter.setItem(getIntent().getIntExtra("position",0), new List(str, getSharedPreferences("pref", MODE_PRIVATE).getBoolean(str+"on",false), isReload(str), "Complete Compile", pref.getString(str+"create","2005-9-17") +" - "+pref.getString(str+"last","")));
                MainActivity.adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"컴파일 하였습니다.",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                LogFragment.adapter.addItem(new Log(e.toString(),true));
                LogFragment.adapter.notifyDataSetChanged();

        }
        } else {
            writeFile("/sdcard/Talk Bot/"+getIntent().getStringExtra("ScriptName"),((CustomEditText)findViewById(R.id.editor)).getText().toString());
            SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            if(data.getBoolean("useHi",false)==false) {
                editor.putBoolean("useHi", true);
                editor.commit();
                HightLighter hightLighter = new HightLighter();
                SpannableStringBuilder ssb = new SpannableStringBuilder(readFile("/sdcard/Talk Bot/"+str));
                hightLighter.apply(ssb);
                customEditText.setText(ssb);
                item.setTitle("하이라이팅 비사용");
            } else {
                editor.putBoolean("useHi", false);
                editor.commit();
                customEditText.setText(readFile("/sdcard/Talk Bot/"+str));
                item.setTitle("하이라이팅 사용");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    //파일쓰기
    public void writeFile(String path, String str) {
        try {
            File file = new java.io.File(path);
            OutputStreamWriter os = new java.io.OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            BufferedWriter bw = new java.io.BufferedWriter(os);
            bw.write(str);
            bw.close();
            os.close();
        } catch (Exception e) {
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Boolean isReload(String name) {
        SharedPreferences data = EditActivity.this.getSharedPreferences("pref", MODE_PRIVATE);
        if(TalkBotListener.resp.get(name)==null||TalkBotListener.scope.get(name)==null) {
            return false;
        } else if(data.getBoolean(name+"reload",true)==false) {
            return false;
        } else{
            return true;
        }
    }
public static String getStr() {
        return str;
}
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
