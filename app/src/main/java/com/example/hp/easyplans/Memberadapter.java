package com.example.hp.easyplans;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HP on 7/7/2017.
 */

class Memberadapter extends ArrayAdapter<Member> {



    public Memberadapter(@NonNull Context context, @NonNull List<Member> objects) {
        super(context, R.layout.list_item_member,objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= LayoutInflater.from(getContext());
        View memview=inflater.inflate(R.layout.list_item_member,parent,false);
        TextView name=(TextView) memview.findViewById(R.id.list_item_members_name);
        Member item=getItem(position);
        name.setText(item.name);
        return memview;
    }
}
