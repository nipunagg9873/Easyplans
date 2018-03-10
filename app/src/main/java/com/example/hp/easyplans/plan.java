package com.example.hp.easyplans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by HP on 6/11/2017.
 */

public class plan extends Object {
    long _id;
    String name;
    String date;
    long default_member_id;
    int no_of_members;
    long[] memids=new long[20];
    ArrayList<Member> members=new ArrayList<>();
    Member initmem=new Member("you","");
    activities[] activities;
    plan(String name, int no_of_members)
    {
        this.name=name;
        this.no_of_members=no_of_members;
    }
    public plan()
    {
        name="Noname";
        no_of_members=1;
        members.add(initmem);
    }

    public static void putindatabase(Context context,plan p)
    {
        PlanProvider provider=new PlanProvider(context);
        ContentValues planvals=new ContentValues();
        ContentValues planmembersvals=new ContentValues();
        planvals.put(PlansContract.plans.COLUMN_PLAN_NAME,p.getName());
        planvals.put(PlansContract.plans.COLUMN_DATE,p.date);
        Uri insertedplanuri=provider.insert(PlanProvider.Addplanuri,planvals);
        planmembersvals.put(PlansContract.plan_members.COLUMN_PLAN_ID,Long.parseLong(PlansContract.plans.getplanidwithuri(insertedplanuri)));
        planmembersvals.put(PlansContract.plan_members.COLUMN_MEMBER_ID,p.default_member_id);
        provider.insert(insertedplanuri.buildUpon().appendPath(PlansContract.PATH_PLAN_MEMBERS).build(),planmembersvals);
        if(p.memids!=null)
        for(int i=0;i<p.memids.length;i++)
        {
            planmembersvals.put(PlansContract.plan_members.COLUMN_MEMBER_ID,p.memids[i]);
            provider.insert(insertedplanuri.buildUpon().appendPath(PlansContract.PATH_PLAN_MEMBERS).build(),planmembersvals);
        }

    }
    public String getName() {
        return name;
    }
    public static plan getplan(Context context,long id)
    {
        PlanProvider provider=new PlanProvider(context);
        Uri uri=PlansContract.plans.buildplanuri(id);
        Log.v("easyplans","inside getplan :"+uri.toString());
        Cursor c=provider.query(uri,null,null,null,null);
        plan p=new plan();
        c.moveToFirst();
        Log.v("easyplans","inside getplan :"+c.getColumnIndex(PlansContract.plans.COLUMN_PLAN_ID));
        p._id=c.getLong(c.getColumnIndex(PlansContract.plans.COLUMN_PLAN_ID));
        p.name=c.getString(c.getColumnIndex(PlansContract.plans.COLUMN_PLAN_NAME));
        p.date=c.getString(c.getColumnIndex(PlansContract.plans.COLUMN_DATE));
        Cursor planmembers=provider.query(uri.buildUpon().appendPath(PlansContract.PATH_PLAN_MEMBERS).build(),null,null,null,null);
        if(planmembers!=null)
        {

            planmembers.moveToFirst();
            for(int i=0;planmembers.moveToNext();i++){
                p.memids[i]=planmembers.getLong(planmembers.getColumnIndex(PlansContract.plan_members.COLUMN_MEMBER_ID));
            }
        }
        return p;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getNo_of_members() {
        return no_of_members;
    }

    public void setNo_of_members(int no_of_members) {
        this.no_of_members = no_of_members;
    }
}
