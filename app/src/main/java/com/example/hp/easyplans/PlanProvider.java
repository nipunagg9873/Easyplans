package com.example.hp.easyplans;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.hp.easyplans.PlansContract.plans.buildplanuri;

/**
 * Created by HP on 6/28/2017.
 */

public class PlanProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    PlansDBhelper mPlansDBhelper;
    Context mcontext;
    public static final int PLANS = 100;
    public static final int PLANBYID = 101;
    public static final int ACTIVITIESBYPLANID = 103;
    public static final int ACTIVITYBYACTIVITYIDANDPLANID = 104;
    public static final int PLANMEMBERS = 105;
    public static final int ACTIVITYMEMBERS = 106;
    public static final int MEMBER=107;
    public static final int ADDMEMBER=108;
    public static final int PLANMEMBERBYID=109;
    public static final int ACTIVITYBYID=110;
    public static Uri Addplanuri=PlansContract.BASE_CONTENT_URI.buildUpon().appendPath(PlansContract.PATH_PLANS).build();
    public static Uri Addmemberuri=PlansContract.BASE_CONTENT_URI.buildUpon().appendPath(PlansContract.PATH_MEMBERS).build();
    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = PlansContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PlansContract.PATH_MEMBERS,ADDMEMBER);
        matcher.addURI(authority, PlansContract.PATH_MEMBERS + "/#",MEMBER);
        matcher.addURI(authority, PlansContract.PATH_PLANS, PLANS);
        matcher.addURI(authority, PlansContract.PATH_PLANS + "/#", PLANBYID);
        matcher.addURI(authority, PlansContract.PATH_ACTIVITY + "/#", ACTIVITYBYID);
        matcher.addURI(authority, PlansContract.PATH_PLANS + "/#" + "/" + PlansContract.PATH_ACTIVITY + "/#", ACTIVITYBYACTIVITYIDANDPLANID);
        matcher.addURI(authority, PlansContract.PATH_ACTIVITY + "/#"+"/" + PlansContract.PATH_MEMBERS, ACTIVITYMEMBERS);
        matcher.addURI(authority, PlansContract.PATH_PLANS + "/#" + "/" + PlansContract.PATH_ACTIVITY, ACTIVITIESBYPLANID);
        matcher.addURI(authority, PlansContract.PATH_PLANS + "/#" + "/" + PlansContract.PATH_PLAN_MEMBERS, PLANMEMBERS);
        if(matcher==null)
            Log.v("easyplans","matcher not formed");
        return matcher;
    }

    public PlanProvider(Context mcontext) {
        this.mcontext = mcontext;
        mPlansDBhelper = new PlansDBhelper(mcontext);
    }
    public Cursor getplanbyid(long id, String[] projection, @Nullable String sortOrder) {
        Log.v("easyplans","retcur id "+id);
        String selection = PlansContract.plans.COLUMN_PLAN_ID + "= ?";
        String[] selectionargs = new String[]{Long.toString(id)};
        Cursor retcur = mPlansDBhelper.getReadableDatabase().query(PlansContract.plans.TABLE_NAME,
                projection, selection, selectionargs, null, null, sortOrder);
        return retcur;
    }

    public Cursor getactivitieswithplanid(long id, String[] projection, @Nullable String sortOrder) {
        String selection = PlansContract.activity.COLUMN_PLAN_ID + "= ?";
        String[] selectionargs = new String[]{Long.toString(id)};
        Cursor retcur = mPlansDBhelper.getReadableDatabase().query(PlansContract.activity.TABLE_NAME,
                projection, selection, selectionargs, null, null, sortOrder);
        return retcur;

    }

    public Cursor getactivitywithid(long activity_id, String[] projection, @Nullable String sortOrder) {
        String selection = PlansContract.activity.COLUMN_ID + "= ? ";
        String[] selectionargs = new String[]{Long.toString(activity_id)};
        Cursor retcur = mPlansDBhelper.getReadableDatabase().query(PlansContract.activity.TABLE_NAME,
                null, selection, selectionargs, null, null, sortOrder);
        return retcur;

    }

    public Cursor getplanmembers(long plan_id, String[] projection, @Nullable String sortOrder) {
        String selection = PlansContract.plan_members.COLUMN_PLAN_ID + "= ? AND "+PlansContract.plan_members.TABLE_NAME+"."+PlansContract.plan_members.COLUMN_MEMBER_ID+"="+PlansContract.members.TABLE_NAME+"."+
                PlansContract.members.COLUMN_MEMBER_ID;
        String[] selectionargs = new String[]{Long.toString(plan_id)};
        if(sortOrder==null)
            sortOrder=PlansContract.members.COLUMN_MEMBER_NAME;
        Cursor retcur = mPlansDBhelper.getReadableDatabase().query(PlansContract.plan_members.TABLE_NAME+","+PlansContract.members.TABLE_NAME,
                projection, selection, selectionargs, null, null, sortOrder);
        return retcur;

    }
    public Cursor getmemberbyid(long memberid,String[] projection,@Nullable String sortOrder)
    {
        String selection=PlansContract.members.COLUMN_MEMBER_ID+"= ?";
        String[] selectionArgs=new String[]{Long.toString(memberid)};
        Cursor retcur=mPlansDBhelper.getReadableDatabase().query(PlansContract.members.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        return retcur;
    }

    public Cursor getactivitymembers(long activity_id, String[] projection, @Nullable String sortOrder) {
        String selection = PlansContract.activity_members.COLUMN_ACTIVITY_ID + "= ? AND "
                +PlansContract.activity_members.TABLE_NAME+"."+PlansContract.activity_members.COLUMN_MEMBER_ID
                +" = "+PlansContract.members.TABLE_NAME+"."+PlansContract.members.COLUMN_MEMBER_ID;
        String[] selectionargs = new String[]{Long.toString(activity_id)};
        if(sortOrder==null)
            sortOrder=PlansContract.members.COLUMN_MEMBER_NAME;
        Log.v("easyplans",selection);
        Cursor retcur = mPlansDBhelper.getReadableDatabase().query(
                PlansContract.activity_members.TABLE_NAME+","+PlansContract.members.TABLE_NAME,
                projection, selection, selectionargs, null, null, sortOrder);
        Log.v("easyplans","activity id "+activity_id+" retcur length "+retcur.getCount());
        return retcur;

    }
    public void updatecostofplanmembers(long plan_id,long member_id,float amount)
    {
        SQLiteDatabase db = mPlansDBhelper.getWritableDatabase();
        String updatequery=" UPDATE "+
                PlansContract.plan_members.TABLE_NAME+
                " SET " +
                PlansContract.plan_members.COLUMN_AMOUNT_DUE +
                " = "+
                PlansContract.plan_members.COLUMN_AMOUNT_DUE+
                " + "+
                amount+
                " WHERE "+
                PlansContract.plan_members.COLUMN_PLAN_ID+
                " = "+plan_id+" AND "+PlansContract.plan_members.COLUMN_MEMBER_ID+" = "+member_id+";";
        Log.v("easyplans",updatequery);
        db.execSQL(updatequery);
        db.close();
    }


    @Override
    public boolean onCreate() {

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Log.v("easyplans", "matcher: "+match);
        Cursor retcur;
        switch (match) {
            case PLANS:
                retcur = mPlansDBhelper.getReadableDatabase().query(PlansContract.plans.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PLANBYID: {
                long id = Long.parseLong(uri.getPathSegments().get(1));
                Log.v("easyplans",uri.getPathSegments().get(1)+uri);
                retcur = getplanbyid(id, projection, sortOrder);
                break;
            }
            case ACTIVITIESBYPLANID: {
                long id = Long.parseLong(uri.getPathSegments().get(1));
                retcur = getactivitieswithplanid(id, projection, sortOrder);
                break;
            }
            case ACTIVITYBYID: {
                long id_activity = Long.parseLong(PlansContract.activity.getactivityidwithuri(uri));
                retcur = getactivitywithid(id_activity, projection, sortOrder);
                break;
            }
            case PLANMEMBERS: {
                long id_plan = Long.parseLong(uri.getPathSegments().get(1));
                retcur = getplanmembers(id_plan, projection, sortOrder);
                break;
            }
            case ACTIVITYMEMBERS: {
                long id_activity = Long.parseLong(uri.getPathSegments().get(1));
                Log.v("easyplans","actiity id "+uri.getPathSegments().get(1));
                retcur = getactivitymembers(id_activity, projection, sortOrder);
                break;
            }
            case MEMBER:{
                long id_member=Long.parseLong(uri.getLastPathSegment());
                retcur=getmemberbyid(id_member,null,null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retcur.setNotificationUri(mcontext.getContentResolver(), uri);
        return retcur;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLANS:
                return PlansContract.plans.CONTENT_TYPE;
            case PLANBYID:
                return PlansContract.plans.CONTENT_ITEM_TYPE;
            case ACTIVITIESBYPLANID:
                return PlansContract.activity.CONTENT_TYPE;
            case ACTIVITYBYACTIVITYIDANDPLANID:
                return PlansContract.activity.CONTENT_ITEM_TYPE;
            case PLANMEMBERS:
                return PlansContract.members.CONTENT_TYPE;
            case ACTIVITYMEMBERS:
                return PlansContract.members.CONTENT_TYPE;
            case MEMBER:
                return PlansContract.members.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
//    switch (match)
//    {
//        case PLANS:
//        case PLANBYID:
//        case ACTIVITIESBYPLANID:
//        case ACTIVITYBYACTIVITYIDANDPLANID:
//        case PLANMEMBERS:
//        case ACTIVITYMEMBERS:
//    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Log.v("easyplans", "match value "+ String.valueOf(match));
        Log.v("easyplans","inserting"+uri.toString());
        final SQLiteDatabase db = mPlansDBhelper.getWritableDatabase();
        Uri returi;
        switch (match) {
            case PLANS: {
                Log.v("easyplans","inserting plan "+values.get(PlansContract.plans.COLUMN_PLAN_NAME));
                long id = db.insert(PlansContract.plans.TABLE_NAME, null, values);
                if (id > 0)
                {
                    Log.v("easyplans","inserted plan with id"+id);
                    returi = buildplanuri(id);
                }
                else
                    throw new android.database.SQLException("Failed to insert plan row " + id);
                break;
            }

            case ACTIVITIESBYPLANID: {
                long id = db.insert(PlansContract.activity.TABLE_NAME, null, values);
                if (id > 0)
                    returi = PlansContract.activity.buildactivityuri(id);
                else
                    throw new android.database.SQLException("Failed to insert activity row " + id);
                break;
            }
            case PLANMEMBERS: {
                long id = db.insert(PlansContract.plan_members.TABLE_NAME, null, values);
                if (id > 0)
                    returi = PlansContract.plan_members.buildplanmemberuri(id);
                else
                    throw new android.database.SQLException("Failed to insert plan_member row " + id);
                break;
            }
            case ACTIVITYMEMBERS: {
                Log.v("easyplans"," values "+values.get(PlansContract.activity_members.COLUMN_ACTIVITY_ID)+" "+values.get(PlansContract.activity_members.COLUMN_MEMBER_ID));
                long id = db.insert(PlansContract.activity_members.TABLE_NAME, null, values);
                if (id > 0)
                    returi = PlansContract.members.buildmemberuri(id);
                else
                    throw new android.database.SQLException("Failed to insert member row " + id);
                break;
            }
            case ADDMEMBER: {
                Cursor c;
                try {
                    c = mPlansDBhelper.getReadableDatabase().query(
                            PlansContract.members.TABLE_NAME,
                            null,
                            PlansContract.members.COLUMN_MEMBER_NAME + " = " +
                                    values.get(PlansContract.members.COLUMN_MEMBER_NAME) + " AND " +
                                    PlansContract.members.COLUMN_MEMBER_NUMBER + " = " +
                                    values.get(PlansContract.members.COLUMN_MEMBER_NUMBER), null, null, null, null);
                } catch (SQLiteException s) {
                    c = null;
                }
                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                    returi = PlansContract.members.buildmemberuri(c.getLong(c.getColumnIndex(PlansContract.members.COLUMN_MEMBER_ID)));
                    break;
                } else {
                    long id = db.insert(PlansContract.members.TABLE_NAME, null, values);
                    if (id > 0)
                        returi = PlansContract.members.buildmemberuri(id);
                    else
                        throw new android.database.SQLException("Failed to insert member row " + id);
                    break;
                }
            }


                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        db.close();
        return returi;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mPlansDBhelper.getWritableDatabase();
        int rowsdeleted = 0;
        if(selection==null)
            selection="1";
        switch (match) {
            case PLANS:
                rowsdeleted=db.delete(PlansContract.plans.TABLE_NAME,selection,selectionArgs);
                break;
            case PLANBYID:{
                String id=PlansContract.plans.getplanidwithuri(uri);
                selection= PlansContract.plans.COLUMN_PLAN_ID+"="+id;
                rowsdeleted=db.delete(PlansContract.plans.TABLE_NAME,selection,null);
                selection=PlansContract.plan_members.COLUMN_PLAN_ID+" = "+id;
                db.delete(PlansContract.plan_members.TABLE_NAME,selection,null);
                selection=PlansContract.activity.COLUMN_PLAN_ID+" = "+id;
                Cursor c=db.query(PlansContract.activity.TABLE_NAME,null,selection,null,null,null,null);
                c.moveToFirst();
                do {
                    db.delete(PlansContract.activity_members.TABLE_NAME,
                            PlansContract.activity_members.COLUMN_ACTIVITY_ID+"="+
                                    c.getLong(c.getColumnIndex(PlansContract.activity._ID)),null);
                }while(c.moveToNext());
                db.delete(PlansContract.activity.TABLE_NAME,selection,null);
                selection= PlansContract.activity.COLUMN_PLAN_ID+"="+id;
                db.delete(PlansContract.activity.TABLE_NAME,selection,null);
                break;
            }
            case ACTIVITIESBYPLANID:{
                String id=PlansContract.activity.getplanidwithuri(uri);
                selection= PlansContract.activity.COLUMN_PLAN_ID+"="+id;
                rowsdeleted=db.delete(PlansContract.activity.TABLE_NAME,selection,null);
                break;
            }
            case ACTIVITYBYID:{
                String activity_id=PlansContract.activity.getactivityidwithuri(uri);
                activities a=activities.getactivitybyid(Long.parseLong(activity_id),mcontext);
                float activty_cost_per_mem=a.amount/a.members.toArray().length;
                for(int i=0;i<a.members.toArray().length;i++)
                {
                    updatecostofplanmembers(a.plan_id,a.members.get(i).member_id,a.amountbymembers[i]-activty_cost_per_mem);
                }
                db = mPlansDBhelper.getWritableDatabase();
                selection=PlansContract.activity.COLUMN_ID+"="+activity_id;
                rowsdeleted=db.delete(PlansContract.activity.TABLE_NAME,selection,null);
                break;
            }
            case MEMBER:{
                String member_id= (uri.getPathSegments().get(1));
                selection=PlansContract.members.COLUMN_MEMBER_ID+"="+member_id;
                rowsdeleted=db.delete(PlansContract.members.TABLE_NAME,selection,null);
                break;
            }
            case ACTIVITYMEMBERS: {
                String activity_id= uri.getPathSegments().get(1);
                selection=PlansContract.activity.COLUMN_ID+"="+activity_id;
                rowsdeleted=db.delete(PlansContract.activity_members.TABLE_NAME,selection,null);
                break;
            }
        }
        if(rowsdeleted>0)
            mcontext.getContentResolver().notifyChange(uri,null);
        db.close();
        return rowsdeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mPlansDBhelper.getWritableDatabase();
        int rowupdated = 0;
        if(selection==null)
            selection="1";
        switch (match) {
            case PLANBYID:{
                String id=PlansContract.plans.getplanidwithuri(uri);
                selection= PlansContract.plans.COLUMN_PLAN_ID+"="+id;
                rowupdated=db.update(PlansContract.plans.TABLE_NAME,values,selection,null);
                break;
            }

            case ACTIVITYBYID:{
                String activity_id=PlansContract.activity.getplanidwithuri(uri);
                selection=PlansContract.activity.COLUMN_ID+"="+activity_id;
                rowupdated=db.update(PlansContract.activity.TABLE_NAME,values,selection,null);
                break;
            }
            case MEMBER:{
                String member_id= (uri.getPathSegments().get(1));
                selection=PlansContract.members.COLUMN_MEMBER_ID+"="+member_id;
                rowupdated=db.update(PlansContract.members.TABLE_NAME,values,selection,null);
                break;
            }
            case ACTIVITYMEMBERS:{

            }
        }
        if(rowupdated>0)
            mcontext.getContentResolver().notifyChange(uri,null);
        db.close();
        return rowupdated;
    }
}
