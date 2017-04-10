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
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christopher on 4/6/2017.
 */

public class SearchlistAdapterAnimated extends ArrayAdapter {
    private Context context;
    private ArrayList<Book> books;
    private SparseArray<Integer> bookCount;
    private SparseBooleanArray bookChecked;
    private LayoutInflater myInflater;

    public SearchlistAdapterAnimated(Context cntxt, ArrayList<Book> bks){
        super(cntxt, R.layout.search_list_item_animated);
        context = cntxt;
        books = bks;
        myInflater = LayoutInflater.from(context);

        bookChecked = new SparseBooleanArray();
        bookCount = new SparseArray<>();
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

    public boolean getBookChecked(int position){
        return bookChecked.get(position);
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
                    convertView = myInflater.inflate(R.layout.search_list_item_animated, parent, false);
                    myHolder.animator = (ViewAnimator) convertView.findViewById(R.id.search_list_item_animated_animator);

                    myHolder.titleView = (TextView) convertView.findViewById(R.id.search_list_item_animated_title);
                    myHolder.priceView = (TextView) convertView.findViewById(R.id.search_list_item_animated_price);
                    myHolder.checkBox = (CheckBox) convertView.findViewById(R.id.search_list_item_animated_check);
                    myHolder.itemView = (RelativeLayout) convertView.findViewById(R.id.search_list_item_animated_area);
                    myHolder.numberPicker = (EditText) convertView.findViewById(R.id.search_list_item_animated_number);

                    myHolder.titleViewSecondary = (TextView) convertView.findViewById(R.id.search_list_item_animated_second_variable_title);
                    myHolder.priceViewSecondary = (TextView) convertView.findViewById(R.id.search_list_item_animated_second_variable_price);
                    myHolder.isbnViewSecondary = (TextView) convertView.findViewById(R.id.search_list_item_animated_second_variable_isbn);
                    myHolder.itemViewSecondary = (RelativeLayout) convertView.findViewById(R.id.search_list_item_animated_second);
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

        myHolder.titleViewSecondary.setText(books.get(position).getTitle());
        myHolder.priceViewSecondary.setText("$" + String.format("%.2f",books.get(position).getPrice()));
        myHolder.isbnViewSecondary.setText(books.get(position).getISBN());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myHolder.animator.showNext();

                /*switch (view.getId()){
                    case R.id.search_list_item_animated_area:
                        myHolder.animator.showNext();
                        myHolder.titleViewSecondary.setText(books.get(position).getTitle());
                        myHolder.priceViewSecondary.setText("$" + String.format("%.2f",books.get(position).getPrice()));
                        myHolder.isbnViewSecondary.setText(books.get(position).getISBN());
                        break;
                    case R.id.search_list_item_animated_second:
                        myHolder.titleView.setText(books.get(position).getTitle());
                        myHolder.priceView.setText("$" + String.format("%.2f",books.get(position).getPrice()));
                        break;
                }*/
            }
        };

        myHolder.itemView.setOnClickListener(listener);
        myHolder.itemViewSecondary.setOnClickListener(listener);

        myHolder.numberPicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bookCount.put(position, Integer.parseInt(myHolder.numberPicker.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                return;
            }
        });

        myHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bookChecked.put(position, myHolder.checkBox.isChecked());
            }
        });

        return convertView;
    }

    private class ViewHolder{
        //top
        ViewAnimator animator;

        //primary
        RelativeLayout itemView;
        TextView titleView;
        TextView priceView;
        CheckBox checkBox;
        EditText numberPicker;

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

    public void update(ArrayList<Book> bks){
        books = bks;
        notifyDataSetChanged();
    }
}
