package com.example.hp.easyplans;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.util.Log;

/**
 * Created by HP on 6/28/2017.
 */

public class PlansContract {
    public static final String CONTENT_AUTHORITY="com.example.hp.easyplans";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PLANS="plans";
    public static final String PATH_ACTIVITY="activity";
    public static final String PATH_MEMBERS="members";
    public static final String PATH_PLAN_MEMBERS="planmembers";
    public static long normalizeDate(long startdate) {
        Time time = new Time();
        time.set(startdate);
        int juliandate = Time.getJulianDay(startdate, time.gmtoff);
        return time.setJulianDay(juliandate);
    }
    public static class plans implements BaseColumns {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANS).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PLANS;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PLANS;
        public static final String TABLE_NAME="plans_table";
        public static final String COLUMN_PLAN_ID="plan_id";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_PLAN_NAME="plan_name";
        public static Uri buildplanuri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static String getplanidwithuri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

    }
    public static class activity implements BaseColumns {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITY).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_ACTIVITY;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_ACTIVITY;
        public static final String TABLE_NAME="activity_table";
        public static final String COLUMN_ID="_id";
        public static final String COLUMN_ACTIVITY_NAME="activity_name";
        public static final String COLUMN_PLAN_ID="plan_id";
        public static final String COLUMN_COST="cost";

        public static Uri buildactivityuri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildactivitywithplanid(Long id)
        {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
        public static String getplanidwithuri(Uri uri)
        {
            return uri.getPathSegments().get(1);

        }
        public static String getactivityidwithuri(Uri uri)
        {
            Log.v("easyplans",uri.getPathSegments().get(1));
            return uri.getPathSegments().get(1);

        }

    }
    public static class members implements BaseColumns {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEMBERS).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MEMBERS;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MEMBERS;
        public static final String TABLE_NAME="members_table";
        public static final String COLUMN_MEMBER_ID="member_id";
        public static final String COLUMN_MEMBER_NAME="name";
        public static final String COLUMN_MEMBER_NUMBER="number";

        public static Uri buildmemberuri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildmemberwithplanid(Long id)
        {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
        public static String getplanidwithuri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }
    }
    public static class plan_members implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAN_MEMBERS).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PLAN_MEMBERS;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PLAN_MEMBERS;
        public static final String TABLE_NAME="plan_members_table";
        public static final String COLUMN_ID="_id";
        public static final String COLUMN_MEMBER_ID="member_id";
        public static final String COLUMN_PLAN_ID="plan_id";
        public static final String COLUMN_AMOUNT_DUE="amount_due";

        public static Uri buildplanmemberuri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }
    public static class activity_members implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAN_MEMBERS).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PLAN_MEMBERS;
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PLAN_MEMBERS;
        public static final String TABLE_NAME="activity_members_table";
        public static final String COLUMN_ID="_id";
        public static final String COLUMN_MEMBER_ID="member_id";
        public static final String COLUMN_ACTIVITY_ID="activity_id";
        public static final String COLUMN_PAID="paid";

        public static Uri buildactivitymemberuri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }

}
