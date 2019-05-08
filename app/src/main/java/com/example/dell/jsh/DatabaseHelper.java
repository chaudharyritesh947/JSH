package com.example.dell.jsh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String dataBase_name ="History.db";
    public static final String table_name ="history";
    public static final String col_1="user";
    public DatabaseHelper(Context context) {
        super(context, dataBase_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+table_name+"( user TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + table_name);
        onCreate(sqLiteDatabase);
    }
    public boolean insertData(String name)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col_1,name);
        long res = sqLiteDatabase.insert(table_name ,null ,cv);//error pr -1 return krega and succesfull pr row number
        if(res==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public Cursor getAlldata(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from "+table_name,null);
        return res;//cursor provides the random read write acces to our data base.
    }


    /*public boolean upDate(String id , String name, String surname,String marks){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues  cv = new ContentValues();
        cv.put(col_1,id);
        cv.put(col_2,name);
        cv.put(col_3,surname);
        cv.put(col_4,marks);
        sqLiteDatabase.update(table_name,cv,"ID = ?"+id,new String[] {id});
        return true;

    }*/

/*
    public boolean updateData(String id,String name,String surname,String marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1,id);
        //contentValues.put(col_2,name);
       // contentValues.put(col_3,surname);
        //contentValues.put(col_4,marks);
        db.update(table_name, contentValues, "ID = ?",new String[] { id });
        return true;
    }
*/
    public Integer deleteData(String idwq){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(table_name,"ID = ? ",new String[]{idwq});//no of rows deleted ..no of rows affected ...can return 0

    }
}
