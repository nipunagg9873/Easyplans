package com.example.hp.easyplans;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_get_user_data extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_data);
    }
    public void submitclicked(View v)
    {
        EditText name=(EditText)findViewById(R.id.Activity_get_user_data_username);
        EditText contact_no=(EditText)findViewById(R.id.Activity_get_user_data_contact_no);
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=sp.edit();
        String username=name.getText().toString();
        Long number=Long.parseLong(contact_no.getText().toString());
        if(username.isEmpty()||number==null)
        {
            Toast.makeText(this,"Please enter valid data",Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            editor.putString(getString(R.string.username_key),username);
            editor.putString(getString(R.string.user_contact_number_key),number.toString());
            editor.commit();
            ContentValues cv=new ContentValues();
            cv.put(PlansContract.members.COLUMN_MEMBER_NAME,username);
            cv.put(PlansContract.members.COLUMN_MEMBER_NUMBER,number);
            PlanProvider provider=new PlanProvider(this);
            Uri uri=provider.insert(PlansContract.BASE_CONTENT_URI.buildUpon().appendPath(PlansContract.PATH_MEMBERS).build(),cv);
            Log.v("easyplans",getString(R.string.user_member_id_key)+uri.getPathSegments().get(1));
            editor.putString(getString(R.string.user_member_id_key),uri.getPathSegments().get(1));
            finish();
        }
    }
}
