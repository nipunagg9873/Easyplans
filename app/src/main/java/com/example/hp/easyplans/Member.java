package com.example.hp.easyplans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by HP on 6/20/2017.
 */

public class Member extends Object {
    String name;
    String contact_no;
    long member_id;

    public Member(String name,String contactnumber)
    {
        this.name=name;
        this.contact_no=contactnumber;
    }
    public static Member[] getmembers(Context context, long[] ids,String[] keys,int noofmems)
    {
        Member[] result=new Member[noofmems];
        int position=0;
        int i;
        Log.v("sunshine",ids[0]+" "+keys[0]);
        for(i=0;i<noofmems;i++) {
            Cursor c = context.getContentResolver().query(ContactsContract.Contacts.getLookupUri(ids[i], keys[i]), null,
                    null, null, null);
            c.moveToFirst();
            if(c.getCount()==0)
            {
                Log.e("easyplans","no data returned");
            }

//            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//            if (hasPhone.equalsIgnoreCase("1")) {
//                Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + ids[i], null, null);
//                phones.moveToFirst();
//                    String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    Member m = new Member(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)), "0");
                    result[position++] = m;
//            }
        }
    return result;
    }
    public static long[] insertmemsindatabase(Context context,Member[] result,int noofmems)
    {

        PlanProvider provider=new PlanProvider(context);
        long[] resultids=new long[noofmems] ;
        ContentValues membervals=new ContentValues();
        SQLiteDatabase db=new PlansDBhelper(context).getWritableDatabase();
        Uri insertedrowuri = Uri.EMPTY;
        for(int i=0;i<noofmems;i++) {
            membervals.put(PlansContract.members.COLUMN_MEMBER_NAME, result[i].name);
            membervals.put(PlansContract.members.COLUMN_MEMBER_NUMBER, result[i].contact_no);
            Log.v("easyplans",PlanProvider.Addmemberuri.toString());
            insertedrowuri = provider.insert(PlanProvider.Addmemberuri, membervals);
            resultids[i] = Long.parseLong(insertedrowuri.getPathSegments().get(1));
        }
        return resultids;
    }
}

