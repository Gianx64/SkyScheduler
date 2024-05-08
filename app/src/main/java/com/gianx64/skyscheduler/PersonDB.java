package com.gianx64.skyscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class PersonDB {
    SQLiteDatabase db;
    ArrayList<PersonClass> personnel = new ArrayList<PersonClass>();
    PersonClass person;
    Context context;
    String dbName = "SkyPersonnel";
    String createQuery = "create table if not exists person(id integer primary key autoincrement, name text, scheduleStart integer, scheduleEnd integer)";

    public PersonDB(Context context) {
        this.context = context;
        db=context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
        db.execSQL(createQuery);
    }

    public ArrayList<PersonClass> readAll(){
        personnel.clear();
        Cursor cursor = db.rawQuery("select * from person", null);
        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                personnel.add(new PersonClass(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)));
            } while(cursor.moveToNext());
        }
        return personnel;
    }

    public PersonClass readOne(int id){
        Cursor cursor = db.rawQuery("select * from person where id = " + String.valueOf(id), null);
        person = new PersonClass(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));
        return person;
    }

    public boolean insert(PersonClass p){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("scheduleStart", p.getScheduleStart());
        contentValues.put("scheduleEnd", p.getScheduleEnd());
        return db.insert("person", null, contentValues) > 0;
    }

    public boolean update(PersonClass p){
        try {
            db.execSQL("UPDATE person SET name='"+p.getName()+"', scheduleStart='"+p.getScheduleStart()+"', scheduleEnd='"+p.getScheduleEnd()+"' WHERE id='"+p.getId()+"'");
        } catch (Exception e) {
            Log.d("info", e.toString());
            return false;
        }
        return true;
    }

    public boolean updateOld(PersonClass p){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("scheduleStart", String.valueOf(p.getScheduleStart()));
        contentValues.put("scheduleEnd", String.valueOf(p.getScheduleEnd()));
        return db.update("person", contentValues, "id = " + person.getId(), null) > 0;
    }

    public boolean remove(int id){
        return db.delete("person", "id = "+id, null) > 0;
    }
}
