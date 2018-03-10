package com.example.hp.easyplans;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class plan_activities extends AppCompatActivity {


    plan p;
    activities activity_selected;
    long id;
    Cursor planactivities;
    ListView activitylist;
    TextView planname;
    Boolean inmain = true;
    PlanProvider provider = new PlanProvider(this);
    SimpleCursorAdapter myadapter;
    private final int ADDACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_activities);
        inmain = true;
        id = getIntent().getLongExtra("plan_id", 0);
        p = plan.getplan(this, id);
        planname = (TextView) findViewById(R.id.plan_name_activities_top);
        activitylist = (ListView) findViewById(R.id.listview_activities);
        planname.setText(p.name);
        planactivities = provider.getactivitieswithplanid(id, new String[]{
                        PlansContract.activity.COLUMN_ID, PlansContract.activity.COLUMN_ACTIVITY_NAME, PlansContract.activity.COLUMN_COST
                },
                null);
        if (planactivities.getCount() != 0) {
            String[] FROM_COLUMNS = new String[]{
                    PlansContract.activity.COLUMN_ACTIVITY_NAME, PlansContract.activity.COLUMN_COST
            };
            int[] TO_IDS = new int[]{
                    R.id.activity_list_activity_name, R.id.activity_list_activity_amount
            };
            try {
                myadapter = new SimpleCursorAdapter(this, R.layout.list_item_activities, planactivities, FROM_COLUMNS, TO_IDS);
            } catch (Exception e) {
                Log.e("easyplans", e.toString());
            }
            activitylist.setAdapter(myadapter);
            activitylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final long selected_id = id;
                    AlertDialog.Builder adbuilder = new AlertDialog.Builder(plan_activities.this);
                    final CharSequence[] items = {getString(R.string.edit), getString(R.string.delete)};
                    adbuilder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: {
                                    Intent i = new Intent(plan_activities.this,edit_activity.class);
                                    i.putExtra("activity_id", selected_id);
                                    startActivity(i);
                                    break;
                                }
                                case 1: {


                                    activities a = activities.getactivitybyid(selected_id, plan_activities.this);
                                    Member[] activitymembers = new Member[a.members.toArray().length];
                                    a.members.toArray(activitymembers);
                                    Log.v("easyplans", "activity members " + a.members.toArray().length);
                                    PlanProvider provider = new PlanProvider(plan_activities.this);
                                    provider.delete(PlansContract.activity.buildactivityuri(selected_id), null, null);
                                    new AlertDialog.Builder(plan_activities.this)
                                            .setTitle(getString(R.string.success))
                                            .setMessage(getString(R.string.activity_deleted))
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
                    adbuilder.setNegativeButton(

                            getString(R.string.cancel), new DialogInterface.OnClickListener()

                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    adbuilder.show();
                    return true;
                }


            });
            activitylist.setOnItemClickListener(new AdapterView.OnItemClickListener()

            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    activity_dets(id);
                }
            });

        }
    }

    public void addclicked(View v) {
        Intent i = new Intent(this, addactivity.class);
        i.putExtra("plan_id", p._id);
        startActivityForResult(i, ADDACTIVITY);
    }

    public void activity_dets(long id) {
        activity_selected = activities.getactivitybyid(id, this);
        View activity_dets = getLayoutInflater().inflate(R.layout.activity_dets, null);
        TextView activity_name = (TextView) activity_dets.findViewById(R.id.activity_dets_name);
        TextView cost_amount = (TextView) activity_dets.findViewById(R.id.activity_dets_cost_amount);
        activity_name.setText(activity_selected.name);
        cost_amount.setText("Rs. " + String.valueOf(activity_selected.amount));
        setContentView(activity_dets);
        inmain = false;

    }

    public void members_clicked(View v) {
        View member_dets = getLayoutInflater().inflate(R.layout.activity_member_dets, null);
        setContentView(member_dets);
        Cursor activity_members = provider.getactivitymembers(activity_selected._id, null, null);
        ListView activity_member_list = (ListView) member_dets.findViewById(R.id.activity_member_dets_list);
        SimpleCursorAdapter member_list_adapter;
        String[] from = new String[]{
                PlansContract.members.COLUMN_MEMBER_NAME
        };
        int[] ids = new int[]{
                R.id.activity_member_dets_list_item_name
        };
        if (activity_members != null) {
            member_list_adapter = new SimpleCursorAdapter(this, R.layout.activity_member_dets_list_item, activity_members, from, ids);
            activity_member_list.setAdapter(member_list_adapter);
        }
    }

    public void photos_clicked(View v) {
        Intent i=new Intent(this,photoes.class);
        i.putExtra(PlansContract.plans.COLUMN_PLAN_ID,p._id);
        startActivity(i);
    }

    public void settlements_clicked(View v) {
        View activity_dets_settlements = getLayoutInflater().inflate(R.layout.activity_dets_settlements, null);
        setContentView(activity_dets_settlements);
        Cursor activity_members = provider.getactivitymembers(activity_selected._id, null, PlansContract.activity_members.COLUMN_PAID + " DESC");
        ListView activity_member_list = (ListView) activity_dets_settlements.findViewById(R.id.settlements_list_view);
        TextView cost = (TextView) activity_dets_settlements.findViewById(R.id.activity_dets_settlements_cost);
        SimpleCursorAdapter settlements_list_adapter;
        String[] from = new String[]{
                PlansContract.members.COLUMN_MEMBER_NAME,
                PlansContract.activity_members.COLUMN_PAID
        };
        int[] ids = new int[]{
                R.id.list_items_settlements_membername,
                R.id.list_items_settlements_amount
        };
        if (activity_members != null) {
            settlements_list_adapter = new SimpleCursorAdapter(this, R.layout.list_item_settlements, activity_members, from, ids);
            activity_member_list.setAdapter(settlements_list_adapter);
        }
        cost.setText("Rs. " + String.valueOf(activity_selected.amount / activity_members.getCount()));
    }

    public void edit_activity_clicked(View v) {
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADDACTIVITY) {
            recreate();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!inmain) {
            recreate();
            return;
        }
        super.onBackPressed();
    }
}
