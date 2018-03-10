package com.example.hp.easyplans;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.example.hp.easyplans.R.id.paid_by_list;

public class addactivity extends AppCompatActivity {

    activities a = new activities();
    PlanProvider provider;
    ListView activitymemberslistview;
    Cursor members;
    View main;
    View editview;
    View paid_by;
    Memberadapter adapter;
    Boolean inedit = false;
    Boolean inpaidby = false;
    Boolean[] itemchecked;
    ListView paidbylist;
    ArrayAdapter<Member> paidbymemberadapter;
    Memberadapter activitymemberadapter;
    ArrayAdapter<Member> paid_by_list_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = getLayoutInflater().inflate(R.layout.activity_addactivity, null);
        setContentView(main);
        a.plan_id = getIntent().getLongExtra("plan_id", 0);
        activitymemberslistview = (ListView) findViewById(R.id.add_activity_member_list);
        Log.v("easyplans", String.valueOf(a.plan_id));
        paid_by_list_adapter = new ArrayAdapter<Member>(this, R.layout.add_activity_paid_by_list_item, a.members) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater().from(getContext());
                View v = inflater.inflate(R.layout.add_activity_paid_by_list_item, null);
                TextView name = (TextView) v.findViewById(R.id.main_paid_by_list_item_name);
                TextView amount = (TextView) v.findViewById(R.id.main_paid_by_list_item_amount);
                Member item = getItem(position);
                name.setText(item.name);
                amount.setText(String.valueOf(a.amountbymembers[position]));
                return v;

            }
        };
        activitymemberslistview.setAdapter(paid_by_list_adapter);
        provider = new PlanProvider(this);
        loadmembers();
        activitymemberadapter = new Memberadapter(this, a.members);
        if (activitymemberadapter == null) {
            Log.v("easyplans", "can't find adapter");
        }

    }

    void loadmembers() {
        members = provider.getplanmembers(a.plan_id, null, null);
        members.moveToFirst();
        a.members.clear();
        for (int i = 0; i < members.getCount(); i++) {
            Member m = new Member(members.getString(members.getColumnIndex(PlansContract.members.COLUMN_MEMBER_NAME)),
                    members.getString(members.getColumnIndex(PlansContract.members.COLUMN_MEMBER_NAME)));
            m.member_id=members.getLong(members.getColumnIndex(PlansContract.members.COLUMN_MEMBER_ID));
            a.members.add(m);
            members.moveToNext();
        }
        a.amountbymembers = new long[a.members.toArray().length];
        Log.v("easyplans", "members loaded");
    }

    class Memberadapter extends ArrayAdapter<Member> {


        public Memberadapter(@NonNull Context context, @NonNull List<Member> objects) {
            super(context, R.layout.list_item_member, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater().from(getContext());
            View v = inflater.inflate(R.layout.add_activity_paid_by_list_item, null);
            TextView name = (TextView) v.findViewById(R.id.main_paid_by_list_item_name);
            TextView amount = (TextView) v.findViewById(R.id.main_paid_by_list_item_amount);
            Member item = getItem(position);
            name.setText(item.name);
            amount.setText(String.valueOf(a.amountbymembers[position]));
            return v;
        }
    }

    public void editmembersclicked(View v) {
        editview = getLayoutInflater().inflate(R.layout.editmembers, null);
        setContentView(editview);
        inedit = true;
        ListView memberslist = (ListView) editview.findViewById(R.id.editmembers_list);
        adapter = new Memberadapter(this, a.members);
        itemchecked = new Boolean[a.members.toArray().length];
        for (int i = 0; i < itemchecked.length; i++)
            itemchecked[i] = false;
        memberslist.setAdapter(adapter);
        memberslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (itemchecked[position]) {
                    itemchecked[position] = false;
                    view.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    itemchecked[position] = true;
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (inedit || inpaidby) {
            setContentView(main);
            inedit = false;
            inpaidby=false;
        } else
            super.onBackPressed();
    }

    public void saveclicked(View v) {
        activitymemberadapter.notifyDataSetChanged();
        TextView amount = (TextView) main.findViewById(R.id.add_activity_bill_amount);
        amount.setText("Rs. " + a.amount);
        onBackPressed();
    }

    public void deleteclicked(View v) {
        int s = 0;
        for (int i = 0; i < itemchecked.length - s; i++) {
            if (itemchecked[i]) {
                a.members.remove(i);
                for (int j = i; j < itemchecked.length - s - 1; j++) {
                    itemchecked[j] = itemchecked[j + 1];
                }
                try {
                    a.amount -= a.amountbymembers[i];
                    for (int j = i; j < a.amountbymembers.length - 1; j++) {
                        a.amountbymembers[j] = a.amountbymembers[j + 1];
                    }
                } catch (Exception e) {
                    Log.v("easyplans", e.toString());
                }
                s++;
                i--;

            }
        }
        for (int i = 0; i < itemchecked.length; i++)
            itemchecked[i] = false;
        adapter.notifyDataSetChanged();
        paid_by_list_adapter.notifyDataSetChanged();
    }

    public void resetclicked(View v) {
        loadmembers();
        adapter.notifyDataSetChanged();
        activitymemberadapter.notifyDataSetChanged();
    }

    public void paidbyclicked(View v) {

        paid_by = getLayoutInflater().inflate(R.layout.activity_add_activiies_paid_by, null);
        setContentView(paid_by);
        inpaidby = true;
        main.cancelPendingInputEvents();
        final TextView[] ev = new TextView[1];
        paidbylist = (ListView) paid_by.findViewById(paid_by_list);
        paidbylist.setItemsCanFocus(true);
        paidbymemberadapter = new ArrayAdapter<Member>(this, R.layout.paid_by_list_item, a.members) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View v = inflater.inflate(R.layout.paid_by_list_item, null);
                Member item = getItem(position);
                TextView name = (TextView) v.findViewById(R.id.paid_by_list_item_name);
                name.setText(item.name);
                ev[0] = (TextView) v.findViewById(R.id.paid_by_list_item_amount);
                ev[0].setText(String.valueOf(a.amountbymembers[position]));
                return v;
            }
        };
        paidbylist.setAdapter(paidbymemberadapter);
        paidbylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(addactivity.this);
                builder.setTitle(getString(R.string.amount_paid));
                final TextView tv=(TextView)view.findViewById(R.id.paid_by_list_item_amount);
                final EditText et=new EditText(addactivity.this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(et);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        a.amountbymembers[position]=Long.parseLong(et.getText().toString());
                        tv.setText(String.valueOf(a.amountbymembers[position]));
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

    }

    public void paid_by_done_clicked(View v) {
        a.amount = 0;
        for (int i = 0; i < a.members.toArray().length; i++) {
                a.amount += a.amountbymembers[i];

        }
        TextView amount = (TextView) main.findViewById(R.id.add_activity_bill_amount);
        amount.setText("Rs. " + a.amount);
        setContentView(main);
        paid_by_list_adapter.notifyDataSetChanged();
        inpaidby = false;
    }

    public void submitclicked(View v) {
        EditText name = (EditText) main.findViewById(R.id.add_activity_name_text);
        if (name.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Name For Activity", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.v("easyplans", name.getText().toString());
            a.name = name.getText().toString();
            activities.putindatabase(getBaseContext(), a);
            finish();
        }
    }

}


