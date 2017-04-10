package com.example.christopher_santana.cs457bookstore;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.Format;
import java.util.ArrayList;

/**
 * Created by Christopher on 4/6/2017.
 */

public class SearchlistAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Book> books;
    private LayoutInflater myInflater;

    public SearchlistAdapter(Context cntxt, ArrayList<Book> bks){
        super(cntxt, R.layout.search_list_item);
        context = cntxt;
        books = bks;
        myInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getPosition(Object item) {
        return books.indexOf(item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder myHolder;
        int type = getItemViewType(position);
        if (convertView == null){
            myHolder = new ViewHolder();
            switch (type){
                case 1:
                    convertView = myInflater.inflate(R.layout.search_list_item, parent, false);
                    myHolder.titleView = (TextView) convertView.findViewById(R.id.search_list_item_title);
                    myHolder.priceView = (TextView) convertView.findViewById(R.id.search_list_item_price);
                    myHolder.checkBox = (CheckBox) convertView.findViewById(R.id.search_list_item_check);
                    myHolder.itemView = (RelativeLayout) convertView.findViewById(R.id.search_list_item_area);
                    break;
            }
            convertView.setTag(myHolder);
        }
        else{
            myHolder = (ViewHolder) convertView.getTag();
        }

        myHolder.titleView.setText(books.get(position).getTitle());
        myHolder.priceView.setText("$" + String.format("%.2f",books.get(position).getPrice()));
        myHolder.position = position;
        return convertView;
    }

    private class ViewHolder{
        RelativeLayout itemView;
        TextView titleView;
        TextView priceView;
        CheckBox checkBox;
        int position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void update(ArrayList<Book> bks){
        books = bks;
        notifyDataSetChanged();
    }
}
