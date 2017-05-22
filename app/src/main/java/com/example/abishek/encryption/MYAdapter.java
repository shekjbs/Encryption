package com.example.abishek.encryption;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abishek on 21-05-2017.
 */

public class MYAdapter extends BaseAdapter {
    ArrayList<String> ls;
    Context c;
    LayoutInflater inflater=null;
    Activity activity;
    public MYAdapter(Context c,ArrayList<String> ls,Activity activity){
        this.ls=ls;
        this.c=c;
        this.activity=activity;
    }
    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi=view;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);

            TextView tv= (TextView) vi.findViewById(R.id.rowtext);
            tv.setText(ls.get(i));

        return vi;
    }
}
