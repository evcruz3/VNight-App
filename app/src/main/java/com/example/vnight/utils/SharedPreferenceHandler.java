package com.example.vnight.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class SharedPreferenceHandler {

    Context context;

    public SharedPreferenceHandler(Context context){
        this.context = context;
    }

    public void saveObjectToSharedPreference(String preferenceFileName, String serializedObjectKey, Object object){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        sharedPreferencesEditor.putString(serializedObjectKey,serializedObject);
        sharedPreferencesEditor.apply();
    }

    public <GenericClass> GenericClass getSavedObjectFromPreference(String preferenceFileName, String preferenceKey, Type classType){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(preferenceFileName,0);
        if(sharedPreferences.contains(preferenceKey)){
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }
        return null;
    }

    public void removeObjectFromSharedPreference(String preferenceFileName, String preferenceKey){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.remove(preferenceKey);
        sharedPreferencesEditor.apply();

    }
}
