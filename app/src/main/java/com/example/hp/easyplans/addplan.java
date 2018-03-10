package com.example.hp.easyplans;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class addplan extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private plan p;
    EditText name;
    DatePicker date;
    ListView memberslist;
    Memberadapter memberadapter;
    long[] membersid;
    String[] memberslookupkeys;
    int noofmems;
    TextView noofmems_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plan);
        p=new plan();
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        p.default_member_id= Integer.parseInt(sp.getString("user_member_id_key","1"));
        name=(EditText)findViewById(R.id.add_plan_name);
        date=(DatePicker)findViewById(R.id.add_plan_date);
        memberslist=(ListView)findViewById(R.id.add_plan_member_list);
        memberadapter=new Memberadapter(this,p.members);
        memberslist.setAdapter(memberadapter);
        noofmems_view=(TextView)findViewById(R.id.add_plan_no_of_mems);
        noofmems_view.setText(getString(R.string.members_added)+" "+(noofmems+1));

    }


    final int REQUEST_CODE_PICK_CONTACT=1;
    public void add_member_clicked(View v)
    {

        Intent phonebookIntent = new Intent(this,getcontacts.class);
        phonebookIntent.putExtra("selected_contacts_ids",membersid);
        phonebookIntent.putExtra("selected_contacts_keys",memberslookupkeys);
        phonebookIntent.putExtra("noofmems",noofmems);
        startActivityForResult(phonebookIntent,REQUEST_CODE_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_PICK_CONTACT)
        {
            if(resultCode==RESULT_OK)
            {
                membersid=data.getLongArrayExtra("resultids");
                memberslookupkeys=data.getStringArrayExtra("resultkeys");
                noofmems=data.getIntExtra("noofmems",0);
                Log.v("sunshine",membersid[0]+" "+memberslookupkeys[0]);
                Member[] members=Member.getmembers(this,membersid,memberslookupkeys,noofmems);
                p.memids=Member.insertmemsindatabase(this,members,noofmems);
                reloadlist();
                memberadapter.notifyDataSetChanged();
                noofmems_view.setText(getString(R.string.members_added)+" "+(noofmems+1));
                Toast.makeText(this,noofmems+" members added ",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void submitclicked(View v)
    {
        if(name.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Please Enter Name For Plan",Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            Log.v("sunshine",name.getText().toString());
            p.name=name.getText().toString();
            int day=date.getDayOfMonth();
            int month=date.getMonth();
            int year=date.getYear();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yy");
            p.date=simpleDateFormat.format(new Date(year,month,day));
            plan.putindatabase(getBaseContext(),p);
            finish();
        }
    }
    public void reloadlist()
    {
        PlanProvider provider=new PlanProvider(this);
        p.members.clear();
        p.members.add(p.initmem);
        for(int i=0;i<p.memids.length;i++)
        {
            Cursor c=provider.getmemberbyid(p.memids[i],null,null);
            c.moveToFirst();
            Member m=new Member(c.getString(c.getColumnIndex(PlansContract.members.COLUMN_MEMBER_NAME)),
                    c.getString(c.getColumnIndex(PlansContract.members.COLUMN_MEMBER_NAME)));
            p.members.add(m);
        }
    }


}
