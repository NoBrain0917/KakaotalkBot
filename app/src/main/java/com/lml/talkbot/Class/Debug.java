package com.lml.talkbot.Class;

public class Debug {
    String bot;
    String user;
    String name;

    public Debug(String bot, String user, String name) {
        this.bot = bot;
        this.user = user;
        this.name = name;
    }

    public  String getBot(){
        return bot;
    }
    public  String getUser(){
        return user;
    }
    public String getName(){ return name;}

}
