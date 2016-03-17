package com.example.android.popmovies7;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmovies7.data.MovieContract.MovieEntry;
import com.example.android.popmovies7.data.MovieContract.ReviewEntry;
import com.example.android.popmovies7.data.MovieContract.TrailerEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by Peter Francisco Balanesi on 3/4/2016.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";

    private static final int MOVIE_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    private static final int REVIEW_LOADER = 3;

    private ShareActionProvider mShareActionProvider;
    private String mTrailerString;
    private long movieId;
    private Uri mUri;
    private FloatingActionButton fab;
    private RatingBar mRatingBar;
    private TextView tv_user_rating;
    private TextView tv_tmdb_rating;
    private TextView tv_vote_count;
    private TextView tv_over_view;
    private TextView tv_release_date;
    private ImageView iv_movie_poster;
    private NestedListView trailerListView;
    private TrailerAdapter mTrailerAdapter;
    private NestedListView reviewListView;
    private ReviewAdapter mReviewAdapter;

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

    private static final String[] TRAILER_COLUMNS ={
            TrailerEntry.TABLE_NAME + "." + TrailerEntry._ID,
            TrailerEntry.COLUMN_TMDB_MOVIE_ID,
            TrailerEntry.COLUMN_NAME,
            TrailerEntry.COLUMN_SIZE,
            TrailerEntry.COLUMN_SOURCE,
            TrailerEntry.COLUMN_TYPE };

            static final int COL_TRAILER_ID = 0;
            static final int COL_TRAILER_TMDB_ID = 1;
            static final int COL_TRAILER_NAME = 2;
            static final int COL_TRAILER_SIZE = 3;
            static final int COL_TRAILER_SOURCE = 4;
            static final int COL_TRAILER_TYPE = 5;

    private static final String[] REVIEW_COLUMNS ={
            ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID,
            ReviewEntry.COLUMN_TMDB_MOVIE_ID,
            ReviewEntry.COLUMN_TMDB_REVIEW_ID,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT,
            ReviewEntry.COLUMN_URL };

            static final int COL_REVIEW_ID = 0;
            static final int COL_REVIEW_TMDB_ID = 1;
            static final int COL_REVIEW_REVIEW_ID = 2;
            static final int COL_REVIEW_AUTHOR = 3;
            static final int COL_REVIEW_CONTENT = 4;
            static final int COL_REVIEW_URL = 5;

    public MovieDetailFragment(){

    }

    @Override
    public void onCreate(Bundle savedStateInstance){
        super.onCreate(savedStateInstance);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.movie_detail_fragment, menu);
        MenuItem menu_item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu_item);

        if (mTrailerString != null){
            mShareActionProvider.setShareIntent(getTrailerShareString());
        }
    }

    private Intent getTrailerShareString(){
        Intent share_intent = new Intent(Intent.ACTION_SEND);
        share_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_TEXT,mTrailerString);

        return share_intent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);


        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        Bundle arguments = getArguments();
        if (arguments != null){
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
            movieId = MovieEntry.getTmdbIdFromUri(mUri);
        }
        final View rootView = inflater.inflate(R.layout.movie_detail_fragment, container, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        addOnClickListenerOnFAB();
        mRatingBar = (RatingBar) rootView.findViewById(R.id.star_rating_bar);
        addListenerOnRatingBar();
        tv_user_rating = (TextView) rootView.findViewById(R.id.tv_user_rating);
        tv_tmdb_rating = (TextView) rootView.findViewById(R.id.tv_tmdb_rating);
        tv_vote_count = (TextView) rootView.findViewById(R.id.tv_vote_count);
        iv_movie_poster = (ImageView) rootView.findViewById(R.id.iv_movie_poster);
        tv_over_view = (TextView) rootView.findViewById(R.id.tv_overview);
        tv_release_date = (TextView) rootView.findViewById(R.id.tv_release_date);
        trailerListView = (NestedListView) rootView.findViewById(R.id.listview_trailers);
        mTrailerAdapter = new TrailerAdapter(getActivity(),null,0);
        trailerListView.setAdapter(mTrailerAdapter);
        addOnItemClickListenerOnTrailerListView();
        reviewListView = (NestedListView) rootView.findViewById(R.id.listview_reviews);
        mReviewAdapter = new ReviewAdapter(getActivity(),null,0);
        reviewListView.setAdapter(mReviewAdapter);

        return rootView;
    }


    private void addListenerOnRatingBar(){
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser){
                Uri uri = MovieEntry.buildMovieUri(movieId);
                ContentValues values = new ContentValues();
                values.put(MovieEntry.COLUMN_USER_RATING, rating);
                String selection = MovieEntry.COLUMN_TMDB_ID + " = " + movieId;
                int i = getActivity().getContentResolver().update(uri,values,selection,null);
            }
        });
    }


    private void addOnClickListenerOnFAB(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = MovieEntry.buildMovieUri(movieId);
                String selection = MovieEntry.COLUMN_TMDB_ID + " = " + movieId;
                Cursor c = getActivity().getContentResolver().query(uri,new String[]{MovieEntry.COLUMN_IS_FAVORITE}, selection,null, null);
                if ( c.moveToFirst()){
                    if (c.getString(0).equals("0")){
                        ContentValues cv = new ContentValues();
                        cv.put(MovieEntry.COLUMN_IS_FAVORITE, true);
                        int i = getActivity().getContentResolver().update(uri,cv,selection,null);
                        if (i == 1){
                            Toast added = Toast.makeText(getContext(), R.string.movie_added_to_favorites, Toast.LENGTH_SHORT);
                            added.show();
                        } else {
                            Toast failed = Toast.makeText(getContext(), R.string.movie_failed_added_to_favorites, Toast.LENGTH_SHORT);
                            failed.show();
                        }

                    } else if (c.getString(0).equals("1")){
                        ContentValues cv = new ContentValues();
                        cv.put(MovieEntry.COLUMN_IS_FAVORITE, false);
                        int i = getActivity().getContentResolver().update(uri,cv,selection,null);
                        if (i == 1){
                            Toast added = Toast.makeText(getContext(), R.string.movie_removed_from_favorites, Toast.LENGTH_SHORT);
                            added.show();
                        } else {
                            Toast failed = Toast.makeText(getContext(), R.string.movie_failed_removing_from_favorites, Toast.LENGTH_SHORT);
                            failed.show();
                        }
                    }
                }
                c.close();
            }});
    }


    private void addOnItemClickListenerOnTrailerListView(){
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if ( cursor != null ) {
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + cursor.getString(COL_TRAILER_SOURCE)));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + cursor.getString(COL_TRAILER_SOURCE)));
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        updateTrailers();
        updateReviews();
    }


    private void updateTrailers(){

        if (Utility.isNetworkAvailable(getActivity())){
            FetchTrailerTask trailerTask = new FetchTrailerTask(getActivity());
            trailerTask.execute(Long.toString(movieId));
        } else {
            Toast toast = Toast.makeText(getActivity(), "Update Movie Library failed.\nNetwork Connection down.\nWill try again later!", Toast.LENGTH_LONG);
            toast.show();
        }

    }


    private void updateReviews(){
        if (Utility.isNetworkAvailable(getActivity())){
            FetchReviewTask reviewTask = new FetchReviewTask(getActivity());
            reviewTask.execute(Long.toString(movieId));
        } else {
            Toast toast = Toast.makeText(getActivity(), "Update Movie Library failed.\nNetwork Connection down.\nWill try again later!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        getLoaderManager().restartLoader(TRAILER_LOADER, null, this);
        getLoaderManager().restartLoader(REVIEW_LOADER, null, this);
    }


    void onMovieChanged(Uri uri){
        movieId = MovieEntry.getTmdbIdFromUri(uri);

        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        getLoaderManager().restartLoader(TRAILER_LOADER, null, this);
        getLoaderManager().restartLoader(REVIEW_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){

        //Intent intent = getActivity().getIntent();
        //if(intent == null || intent.getData() == null){
        //    return null;
        //}
        //if(movieId != 0){

            switch (i){
                case MOVIE_LOADER: {
                    String sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";
                    Uri moviesUri = MovieEntry.buildMovieUri(movieId);
                    return new CursorLoader(getActivity(),
                            moviesUri,
                            MOVIE_COLUMNS,
                            null,
                            null,
                            sortOrder);
                }
                case TRAILER_LOADER: {
                    String sortOrder = TrailerEntry.COLUMN_TYPE + " ASC";
                    Uri trailersUri = TrailerEntry.buildTrailerUri(movieId);
                    return new CursorLoader(getActivity(),
                            trailersUri,
                            TRAILER_COLUMNS,
                            null,
                            null,
                            sortOrder);
                }
                case REVIEW_LOADER:{
                    String sortOrder = ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID + " ASC";
                    Uri reviewsUri = ReviewEntry.buildReviewUri(movieId);
                    return new CursorLoader(getActivity(),
                            reviewsUri,
                            REVIEW_COLUMNS,
                            null,
                            null,
                            sortOrder);
                }
                default: return null;
            }
        //return null;
        }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){

        int loader = cursorLoader.getId();
        switch (loader){
            case MOVIE_LOADER:{
               if(cursor != null && cursor.moveToFirst()){

                   ((AppCompatActivity)getActivity()).setTitle(cursor.getString(MovieDetailFragment.COL_TITLE));

                    mRatingBar.setRating(cursor.getFloat(MovieDetailFragment.COL_USER_RATING));
                    tv_user_rating.setText("Rating: " + cursor.getString(MovieDetailFragment.COL_USER_RATING) + "/10");
                    tv_tmdb_rating.setText("TMdB Rating: " + cursor.getString(MovieDetailFragment.COL_VOTE_AVERAGE) + "/10");
                    tv_vote_count.setText("TMdB Vote Count: " + cursor.getString(MovieDetailFragment.COL_VOTE_COUNT));

                    if( cursor.getString(MovieDetailFragment.COL_IS_FAVORITE).equals("0")){

                        fab.setImageResource(R.drawable.ic_thumbs_up_white);
                        int[][] states = new int[][]{
                                new int[] {android.R.attr.state_enabled},
                                new int[] {android.R.attr.state_pressed},
                                new int[]{}};
                        int[] colors = new int[]{
                                getContext().getResources().getColor(R.color.greenFab),
                                getContext().getResources().getColor(R.color.greenFap_pressed),
                                getContext().getResources().getColor(R.color.greenFab)};

                        ColorStateList csl = new ColorStateList(states, colors);
                        //fab.setBackgroundColor(Color.GREEN);
                        fab.setBackgroundTintList(csl);


                    } else {
                        fab.setImageResource(R.drawable.ic_thumbs_down_white);
                        int[][] states = new int[][]{
                                new int[] {android.R.attr.state_enabled},
                                new int[] {android.R.attr.state_pressed}};
                        int[] colors = new int[]{
                                getContext().getResources().getColor(R.color.redFab),
                                getContext().getResources().getColor(R.color.redFab_pressed)};

                        ColorStateList csl = new ColorStateList(states, colors);
                        fab.setBackgroundTintList(csl);
                    }


                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    Uri uri = Uri.parse("http://image.tmdb.org/t/p/w500" + cursor.getString(MovieDetailFragment.COL_POSTER_PATH));
                    Context context = iv_movie_poster.getContext();
                    Picasso.with(context).load(uri).resize(displayMetrics.widthPixels, displayMetrics.heightPixels).centerInside().into(iv_movie_poster);

                 tv_over_view.setText(cursor.getString(MovieDetailFragment.COL_OVERVIEW));

                   tv_release_date.setText("Release Date: " + cursor.getString(MovieDetailFragment.COL_RELEASE_DATE));



                break;}
            }
            case TRAILER_LOADER:{

                if (cursor != null && cursor.moveToFirst()){
                    mTrailerString = "http://www.youtube.com/watch?v=" + cursor.getString(COL_TRAILER_SOURCE);
                }

                if (mShareActionProvider != null){
                       mShareActionProvider.setShareIntent(getTrailerShareString());
                }


                mTrailerAdapter.swapCursor(cursor);
                break;
            }
            case REVIEW_LOADER:{
                mReviewAdapter.swapCursor(cursor);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader){
        int loader = cursorLoader.getId();
        switch (loader){
            case MOVIE_LOADER:{
                break;
            }
            case TRAILER_LOADER:{
                mTrailerAdapter.swapCursor(null);
                break;
            }
            case REVIEW_LOADER:{
                mReviewAdapter.swapCursor(null);
            }
        }

    }

}
