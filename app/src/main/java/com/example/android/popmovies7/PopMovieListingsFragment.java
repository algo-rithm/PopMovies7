package com.example.android.popmovies7;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popmovies7.data.MovieContract.MovieEntry;

/**
 * Created by Peter Francisco Balanesi on 3/2/2016.
 */
public class PopMovieListingsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String COLLECTION = "collection";
    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_TMDB_ID,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_VOTE_COUNT,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_USER_RATING,
            MovieEntry.COLUMN_IS_FAVORITE };

    static final int COL_MOVIE_ID = 0;
    static final int COL_TMDB_ID = 1;
    static final int COL_POSTER_PATH = 2;
    static final int COL_TITLE = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_OVERVIEW = 5;
    static final int COL_BACKDROP_PATH = 6;
    static final int COL_POPULARITY = 7;
    static final int COL_VOTE_COUNT = 8;
    static final int COL_VOTE_AVERAGE = 9;
    static final int COL_USER_RATING = 10;
    static final int COL_IS_FAVORITE = 11;

    private MovieAdapter mMovieAdapter;
    private final static String MOVIE_LIST = "movie_list";
    private String movie_list;

    public interface Callback {
        public void onItemSelected(Uri movieUri);
    }

    public PopMovieListingsFragment(){
    }

    public static PopMovieListingsFragment newInstance(String movie_list){
        PopMovieListingsFragment popMovieListingsFragment = new PopMovieListingsFragment();
        Bundle args = new Bundle();

        args.putString(MOVIE_LIST, movie_list);

        popMovieListingsFragment.setArguments(args);
        return popMovieListingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if( getArguments()!= null){
            movie_list = getArguments().getString(MOVIE_LIST);
        } else movie_list = Utility.getPreferredMovieList(getActivity());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mMovieAdapter = new MovieAdapter(getActivity(),null,0);

        View rootView = inflater.inflate(R.layout.pop_movie_listings_fragment_container, container, false);


        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_pop_movies);
        gridView.setAdapter(mMovieAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null){
                    ((Callback) getActivity()).onItemSelected(MovieEntry.buildMovieUri(cursor.getLong(COL_TMDB_ID)));
                }
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(MOVIE_LOADER, savedInstanceState, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();

        if(!movie_list.equals("favorite")){
            movie_list = Utility.getPreferredMovieList(getActivity());
        }

        updateMovies();
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    private void updateMovies(){

        if (Utility.isNetworkAvailable(getActivity())){
            FetchMovieTask movieTask = new FetchMovieTask(getActivity());
            switch (movie_list){
                case "now_playing": {movieTask.execute(getResources().getString(R.string.pref_now_playing_baseapi), movie_list);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Now Playing");
                    break;}
                case "popular":{ movieTask.execute(getResources().getString(R.string.pref_popular_baseapi), movie_list);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Popular");
                    break;}
                case "top_rated":{ movieTask.execute(getResources().getString(R.string.pref_top_rated_baseapi), movie_list);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Top Rated");
                    break;}
                case "upcoming": {movieTask.execute(getResources().getString(R.string.pref_upcoming_baseapi), movie_list);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Upcoming");
                    break;}
                case "favorite": {
                    break;}
                default: {
                    Toast error = Toast.makeText(getActivity(), "movie list failed", Toast.LENGTH_SHORT);
                    error.show();
                }
            }


        } else {
            Toast toast = Toast.makeText(getActivity(), "Update Movie Library failed.\nNetwork Connection down.\nWill try again later!", Toast.LENGTH_LONG);
            toast.show();
        }

        getLoaderManager().restartLoader(MOVIE_LOADER, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){

        switch(movie_list){
            case "now_playing":{
                String sortOrder = MovieEntry.COLUMN_RELEASE_DATE + " ASC";
                Uri moviesUri = MovieEntry.buildMovieUri();

                return new CursorLoader(getActivity(),
                        moviesUri,
                        MOVIE_COLUMNS,
                        MovieEntry.COLUMN_MOVIE_LIST + " = ?",
                        new String[]{"now_playing"},
                        sortOrder);}
            case "popular":{
                String sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";
                Uri moviesUri = MovieEntry.buildMovieUri();

                return new CursorLoader(getActivity(),
                        moviesUri,
                        MOVIE_COLUMNS,
                        MovieEntry.COLUMN_MOVIE_LIST + " = ?",
                        new String[]{"popular"},
                        sortOrder);}
            case "top_rated":{
                String sortOrder = MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
                Uri moviesUri = MovieEntry.buildMovieUri();

                return new CursorLoader(getActivity(),
                        moviesUri,
                        MOVIE_COLUMNS,
                        MovieEntry.COLUMN_MOVIE_LIST + " = ?",
                        new String[]{"top_rated"},
                        sortOrder);}
            case "upcoming":{
                String sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";
                Uri moviesUri = MovieEntry.buildMovieUri();

                return new CursorLoader(getActivity(),
                        moviesUri,
                        MOVIE_COLUMNS,
                        MovieEntry.COLUMN_MOVIE_LIST + " = ?",
                        new String[]{"upcoming"},
                        sortOrder);}
            case "favorite":{
                String sortOrder = MovieEntry.COLUMN_USER_RATING + " DESC";
                Uri moviesUri = MovieEntry.buildMovieUri();

                return new CursorLoader(getActivity(),
                        moviesUri,
                        MOVIE_COLUMNS,
                        MovieEntry.COLUMN_IS_FAVORITE + " = ?",
                        new String[]{"1"},sortOrder);
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        mMovieAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }
}
