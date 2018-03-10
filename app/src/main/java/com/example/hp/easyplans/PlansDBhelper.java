package com.example.hp.easyplans;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HP on 6/28/2017.
 */

public class PlansDBhelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "easyplans.db";
    public static final int DATABASE_VERSION = 54;

    public PlansDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createplantablequery = "CREATE TABLE " + PlansContract.plans.TABLE_NAME + " ( " +
                PlansContract.plans.COLUMN_PLAN_ID + " integer primary key autoincrement, " +
                PlansContract.plans.COLUMN_PLAN_NAME + " text not null, " +
                PlansContract.plans.COLUMN_DATE + " text not null);";
        String createactivitytablequery = "CREATE TABLE " + PlansContract.activity.TABLE_NAME + " ( " +
                PlansContract.activity.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlansContract.activity.COLUMN_ACTIVITY_NAME+" TEXT NOT NULL, "+
                PlansContract.activity.COLUMN_PLAN_ID+" INTEGER NOT NULL REFERENCES "+
                PlansContract.plans.TABLE_NAME+"("+PlansContract.plans.COLUMN_PLAN_ID+"),"+
                PlansContract.activity.COLUMN_COST + " INTEGER NOT NULL "+");";
        String creatememerstablequery = "CREATE TABLE " + PlansContract.members.TABLE_NAME + " ( " +
                PlansContract.members.COLUMN_MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlansContract.members.COLUMN_MEMBER_NAME + " TEXT NOT NULL, " +
                PlansContract.members.COLUMN_MEMBER_NUMBER + " INTEGER NOT NULL);";
        String creatememberplantable=" CREATE TABLE "+PlansContract.plan_members.TABLE_NAME + " ( "+
                PlansContract.plan_members.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PlansContract.plan_members.COLUMN_PLAN_ID +" INTEGER NOT NULL REFERENCES "+
                PlansContract.plans.TABLE_NAME+"("+PlansContract.plans.COLUMN_PLAN_ID+"), "+
                PlansContract.plan_members.COLUMN_MEMBER_ID+" INTEGER NOT NULL REFERENCES "+
                PlansContract.members.TABLE_NAME+"("+PlansContract.members.COLUMN_MEMBER_ID+"),"+
                PlansContract.plan_members.COLUMN_AMOUNT_DUE+" INTEGER DEFAULT '0' "+
                " ); ";
        String createactivitymembertable=" CREATE TABLE "+PlansContract.activity_members.TABLE_NAME + " ( "+
                PlansContract.activity_members.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                PlansContract.activity_members.COLUMN_ACTIVITY_ID +" INTEGER NOT NULL REFERENCES "+
                PlansContract.activity.TABLE_NAME+"("+PlansContract.activity.COLUMN_ID+"), "+
                PlansContract.activity_members.COLUMN_MEMBER_ID+" INTEGER REFERENCES "+
                PlansContract.members.TABLE_NAME+"("+PlansContract.members.COLUMN_MEMBER_ID+"),"+
                PlansContract.activity_members.COLUMN_PAID+" INTEGER DEFAULT 0"+
                " ); ";
        db.execSQL(createplantablequery);
        db.execSQL(creatememerstablequery);
        db.execSQL(createactivitytablequery);
        db.execSQL(creatememberplantable);
        db.execSQL(createactivitymembertable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlansContract.plans.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlansContract.activity.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlansContract.members.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlansContract.plan_members.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlansContract.activity_members.TABLE_NAME);
        onCreate(db);
    }
}
