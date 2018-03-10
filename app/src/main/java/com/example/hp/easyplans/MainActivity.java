package com.example.hp.easyplans;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int SETTINGS_CHANGED=2;
    public ArrayList<plan> planlist = new ArrayList<>();
    plan_list_adapter adapter;
    ListView l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plans);
            if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id=item.getItemId();
        if(id==R.id.action_settings)
        {
            Intent i=new Intent(this,SettingsActivity.class);
            startActivityForResult(i,SETTINGS_CHANGED);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SETTINGS_CHANGED)
        {
                SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
                long id=Long.parseLong(sp.getString(getString(R.string.user_member_id_key),"1"));
                PlanProvider provider=new PlanProvider(this);
                ContentValues cv=new ContentValues();
                cv.put(PlansContract.members.COLUMN_MEMBER_NAME,sp.getString(getString(R.string.username_key),""));
                cv.put(PlansContract.members.COLUMN_MEMBER_NUMBER,Long.parseLong(sp.getString(getString(R.string.user_contact_number_key),"")));
                provider.update(
                        PlansContract.BASE_CONTENT_URI.buildUpon().appendPath(PlansContract.PATH_MEMBERS).appendPath(String.valueOf(id)).build(),
                        cv,null,null);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addcicked(View v) {
        Intent i = new Intent(this, addplan.class);
        startActivity(i);


    }

    @Override
    protected void onStart() {
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        String username=sp.getString(getString(R.string.username_key),"not set");
        String contact_no=sp.getString(getString(R.string.user_contact_number_key),"not set");
        Log.v("easyplans",sp.getString(getString(R.string.username_key),"not set"));
        Log.v("easyplans",sp.getString(getString(R.string.user_contact_number_key),"not set"));
        if(username.equalsIgnoreCase("not set")||contact_no.equalsIgnoreCase("not set"))
        {
            Log.v("easyplan","inside if");
            Intent i=new Intent(this,Activity_get_user_data.class);
            startActivity(i);
        }

        loadlist();
        super.onStart();
    }

    public void loadlist() {
         l = (ListView) findViewById(R.id.listview_plans);
        PlanProvider provider = new PlanProvider(this);
        final Cursor plans = provider.query(PlansContract.BASE_CONTENT_URI.buildUpon().appendPath(PlansContract.PATH_PLANS).build(), null, null, null, PlansContract.plans.COLUMN_DATE+" DESC");
        try{
            if (plans != null) {
                planlist.clear();
                plans.moveToFirst();
                do {
                    plan plist = new plan();
                    plist.name = plans.getString(plans.getColumnIndex(PlansContract.plans.COLUMN_PLAN_NAME));
                    plist._id = plans.getLong(plans.getColumnIndex(PlansContract.plans.COLUMN_PLAN_ID));
                    planlist.add(plist);
                }while (plans.moveToNext());
            }
        }catch(CursorIndexOutOfBoundsException e)
        {
            Log.e("easyplans",e.toString());
        }
        if (planlist.isEmpty())
            planlist.add(new plan("no plans to display", 0));
        adapter = new plan_list_adapter(this, planlist);
        l.setAdapter(adapter);
        final Intent i = new Intent(this, plan_dets.class);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                plans.moveToPosition(position);
                long item_id = plans.getLong(plans.getColumnIndex(PlansContract.plans.COLUMN_PLAN_ID));
                i.putExtra(PlansContract.plans.COLUMN_PLAN_ID, item_id);
                Log.v("easyplans", String.valueOf(item_id));
                startActivity(i);
            }
        });
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long del_id=planlist.get(position)._id;
                AlertDialog.Builder adbuilder=new AlertDialog.Builder(MainActivity.this);
                final CharSequence[] items={getString(R.string.edit),getString(R.string.delete)};
                adbuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                break;
                            }
                            case 1: {
                                PlanProvider provider = new PlanProvider(MainActivity.this);
                                provider.delete(PlansContract.plans.buildplanuri(del_id), null, null);
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(getString(R.string.success))
                                        .setMessage(getString(R.string.plan_deleted))
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                recreate();
                                            }
                                        }).show();
                            break;
                            }

                        }
                    }
                });
                adbuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adbuilder.show();
                return true;
            }


        });

    }

}
