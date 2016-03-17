package com.example.android.popmovies7;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Peter Francisco Balanesi on 3/6/2016.
 */
public class ReviewAdapter extends CursorAdapter implements AdapterView.OnItemClickListener {

    public ReviewAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        TextView tv_author = (TextView) view.findViewById(R.id.tv_author);
        tv_author.setText("- " + cursor.getString(MovieDetailFragment.COL_REVIEW_AUTHOR));

        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText("\"" + cursor.getString(MovieDetailFragment.COL_REVIEW_CONTENT) + "\"");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){

    }
}
