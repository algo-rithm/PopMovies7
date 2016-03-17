package com.example.android.popmovies7;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Peter Francisco Balanesi on 3/4/2016.
 */
public class TrailerAdapter extends CursorAdapter implements AdapterView.OnItemClickListener {

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_trailer_name = (TextView) view.findViewById(R.id.list_item_trailer_textview);
        ImageView iv_you_tube_icon = (ImageView) view.findViewById(R.id.you_tube_icon);
        tv_trailer_name.setText(cursor.getString(MovieDetailFragment.COL_TRAILER_NAME));
        iv_you_tube_icon.setImageResource(R.drawable.ic_you_tube);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
