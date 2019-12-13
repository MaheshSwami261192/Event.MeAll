package com.prometteur.myevents.SingletonClasses;

public class Contact {
    private String name,subname,isSelected,userName;

    public Contact() {
    }

    public Contact(String name,String subname,String userName) {
        this.name = name;
        this.subname = subname;
        this.userName = userName;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}