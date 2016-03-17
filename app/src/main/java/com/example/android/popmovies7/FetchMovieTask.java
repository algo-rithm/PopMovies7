package com.example.android.popmovies7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popmovies7.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Peter Francisco Balanesi on 3/2/2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params){

        for(int i = 1; i < 10 ; i++){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            String numPages = ""+i;

            try{
                final String TMDB_BASE_URL = "http://api.themoviedb.org/3/" + params[0];
                final String KEY = "api_key";
                final String PAGES_PARAM = "page";

                Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendQueryParameter(PAGES_PARAM, numPages)
                        .appendQueryParameter(KEY, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                getMoviesFromJson(moviesJsonStr, params[1]);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

        }

        return null;
    }

    private void getMoviesFromJson(String moviesJSON, String movie_list) throws JSONException{

        final String TMDB_LIST = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "original_title";
        final String TMDB_BACKDROP_IMAGE = "backdrop_path";
        final String TMDB_SYNOPSIS = "overview";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_VOTE_COUNT = "vote_count";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_POPULARITY = "popularity";

        try{

            JSONObject popMovieJson = new JSONObject(moviesJSON);
            JSONArray popMovieArray = popMovieJson.getJSONArray(TMDB_LIST);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(popMovieArray.length());

            for (int i = 0; i < popMovieArray.length(); i++){
                String poster_path;
                String overview;
                String release_date;
                long id;
                String title;
                String backdrop_path;
                double popularity;
                long vote_count;
                double vote_average;

                JSONObject movieResultsJSON = popMovieArray.getJSONObject(i);
                id = movieResultsJSON.getLong(TMDB_ID);
                poster_path = movieResultsJSON.getString(TMDB_POSTER);
                overview = movieResultsJSON.getString(TMDB_SYNOPSIS);
                release_date = movieResultsJSON.getString(TMDB_RELEASE_DATE);

                title = movieResultsJSON.getString(TMDB_TITLE);
                backdrop_path = movieResultsJSON.getString(TMDB_BACKDROP_IMAGE);
                popularity = movieResultsJSON.getDouble(TMDB_POPULARITY);
                vote_count = movieResultsJSON.getLong(TMDB_VOTE_COUNT);
                vote_average = movieResultsJSON.getDouble(TMDB_VOTE_AVERAGE);
                double user_rating = 0.0;

                Uri uri = MovieContract.MovieEntry.buildMovieUri(id);
                Cursor c = mContext.getContentResolver().query(uri,null, MovieContract.MovieEntry.COLUMN_TMDB_ID + " = ?", new String[]{Long.toString(id)},null);
                if ( !c.moveToFirst() ){

                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, id);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdrop_path);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, vote_count);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, user_rating);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_LIST, movie_list);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, false);

                    cVVector.add(movieValues);
                }

                c.close();

            }

            int inserted = 0;

            if(cVVector.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

}


