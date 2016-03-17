package com.example.android.popmovies7.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.android.popmovies7.data.MovieContract.MovieEntry;
import com.example.android.popmovies7.data.MovieContract.ReviewEntry;
import com.example.android.popmovies7.data.MovieContract.TrailerEntry;
/**
 * Created by Peter Francisco Balanesi on 3/2/2016.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private  MovieDbHelper mDbHelper;

    static final int MOVIE = 111;
    static final int MOVIE_WITH_TMDB_ID = 101;
    static final int TRAILER = 222;
    static final int TRAILER_WITH_TMDB_ID = 202;
    static final int REVIEW = 333;
    static final int REVIEW_WITH_TMDB_ID = 303;

    private static final SQLiteQueryBuilder sTrailersByTmdbIdQueryBuilder;
    private static final SQLiteQueryBuilder sReviewsByTmdbIdQueryBuilder;
    static{
        sTrailersByTmdbIdQueryBuilder = new SQLiteQueryBuilder();
        sReviewsByTmdbIdQueryBuilder = new SQLiteQueryBuilder();

        sTrailersByTmdbIdQueryBuilder.setTables(
                TrailerEntry.TABLE_NAME + " INNER JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + TrailerEntry.TABLE_NAME +
                        " . " + TrailerEntry.COLUMN_TMDB_MOVIE_ID +
                        " = " + MovieEntry.TABLE_NAME +
                        " . " + MovieEntry.COLUMN_TMDB_ID);

        sReviewsByTmdbIdQueryBuilder.setTables(
                ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + ReviewEntry.TABLE_NAME +
                        " . " + ReviewEntry.COLUMN_TMDB_MOVIE_ID +
                        " = " + MovieEntry.TABLE_NAME +
                        " . " + MovieEntry.COLUMN_TMDB_ID);
    }

    private static final String sMovieSelection = MovieEntry.TABLE_NAME +"."+ MovieEntry.COLUMN_TMDB_ID + " = ? ";
    private static final String sTrailersSelection = TrailerEntry.TABLE_NAME + "." + TrailerEntry.COLUMN_TMDB_MOVIE_ID + " = ? ";
    private static final String sReviewsSelection = ReviewEntry.TABLE_NAME + "." + ReviewEntry.COLUMN_TMDB_MOVIE_ID + " = ? ";


    private Cursor getMovieByTmdbId(Uri uri, String[] projection, String sortOrder) {
        long tmdb_id = MovieEntry.getTmdbIdFromUri(uri);
        String selection;
        String[] selectionArgs;

        selection = sMovieSelection;
        selectionArgs = new String[] {Long.toString(tmdb_id)};

        return  mDbHelper.getReadableDatabase().query(
                MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null,
                sortOrder);

    }

    private Cursor getTrailersByTmdbId(Uri uri, String[] projection, String sortOrder){
        long tmdb_id = TrailerEntry.getTmdbIdFromUri(uri);
        String selection = sTrailersSelection;
        String[] selectionArgs = new String[]{Long.toString(tmdb_id)};

        return sTrailersByTmdbIdQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null, null,
                sortOrder);
    }

    private Cursor getReviewsByTmdbId(Uri uri, String[] projection, String sortOrder){
        long tmdb_id = ReviewEntry.getTmdbIdFromUri(uri);
        String selection = sReviewsSelection;
        String[] selectionArgs = new String[] {Long.toString(tmdb_id)};

        return sReviewsByTmdbIdQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null, null,
                sortOrder);
    }

    @Override
    public boolean onCreate(){
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_TMDB_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_TMDB_ID:
                return ReviewEntry.CONTENT_TYPE;
            case TRAILER:
                return TrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_TMDB_ID:
                return TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            // "movie"
            case MOVIE: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            case MOVIE_WITH_TMDB_ID: {
                retCursor = getMovieByTmdbId(uri, projection, sortOrder);
                break;
            }
            case TRAILER: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            case TRAILER_WITH_TMDB_ID: {
                retCursor = getTrailersByTmdbId(uri, projection, sortOrder);
                break;
            }
            case REVIEW: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            case REVIEW_WITH_TMDB_ID:{
                retCursor = getReviewsByTmdbId(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return  retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIE: {
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unkonw uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";

        switch (match){
            case MOVIE:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_WITH_TMDB_ID:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }}

        if(rowsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIE:{
                db.beginTransaction();
                int returnCount = 0;
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;}
            case TRAILER:{
                db.beginTransaction();
                int returnCount = 0;
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;}
            case REVIEW:{
                db.beginTransaction();
                int returnCount = 0;
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;}
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_TMDB_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        uriMatcher.addURI(authority, MovieContract.PATH_TRAILER + "/*", TRAILER_WITH_TMDB_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW + "/*", REVIEW_WITH_TMDB_ID);

        return uriMatcher;
    }
}
