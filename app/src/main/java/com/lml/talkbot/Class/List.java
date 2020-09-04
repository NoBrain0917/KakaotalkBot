package com.lml.talkbot.Class;

public class List {
    String name;
    Boolean checked;
    Boolean isReload;
    String error;
    String Date;

    public List(String name, Boolean checked, Boolean Reload,String error,String Date) {
        this.name = name;
        this.checked = checked;
        this.isReload = Reload;
        this.error = error;
        this.Date = Date;
    }

    public  String getName(){
        return name;
    }

    public Boolean getChecked(){
        return checked;
    }

    public Boolean getReload(){return isReload;}

    public void setName(String name){
        this.name = name;
    }

    public void setError(String error){this.error=error;}
    public String getError(){return error;}

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}

