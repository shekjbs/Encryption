package com.example.abishek.encryption;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Abishek on 21-05-2017.
 */

public class LocalData extends SQLiteOpenHelper {
    public LocalData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    String s=new String();
        s="CREATE TABLE IF NOT EXISTS datal(path varchar(50),name varchar(50),key blob,pass varchar(50)) ";//PRIMARY KEY";
        sqLiteDatabase.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(String path,String name,byte[] key,String pass){
        ContentValues cv=new ContentValues();
        cv.put("path",path);
        cv.put("name",name);
        cv.put("key",key);
        cv.put("pass",pass);
        SQLiteDatabase db=getWritableDatabase();
        db.insert("datal",null,cv);
        db.close();
    }
    public void delete(String s){
        String ss="name = "+s;
        getWritableDatabase().delete("datal",ss,null);
    }
    public Cursor getData(String name){
        String s="SELECT * FROM datal WHERE name = '"+name+"'";//+"and key="+key;

        return getWritableDatabase().rawQuery(s,null);
    }
    public Cursor allData(){
        String s="SELECT * FROM datal";
        return getWritableDatabase().rawQuery(s,null);
    }
}
