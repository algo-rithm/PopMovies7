package com.example.android.popmovies7.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Peter Francisco Balanesi on 3/2/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popmovies7";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //TABLE PATHS
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    public static final class MovieEntry implements BaseColumns {

        // content://com.example.android.popmovies3.app/movie
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        //set MIMEs
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        // Column names
        public static final String COLUMN_TMDB_ID = "tmdb_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_MOVIE_LIST = "movie_list";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static Uri buildMovieUri(){
            return CONTENT_URI;
        }

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriwithTMDBid(long tmdb_id){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(tmdb_id)).build();
        }

        public static long getTmdbIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getDetailFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }
    }

    public static final class TrailerEntry implements BaseColumns {

        // content://com.example.android.popmovies3.app/trailer
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        //set MIMEs
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ PATH_TRAILER;

        // Table name
        public static final String TABLE_NAME = "trailer";

        // Column names
        public static final String COLUMN_TMDB_MOVIE_ID = "tmdb_movie_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_TYPE = "type";

        public static Uri buildTrailerUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieTrailerUri(long tmdb_id){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(tmdb_id)).build();
        }

        public static long getTmdbIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    public static final class ReviewEntry implements BaseColumns {

        // content://com.example.android.popmovies3.app/review
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        //set MIMEs
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ PATH_REVIEW;

        // Table name
        public static final String TABLE_NAME = "review";

        // Column names
        public static final String COLUMN_TMDB_MOVIE_ID = "tmdb_movie_id";
        public static final String COLUMN_TMDB_REVIEW_ID = "tmdb_review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static Uri buildReviewUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieReviewUri(long tmdb_id){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(tmdb_id)).build();
        }

        public static long getTmdbIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }
}
