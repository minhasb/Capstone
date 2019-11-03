package com.example.carworld;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Groups {
  private  String groupimage,groupcar,grouplocation,groupname,groupownerid,groupstatus;
private ArrayList<User> members;


    public String getGroupimage() {
        return groupimage;
    }

    public String getGroupstatus() {
        return groupstatus;
    }

    public void setGroupstatus(String groupstatus) {
        this.groupstatus = groupstatus;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public void setGroupimage(String groupimage) {
        this.groupimage = groupimage;
    }

    public String getGroupcar() {
        return groupcar;
    }

    public void setGroupcar(String groupcar) {
        this.groupcar = groupcar;
    }

    public String getGrouplocation() {
        return grouplocation;
    }

    public void setGrouplocation(String grouplocation) {
        this.grouplocation = grouplocation;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupownerid() {
        return groupownerid;
    }

    public void setGroupownerid(String groupownerid) {
        this.groupownerid = groupownerid;
    }
}