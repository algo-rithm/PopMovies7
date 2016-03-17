package com.example.android.popmovies7;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * Created by Peter Francisco Balanesi on 3/3/2016.
 */
public class MovieAdapter extends CursorAdapter implements AdapterView.OnItemClickListener {

    public static class ViewHolder {
        public final ImageView iv_movie_poster;
        public final TextView tv_movie_title;
        public final FloatingActionButton fab_fav_movie;

        public ViewHolder(View view){
            iv_movie_poster = (ImageView) view.findViewById(R.id.image_movie_poster);
            tv_movie_title = (TextView) view.findViewById(R.id.list_item_movie_textview);
            fab_fav_movie = (FloatingActionButton) view.findViewById(R.id.fab);
        }
    }


    private class MovieHolder {

    }

    public MovieAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor){


        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Uri uri = Uri.parse("http://image.tmdb.org/t/p/w300" + cursor.getString(PopMovieListingsFragment.COL_POSTER_PATH));
        Picasso.with(context).load(uri).into(viewHolder.iv_movie_poster);

        viewHolder.tv_movie_title.setText(cursor.getString(PopMovieListingsFragment.COL_TITLE));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){

    }
}
