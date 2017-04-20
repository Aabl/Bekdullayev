package com.example.abl.bekdullayev.model;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;


public class Source implements Serializable{
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    public Source(String id, String name){
        this.id = id;
        this.name= name;
    }

    public String getID() {
        return id;
    }

    public void setID(String id){
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name=name;
    }
}
