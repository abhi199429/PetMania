package com.example.abhishek.petmania;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Abhishek on 3/3/2018.
 */

public class SessionManager {

    Context context;
    private String name;
    SharedPreferences sharedPreferences;

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);

    }

    public void remove(){
        sharedPreferences.edit().clear().commit();
    }

    public String getName() {
        name = sharedPreferences.getString("Userdata", "");
        return name;
    }

    public void setName(String name) {
        this.name = name;
        sharedPreferences.edit().putString("Userdata", name).commit();
    }
}
