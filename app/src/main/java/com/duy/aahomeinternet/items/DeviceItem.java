package com.duy.aahomeinternet.items;

import java.io.Serializable;

/**
 * Created by tranleduy on 22-May-16.
 */
public class DeviceItem implements Serializable {
    public static final long serialVersionUID = 2L;

    private String name = "No name", des = "No description";
    private int id;
    private int img = -1;

    public DeviceItem() {

    }

    public DeviceItem(String name, String des, int id, int img) {
        this.name = name;
        this.des = des;
        this.id = id;
        this.img = img;
    }


    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
