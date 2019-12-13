package com.prometteur.myevents.SingletonClasses;

public class Invitees {
    private String name;
    private String userName;
    private int position;

    public Invitees() {
    }

    public Invitees(String name,String userName,int position) {
        this.name = name;
        this.userName = userName;
        this.position = position;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}