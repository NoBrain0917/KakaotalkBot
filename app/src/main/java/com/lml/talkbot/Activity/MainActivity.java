package com.lml.talkbot.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lml.talkbot.Adapter.ListAdapter;
import com.lml.talkbot.Class.FileStream;
import com.lml.talkbot.Class.Utils;
import com.lml.talkbot.Fragment.DebugFragment;
import com.lml.talkbot.R;
import com.lml.talkbot.Class.List;
import com.lml.talkbot.Service.TalkBotListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.jsc.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static ListAdapter adapter;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListAdapter();
        new ItemTouchHelper(ith).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.custom_dialog_access, null);
                    builder.setView(view);
                    LinearLayout file = view.findViewById(R.id.file);
                    LinearLayout noti = view.findViewById(R.id.noti);
                    LinearLayout acc = view.findViewById(R.id.acess);
                    AlertDialog dialog = builder.create();
                    file.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Build.VERSION.SDK_INT > 22) {
                                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                }
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 1);
                                }
                            }
                        }
                    });
                    noti.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                            startActivity(intent);
                        }
                    });
                    acc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
                            startActivity(intent);
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                } else {
                    if(new File("/sdcard/Talk Bot/").isDirectory()) {
                        for (int n = 0; n < new File("/sdcard/Talk Bot/").list().length; n++) {
                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                            String name = new File("/sdcard/Talk Bot/").list()[n];
                            adapter.addItem(new List(name, getSharedPreferences("pref", MODE_PRIVATE).getBoolean(name+"on",false), isReload(name), "Please Compile", pref.getString(name+"create","2005-9-17") +" - "+ pref.getString(name+"last","")));
                        }

                        adapter.notifyDataSetChanged();
                    }
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.custom_dialog, null);
                builder.setView(view);
                TextView title = view.findViewById(R.id.title);
                TextView subtitle = view.findViewById(R.id.subtitle);
                title.setText("버전이 너무 낮습니다 :(");
                subtitle.setText("안탑깝네요...");
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.custom_dialog_makesource, null);
                final EditText name = v.findViewById(R.id.editText);
                final Button ok = v.findViewById(R.id.rightButton);
                Button cancel = v.findViewById(R.id.leftButton);
                builder.setView(v);
                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ms = "function onMessage(message) {\n\n" +
                                "}";
                        if (name.getText().toString().length() > 0) {
                            Toast.makeText(MainActivity.this, "생성하였습니다. (" + name.getText() + ".js)", Toast.LENGTH_SHORT).show();
                            if(new File("/sdcard/Talk Bot/").isDirectory()==false) {
                                GregorianCalendar gc = new GregorianCalendar();
                                SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = data.edit();
                                editor.putString(name.getText() + ".jscreate",gc.get(Calendar.YEAR)+"-"+(gc.get(Calendar.MONTH)+1)+"-"+gc.get(Calendar.DATE));
                                editor.commit();
                                String date = gc.get(Calendar.YEAR)+"-"+(gc.get(Calendar.MONTH)+1)+"-"+gc.get(Calendar.DATE);
                                new File("/sdcard/Talk Bot/").mkdir();
                                writeFile("/sdcard/Talk Bot/" + name.getText() + ".js", ms);
                                    adapter.addItem(new List(name.getText().toString()+".js", false, false, "Please Compile", date +" - "));
                                adapter.notifyDataSetChanged();
                            } else {
                                    GregorianCalendar gc = new GregorianCalendar();
                                    SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = data.edit();
                                    editor.putString(name.getText() + ".jscreate",gc.get(Calendar.YEAR)+"-"+(gc.get(Calendar.MONTH)+1)+"-"+gc.get(Calendar.DATE));
                                    editor.commit();
                                    String date = gc.get(Calendar.YEAR)+"-"+(gc.get(Calendar.MONTH)+1)+"-"+gc.get(Calendar.DATE);
                                    adapter.addItem(new List(name.getText().toString()+".js", false, false,"Please Compile",date +" - "));
                                    recyclerView.setAdapter(adapter);
                                    writeFile("/sdcard/Talk Bot/" + name.getText().toString()+ ".js", ms);
                                                                }
                             dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "스크립트명이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        ListAdapter listAdapter = new ListAdapter();
        listAdapter.setOnItemClickListener(new ListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if(v.getId()==findViewById(R.id.cardview).getId()) {
                    Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                    intent.putExtra("ScriptName",adapter.getItems(position).getName());
                    intent.putExtra("position",position);
                    startActivity(intent);
                                    } else {
                    SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = data.edit();
                    String name = adapter.getItems(position).getName();
                    if(data.getBoolean(name+"on",false)==false) {
                        editor.putBoolean(name + "on", true);
                        editor.commit();
                    } else {
                        editor.putBoolean(name + "on", false);
                        editor.commit();
                    }

                }
            }


            @Override
            public void onItemLongClick(int position, View v) {
                try {
                    Script script_real;
                    String str = adapter.getItems(position).getName();
                    Context rhino = Context.enter();
                    rhino.setOptimizationLevel(-1);
                    rhino.setLanguageVersion(Context.VERSION_1_8);
                    script_real = rhino.compileReader(new FileReader("/sdcard/Talk Bot/" +str), str, 0, null);
                    ScriptableObject scope = new ImporterTopLevel(rhino);
                    ScriptableObject.defineClass(scope, DebugFragment.Api.class);
                    ScriptableObject.defineClass(scope, FileStream.class);
                    ScriptableObject.defineClass(scope, Utils.class);
                    ScriptableObject.putProperty(scope, "context", this);
                    script_real.exec(rhino, scope);
                    new DebugFragment().Reload(str);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        TalkBotListener.Reload(str);
                    }
                    GregorianCalendar gc = new GregorianCalendar();
                    SharedPreferences data = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = data.edit();
                    editor.putBoolean(str+"reload",true);
                    editor.putString(str + "last",gc.get(Calendar.YEAR)+"-"+(gc.get(Calendar.MONTH)+1)+"-"+gc.get(Calendar.DATE));
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"컴파일 하였습니다.",Toast.LENGTH_SHORT).show();
                    adapter.setItem(position, new List(str, getSharedPreferences("pref", MODE_PRIVATE).getBoolean(str+"on",false), isReload(str), "Complete Compile", data.getString(str+"create","2005-9-17") +" - "+data.getString(str+"last","")));
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void writeFile(String path, String str) {
        try {
            File file = new java.io.File(path);
            OutputStreamWriter os = new java.io.OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            BufferedWriter bw = new java.io.BufferedWriter(os);
            bw.write(str);
            bw.close();
            os.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }





    ItemTouchHelper.SimpleCallback ith = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final String name =adapter.getItems(viewHolder.getAdapterPosition()).getName();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.custom_dialog, null);
            builder.setView(view);
            TextView title = view.findViewById(R.id.title);
            TextView subtitle = view.findViewById(R.id.subtitle);
            final Button ok = view.findViewById(R.id.rightButton);
            Button cancel = view.findViewById(R.id.leftButton);
            final AlertDialog dialog = builder.create();
            title.setText(name);
            subtitle.setText("정말로 지우실건가요?");
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    adapter.removeItem(viewHolder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                    new File("/sdcard/Talk Bot/"+name).delete();
                    Toast.makeText(MainActivity.this,"삭제되었습니다 . ("+name+")",Toast.LENGTH_LONG).show();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Boolean isReload(String name) {
        SharedPreferences data = MainActivity.this.getSharedPreferences("pref", MODE_PRIVATE);
        if(TalkBotListener.resp.get(name)==null||TalkBotListener.scope.get(name)==null) {
            return false;
        } else if(data.getBoolean(name+"reload",true)==false) {
            return false;
        } else{
            return true;
        }
    }

}
