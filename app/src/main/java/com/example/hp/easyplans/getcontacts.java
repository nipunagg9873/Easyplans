package com.example.hp.easyplans;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class getcontacts extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView contactslist;
    long[] selectedids=new long[20];
    String[] lookupkeys=new String[20];
    int p=0;
    SimpleCursorAdapter myadapter;
    private String[] From_COLUMNS={
      ContactsContract.Contacts.DISPLAY_NAME
    };
    private int[] TO_IDS={
      R.id.add_contacts_list_item_name,
    };
    CheckedTextView ctv;
    Cursor c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcontacts);
        c= getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        c.moveToFirst();
        int count=c.getCount();
        Bundle b=getIntent().getExtras();
        long[] preselectedids=b.getLongArray("selected_contacts_ids");
        String[] prelookupkeys=b.getStringArray("selected_contacts_keys");
        p=b.getInt("noofmems",0);
        if(preselectedids!=null)
        {
            for(int i=0;i<p;i++)
            {
                selectedids[i]=preselectedids[i];
                lookupkeys[i]=prelookupkeys[i];
            }
        }
        else
            p=0;
        final Boolean[] set=new Boolean[count];
        contactslist= (ListView) findViewById(R.id.add_contacts_list);
        for(int k=0;k<count;k++)
        {
            set[k]=false;

            for(int i=0;i<p;i++)
            {
                c.moveToPosition(k);
                if(c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID))==selectedids[i])
                {
                    set[k]=true;

                }
            }
        }
        myadapter=new SimpleCursorAdapter(this,R.layout.activity_addcontacts_list_item,c,From_COLUMNS,TO_IDS){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v= super.getView(position, null, parent);
                CheckedTextView ctv=(CheckedTextView)v.findViewById(R.id.add_contacts_list_item_name);
                Cursor c=myadapter.getCursor();
                if(inselectedids(c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID))))
                    ((ListView)parent).setItemChecked(position,true);
                return v;
            }
        };
        myadapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                        null,
                        ContactsContract.Contacts.DISPLAY_NAME+" LIKE '%"+constraint.toString()+"%'",null,ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
            }
        });
        contactslist.setAdapter(myadapter);
        contactslist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        contactslist.setItemsCanFocus(false);

        contactslist.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView ctv= (CheckedTextView)myadapter.getView(position,null,parent);
                Cursor c=myadapter.getCursor();
                ListView lv=(ListView)parent;
                c.moveToPosition(position);
                Log.v("easyplans",String.valueOf(ctv.isChecked()));
                if(inselectedids(id)) {
                    Log.v("","inside true");
                    view.setVisibility(View.VISIBLE);
                    lv.setItemChecked(position,false);
                    long id_delete=id;
                    for(int i=0;i<=p;i++)
                    {


                        if(selectedids[i]==id_delete)
                        {

                            Log.v("","insideloop");
                            for(int j=i;j<p;j++)
                            {
                                selectedids[j]=selectedids[j+1];
                                lookupkeys[j]=lookupkeys[j+1];
                            }
                            p--;

                        }
                    }
                    return;

                }
                else {
                    if(p==20)
                    {
                        Toast.makeText(getBaseContext(),"max 20",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    lv.setItemChecked(position,true);
                    c.moveToPosition(position);
                    selectedids[p]=id;
                    lookupkeys[p]=c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    p++;
                    return;
                }
            }

        });

    }
    public boolean inselectedids(long id)
    {
        for(int i=0;i<selectedids.length;i++)
        {
            if(selectedids[i]==id)
                return true;

        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }
    public void doneclicked(View v)
    {
        Intent resultintent=new Intent();
        resultintent.putExtra("resultids",selectedids);
        resultintent.putExtra("resultkeys",lookupkeys);
        resultintent.putExtra("noofmems",p);
        setResult(RESULT_OK,resultintent);
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        myadapter.getFilter().filter(newText);
        myadapter.notifyDataSetChanged();
        return true;
    }
}
