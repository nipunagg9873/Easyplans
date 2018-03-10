package com.example.hp.easyplans;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HP on 6/11/2017.
 */

public class plan_list_adapter extends ArrayAdapter<plan> {
    public plan_list_adapter(@NonNull Context context, ArrayList<plan> plist) {
            super(context, R.layout.list_item_plans,plist);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inf= LayoutInflater.from(getContext());
        View plan_list_item=inf.inflate(R.layout.list_item_plans,parent,false);
        TextView plan_name= (TextView) plan_list_item.findViewById(R.id.list_item_plan);
        plan dataitem=getItem(position);
        plan_name.setText(dataitem.getName());
        return  plan_list_item;
    }
}
