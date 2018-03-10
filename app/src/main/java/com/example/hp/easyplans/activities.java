package com.example.hp.easyplans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by HP on 6/20/2017.
 */

public class activities extends Object {
    long _id;
    String name;
    int amount;
    long plan_id;
    long[] amountbymembers;
    ArrayList<Member> members;
    public activities()
    {
        members=new ArrayList<>();
    }
    public static long putindatabase(Context context,activities a)
    {
        ContentValues cv=new ContentValues();
        cv.put(PlansContract.activity.COLUMN_ACTIVITY_NAME,a.name);
        cv.put(PlansContract.activity.COLUMN_PLAN_ID,a.plan_id);
        cv.put(PlansContract.activity.COLUMN_COST,a.amount);
        PlanProvider provider=new PlanProvider(context);
        Uri insertedactivity=provider.insert(PlansContract.BASE_CONTENT_URI.buildUpon().
                appendPath(PlansContract.PATH_PLANS).
                appendPath(String.valueOf(a.plan_id)).appendPath(PlansContract.PATH_ACTIVITY).build(),cv);
        a._id=Long.parseLong(PlansContract.activity.getactivityidwithuri(insertedactivity));
        if(a._id>0)
        {
            Log.v("easyplans","activity inserted");
        }
        ContentValues activitymembervals=new ContentValues();
        activitymembervals.put(PlansContract.activity_members.COLUMN_ACTIVITY_ID,a._id);
        Member[] activitymembers = new Member[a.members.toArray().length];
        a.members.toArray(activitymembers);
        float activityamountpermember=a.amount/activitymembers.length;
        for(int i=0;i<activitymembers.length;i++)
        {
            activitymembervals.put(PlansContract.activity_members.COLUMN_MEMBER_ID,activitymembers[i].member_id);
            activitymembervals.put(PlansContract.activity_members.COLUMN_PAID,a.amountbymembers[i]);
            provider.insert(PlansContract.BASE_CONTENT_URI.buildUpon().
                    appendPath(PlansContract.PATH_ACTIVITY).
                    appendPath(String.valueOf(a._id)).
                    appendPath(PlansContract.PATH_MEMBERS).
                    build(),activitymembervals);
            provider.updatecostofplanmembers(a.plan_id,activitymembers[i].member_id,  activityamountpermember-a.amountbymembers[i]);
        }

    return a._id;
    }
    public static long  updateindatabase(Context context, activities a)
    {
        PlanProvider provider=new PlanProvider(context);
        provider.delete(
                PlansContract.BASE_CONTENT_URI.
                        buildUpon().
                        appendPath(PlansContract.PATH_ACTIVITY).
                        appendPath(String.valueOf(a._id)).build(),null,null);
        putindatabase(context,a);
        return a._id;
    }

    public static activities getactivitybyid(long id,Context context)
    {
        activities a=new activities();
        PlanProvider provider=new PlanProvider(context);
        Cursor c=provider.query(PlansContract.activity.buildactivityuri(id),null,null,null,null);
        Log.v("easyplans","number of columns returned " +String.valueOf(c.getColumnCount()));
        String[] N=c.getColumnNames();
        for(int i=0;i<N.length;i++)
        {
            Log.v("easyplans",N[i]);
        }
        Cursor c2=provider.query(PlansContract.activity.buildactivityuri(id).buildUpon().appendPath(PlansContract.PATH_MEMBERS).build(),null,null,null,null);
        c.moveToFirst();
        if(c.getCount()>0&&c!=null)
        {
            a._id=c.getLong(c.getColumnIndex(PlansContract.activity.COLUMN_ID));
            a.name=c.getString(c.getColumnIndex(PlansContract.activity.COLUMN_ACTIVITY_NAME));
            a.amount=c.getInt(c.getColumnIndex(PlansContract.activity.COLUMN_COST));
            a.plan_id=c.getLong(c.getColumnIndex(PlansContract.activity.COLUMN_PLAN_ID));
            a.amountbymembers=new long[c2.getCount()];
            Log.v("easyplans",String.valueOf(c2.getCount()));
            for(int i=0;i<c2.getCount();i++)
            {
                c2.moveToPosition(i);
                Member m=new Member(c2.getString(c2.getColumnIndex(PlansContract.members.COLUMN_MEMBER_NAME)),c2.getString(c2.getColumnIndex(PlansContract.members.COLUMN_MEMBER_NUMBER)));
                m.member_id=c2.getLong(c2.getColumnIndex(PlansContract.members.COLUMN_MEMBER_ID));
                a.members.add(m);
                a.amountbymembers[i]=c2.getLong(c2.getColumnIndex(PlansContract.activity_members.COLUMN_PAID));
            }
        }
        Log.v("easyplans","activity members "+a.members.toArray().length);
        return a;
    }
}
