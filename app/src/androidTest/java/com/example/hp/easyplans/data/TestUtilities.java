package com.example.hp.easyplans.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.test.AndroidTestCase;

import com.example.hp.easyplans.PlansContract;
import com.example.hp.easyplans.PlansDBhelper;
import com.example.hp.easyplans.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createplanvalues(@Nullable long memberid) {
        ContentValues planValues = new ContentValues();
        planValues.put(PlansContract.plans.COLUMN_PLAN_NAME, "BIRTHDAY");
        planValues.put(PlansContract.plans.COLUMN_DATE, TEST_DATE);
        planValues.put(PlansContract.plans.COLUMN_MEMBERS_ID, memberid);

        return planValues;
    }
    static ContentValues createmembervalues() {
        ContentValues memberValues = new ContentValues();
        memberValues.put(PlansContract.members.COLUMN_MEMBER_NAME, "nipun");
        memberValues.put(PlansContract.members.COLUMN_MEMBER_NUMBER, "9873978508");
        return memberValues;
    }
    static ContentValues createactivityvalues(long planid,@Nullable long memberid) {
        ContentValues activityValues = new ContentValues();
        activityValues.put(PlansContract.activity.COLUMN_PLAN_ID, planid);
        activityValues.put(PlansContract.activity.COLUMN_COST, 900);
        activityValues.put(PlansContract.activity.COLUMN_MEMBERS_ID, memberid);

        return activityValues;
    }



    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    static long insertmembervalues(Context context) {
        // insert our test records into the database
        PlansDBhelper dbHelper = new PlansDBhelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createmembervalues();

        long memberRowId;
        memberRowId = db.insert(PlansContract.members.TABLE_NAME, null, testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert member Values", memberRowId != -1);

        return memberRowId;
    }
    static long[] insertplanvalues(Context context) {
        // insert our test records into the database
        PlansDBhelper dbHelper = new PlansDBhelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long memberid=insertmembervalues(context);
        ContentValues testValues = TestUtilities.createplanvalues(memberid);

        long planRowId;
        planRowId = db.insert(PlansContract.plans.TABLE_NAME, null, testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert plan Values", planRowId != -1);

        return new long[]{planRowId,memberid};
    }

    static long insertactivityvalues(Context context) {
        // insert our test records into the database
        PlansDBhelper dbHelper = new PlansDBhelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long[] id=insertplanvalues(context);
        ContentValues testValues = TestUtilities.createactivityvalues(id[0],id[1]);
        long planRowId;
        planRowId = db.insert(PlansContract.activity.TABLE_NAME, null, testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert activity Values", planRowId != -1);

        return planRowId;
    }
    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
