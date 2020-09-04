package com.lml.talkbot.Class;

public class Log {
    String name;
    Boolean isError;
    public Log(String name,Boolean isError) {
        this.name = name;
        this.isError = isError;
    }

    public Boolean getError() {
        return isError;
    }

    public String getName() {
        return name;
    }

    public void setError(Boolean error) {
        isError = error;
    }

    public void setName(String name) {
        this.name = name;
    }
}
