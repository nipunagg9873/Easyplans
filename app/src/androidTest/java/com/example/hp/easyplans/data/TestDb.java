/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hp.easyplans.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.hp.easyplans.PlansContract;
import com.example.hp.easyplans.PlansDBhelper;

import java.util.HashSet;

;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(PlansDBhelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(PlansContract.plans.TABLE_NAME);
        tableNameHashSet.add(PlansContract.activity.TABLE_NAME);
        tableNameHashSet.add(PlansContract.members.TABLE_NAME);

        mContext.deleteDatabase(PlansDBhelper.DATABASE_NAME);
        SQLiteDatabase db = new PlansDBhelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + PlansContract.plans.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> plansColumnHashSet = new HashSet<String>();
        plansColumnHashSet.add(PlansContract.plans.COLUMN_PLAN_ID);
        plansColumnHashSet.add(PlansContract.plans.COLUMN_DATE);
        plansColumnHashSet.add(PlansContract.plans.COLUMN_PLAN_NAME);
        plansColumnHashSet.add(PlansContract.plans.COLUMN_MEMBERS_ID);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            plansColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The plans doesn't contain all of the required plans columns",
                plansColumnHashSet.isEmpty());
        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + PlansContract.activity.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> activityColumnHashSet = new HashSet<String>();
        activityColumnHashSet.add(PlansContract.activity.COLUMN_ACTIVITY_ID);
        activityColumnHashSet.add(PlansContract.activity.COLUMN_PLAN_ID);
        activityColumnHashSet.add(PlansContract.activity.COLUMN_COST);
        activityColumnHashSet.add(PlansContract.activity.COLUMN_MEMBERS_ID);
        int columnNameIndex2 = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex2);
            activityColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The activity doesn't contain all of the required plans columns",
                activityColumnHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + PlansContract.members.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> membersColumnHashSet = new HashSet<String>();
        membersColumnHashSet.add(PlansContract.members.COLUMN_MEMBER_ID);
        membersColumnHashSet.add(PlansContract.members.COLUMN_MEMBER_NAME);
        membersColumnHashSet.add(PlansContract.members.COLUMN_MEMBER_NUMBER);
        int columnNameIndex3 = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex3);
            membersColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The members doesn't contain all of the required plans columns",
                membersColumnHashSet.isEmpty());

        db.close();
    }

//    /*
//        Students:  Here is where you will build code to test that we can insert and query the
//        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
//        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
//        also make use of the ValidateCurrentRecord function from within TestUtilities.
//    */
//
// public void testplanTable() {
//     long activityRowId=TestUtilities.insertactivityvalues(mContext);
//     assertFalse("Error: Location Not Inserted Correctly", activityRowId == -1L);
//        PlansDBhelper dbHelper = new PlansDBhelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//     Cursor activityCursor = db.query(
//                PlansContract.activity.TABLE_NAME,  // Table to Query
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null, // columns to group by
//                null, // columns to filter by row groups
//                null  // sort order
//        );
//
//        // Move the cursor to the first valid database row and check to see if we have any rows
//        assertTrue( "Error: No Records returned from location query", activityCursor.moveToFirst() );
//
//        // Fifth Step: Validate the location Query
//        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
//                activityCursor, activityValues);
//
//        // Move the cursor to demonstrate that there is only one record in the database
//        assertFalse( "Error: More than one record returned from weather query",
//                weatherCursor.moveToNext() );
//
//        // Sixth Step: Close cursor and database
//        weatherCursor.close();
//        dbHelper.close();
//    }
//
//
//    /*
//        Students: This is a helper method for the testWeatherTable quiz. You can move your
//        code from testLocationTable to here so that you can call this code from both
//        testWeatherTable and testLocationTable.
//     */
//    public long insertLocation() {
//        // First step: Get reference to writable database
//        // If there's an error in those massive SQL table creation Strings,
//        // errors will be thrown here when you try to get a writable database.
//        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        // Second Step: Create ContentValues of what you want to insert
//        // (you can use the createNorthPoleLocationValues if you wish)
//        ContentValues testValues = com.example.hp.easyplans.TestUtilities.createNorthPoleLocationValues();
//
//        // Third Step: Insert ContentValues into database and get a row ID back
//        long locationRowId;
//        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);
//
//        // Verify we got a row back.
//        assertTrue(locationRowId != -1);
//
//        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
//        // the round trip.
//
//        // Fourth Step: Query the database and receive a Cursor back
//        // A cursor is your primary interface to the query results.
//        Cursor cursor = db.query(
//                WeatherContract.LocationEntry.TABLE_NAME,  // Table to Query
//                null, // all columns
//                null, // Columns for the "where" clause
//                null, // Values for the "where" clause
//                null, // columns to group by
//                null, // columns to filter by row groups
//                null // sort order
//        );
//
//        // Move the cursor to a valid database row and check to see if we got any records back
//        // from the query
//        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );
//
//        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
//        // (you can use the validateCurrentRecord function in TestUtilities to validate the
//        // query if you like)
//        com.example.hp.easyplans.TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
//                cursor, testValues);
//
//        // Move the cursor to demonstrate that there is only one record in the database
//        assertFalse( "Error: More than one record returned from location query",
//                cursor.moveToNext() );
//
//        // Sixth Step: Close Cursor and Database
//        cursor.close();
//        db.close();
//        return locationRowId;
//    }
}
