package com.example.hp.easyplans;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class plan_members_dets extends AppCompatActivity {
    ListView memberslist;
    SimpleCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_members_dets);
        Bundle b=getIntent().getExtras();
        PlanProvider provider=new PlanProvider(this);
        Cursor c=provider.getplanmembers(b.getLong("plan_id"),null,null);
        memberslist=(ListView)findViewById(R.id.plan_members_dets_list);
        String[] from=new String[]{
                PlansContract.members.COLUMN_MEMBER_NAME
        };
        int[] ids= new int[]{
                R.id.plan_members_dets_list_item_name
        };
        if(c!=null) {
            adapter=new SimpleCursorAdapter(this,R.layout.plan_member_dets_list_item,c,from,ids);
            memberslist.setAdapter(adapter);
        }
    }
    public void edit_members_clicked(View v)
    {
        return;
    }
}
