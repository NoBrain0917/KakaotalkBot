package com.lml.talkbot.Class;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class FileStream extends ScriptableObject {
    private static String pa;
    @Override
    public String getClassName() {
        return "FileStream";
    }
    @JSStaticFunction

    public static void write(String path,String content){
        try{
            File file=new File(path);
            OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            BufferedWriter bw = new BufferedWriter(os);
            bw.write(content);
            bw.close();
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @JSStaticFunction
    public static String read(String path){
        String s;
        try{
            File file=new File(path);
            if(!file.exists()) return "";
            InputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            s=br.readLine();
            String read="";
            while ((read=br.readLine())!=null) s+="\n"+read;
            fis.close();
            isr.close();
            br.close();
            return s;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }


    @JSStaticFunction
    public static Boolean zip(String path, String toPath) {
        try {
            pa = path;
            File dir = new File(path);
            File dir2 = new File(toPath);
            String fileName = dir2.getName();
            toPath = toPath.replace(fileName,"");
            String[] list = dir.list();
            String _path;

            if (dir.canRead() || dir.canWrite()) {

                int len = list.length;

                if (path.charAt(path.length() - 1) != '/')
                    _path = path + "/";
                else
                    _path = path;


                ZipOutputStream zip_out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(toPath + "/" + fileName), 2048));

                for (int i = 0; i < len; i++)
                    zip_folder("", new File(_path + list[i]), zip_out);

                zip_out.close();

            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    private static void zip_folder(String parent, File file, ZipOutputStream zout) throws IOException {
        byte[] data = new byte[2048];
        int read;

        if (file.isFile()) {
            ZipEntry entry = new ZipEntry(parent + file.getName());
            zout.putNextEntry(entry);
            BufferedInputStream instream = new BufferedInputStream(new FileInputStream(file));

            while ((read = instream.read(data, 0, 2048)) != -1)
                zout.write(data, 0, read);

            zout.flush();
            zout.closeEntry();
            instream.close();

        } else if (file.isDirectory()) {
            String parentString = file.getPath().replace(pa,"");
            parentString = parentString.substring(0,parentString.length() - file.getName().length());
            ZipEntry entry = new ZipEntry(parentString+file.getName()+"/");
            zout.putNextEntry(entry);

            String[] list = file.list();
            if (list != null) {
                int len = list.length;
                for (int i = 0; i < len; i++) {
                    zip_folder(entry.getName(),new File(file.getPath() + "/" + list[i]), zout);
                }
            }
        }
    }






}

