package com.example.hp.easyplans;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import static com.example.hp.easyplans.R.id.plan_name;

public class plan_dets extends AppCompatActivity {

    plan p;
    TextView planname;
    TextView date;
    View main;
    View settlements;
    Boolean insettlements=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater=getLayoutInflater().from(this);
        main=inflater.inflate(R.layout.plan_dets,null);
        settlements=inflater.inflate(R.layout.settlements,null);
        setContentView(main);
        Bundle b=getIntent().getExtras();
        planname=(TextView)main.findViewById(plan_name);
        date=(TextView)main.findViewById(R.id.date);
        PlanProvider provider=new PlanProvider(this);
        long id=b.getLong(PlansContract.plans.COLUMN_PLAN_ID);
        Log.v("easyplan", String.valueOf(b.getLong(PlansContract.plans.COLUMN_PLAN_ID)));
        p=plan.getplan(this,id);
        planname.setText(p.name);
        date.setText(p.date);

    }
    public void activities_clicked(View v)
    {
        Intent intent=new Intent(this,plan_activities.class);
        intent.putExtra("plan_id",p._id);
        startActivity(intent);
    }
    public void members_clicked(View v)
    {

        Intent intent=new Intent(this,plan_members_dets.class);
        intent.putExtra("plan_id",p._id);
        startActivity(intent);
    }
    public void photos_clicked(View v)
    {
        Intent i=new Intent(this,photoes.class);
        i.putExtra(PlansContract.plans.COLUMN_PLAN_ID,p._id);
        startActivity(i);
    }
    public void settlements_clicked(View v)
    {
        setContentView(settlements);
        insettlements=true;
        TextView plan_name= (TextView) settlements.findViewById(R.id.settlements_plan_name);
        plan_name.setText(p.name);
        ListView settlements_list=(ListView)settlements.findViewById(R.id.settlements_list_view);
        PlanProvider provider=new PlanProvider(this);
        Cursor c=provider.getplanmembers(p._id,null,null);
        String[] cn=c.getColumnNames();
        for(int i=0;i<cn.length;i++)
        Log.v("easyplans",cn[i]);
        if(c.getCount()>0) {
            SimpleCursorAdapter settlements_adapter = new SimpleCursorAdapter(this, R.layout.list_item_settlements, c, new String[]{
                    PlansContract.members.COLUMN_MEMBER_NAME,
                    PlansContract.plan_members.COLUMN_AMOUNT_DUE}, new int[]{
                    R.id.list_items_settlements_membername,
                    R.id.list_items_settlements_amount
            }
            );
            settlements_list.setAdapter(settlements_adapter);
            setListViewHeightBasedOnChildren(settlements_list);
        }

    }
    private void setListViewHeightBasedOnChildren(ListView listView) {
        Log.e("Listview Size ", "" + listView.getCount());
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {

            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    @Override
    public void onBackPressed() {
        if(insettlements)
        {
            setContentView(main);
            insettlements=false;
            return;
        }
        super.onBackPressed();
    }

    public void add_activity_clicked(View v)
    {
        Intent i=new Intent(this,addactivity.class);
        i.putExtra("plan_id",p._id);
        startActivity(i);
    }
}
