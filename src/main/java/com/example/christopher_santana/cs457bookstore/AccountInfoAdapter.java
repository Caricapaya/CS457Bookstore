package com.example.christopher_santana.cs457bookstore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.util.ArrayList;

/**
 * Created by Christopher on 4/7/2017.
 */

public class AccountInfoAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Book> books;
    private ArrayList<Integer> bookCount;
    private LayoutInflater myInflater;

    public AccountInfoAdapter(Context cntxt, ArrayList<Book> bks, ArrayList<Integer> counts){
        super(cntxt, R.layout.account_info_list_item);
        context = cntxt;
        books = bks;
        bookCount = counts;
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

    public int getNumberBooks(int position){
        return bookCount.get(position);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder myHolder;
        int type = getItemViewType(position);
        if (convertView == null){
            myHolder = new ViewHolder();
            switch (type){
                case 1:
                    convertView = myInflater.inflate(R.layout.account_info_list_item, parent, false);
                    myHolder.animator = (ViewAnimator) convertView.findViewById(R.id.account_info_list_item_animator);

                    myHolder.titleView = (TextView) convertView.findViewById(R.id.account_info_list_item_layout1_title);
                    myHolder.priceView = (TextView) convertView.findViewById(R.id.account_info_list_item_layout1_price);
                    myHolder.countView = (TextView)  convertView.findViewById(R.id.account_info_list_item_layout1_number_variable);
                    myHolder.itemView = (RelativeLayout) convertView.findViewById(R.id.account_info_list_item_layout1);

                    myHolder.titleViewSecondary = (TextView) convertView.findViewById(R.id.account_info_list_item_layout2_title_variable);
                    myHolder.priceViewSecondary = (TextView) convertView.findViewById(R.id.account_info_list_item_layout2_price_variable);
                    myHolder.isbnViewSecondary = (TextView) convertView.findViewById(R.id.account_info_list_item_layout2_isbn_variable);
                    myHolder.itemViewSecondary = (RelativeLayout) convertView.findViewById(R.id.account_info_list_item_layout2);
                    break;
            }
            convertView.setTag(myHolder);
        }
        else{
            myHolder = (ViewHolder) convertView.getTag();
        }

        myHolder.titleView.setText(books.get(position).getTitle());
        myHolder.priceView.setText("$" + String.format("%.2f",books.get(position).getPrice()));
        myHolder.countView.setText(bookCount.get(position).toString());
        myHolder.position = position;

        myHolder.titleViewSecondary.setText(books.get(position).getTitle());
        myHolder.priceViewSecondary.setText("$" + String.format("%.2f",books.get(position).getPrice()));
        myHolder.isbnViewSecondary.setText(books.get(position).getISBN());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHolder.animator.showNext();
            }
        };

        myHolder.itemView.setOnClickListener(listener);
        myHolder.itemViewSecondary.setOnClickListener(listener);

        return convertView;
    }

    private class ViewHolder{
        //top
        ViewAnimator animator;

        //primary
        RelativeLayout itemView;
        TextView titleView;
        TextView priceView;
        TextView countView;

        //secondary
        RelativeLayout itemViewSecondary;
        TextView titleViewSecondary;
        TextView priceViewSecondary;
        TextView isbnViewSecondary;

        int position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void update(ArrayList<Book> bks, ArrayList<Integer> cnt){
        books = bks;
        bookCount = cnt;
        notifyDataSetChanged();
    }
}
