package com.example.android.popmovies7;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

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
 * Created by Peter Francisco Balanesi on 3/6/2016.
 */
public class FetchReviewTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();
    private final Context mContext;

    public FetchReviewTask(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewsJsonStr;

        try {
            final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String TMDB_BASE_URL_WITH_ID = TMDB_BASE_URL + params[0] + "/reviews";
            final String KEY = "api_key";

            Uri builtUri = Uri.parse(TMDB_BASE_URL_WITH_ID).buildUpon()
                    .appendQueryParameter(KEY, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null){return null;}
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null){buffer.append(line + "\n");}
            if (buffer.length() == 0) {return null;}

            reviewsJsonStr = buffer.toString();

            getReviewsFromJson(reviewsJsonStr, params[0]);
        } catch (IOException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    private void getReviewsFromJson(String reviewsJSON, String tmdb_id) throws JSONException{

        final String TMDB_RESULTS = "results";

        final String TMDB_REVIEW_ID = "id";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_URL = "url";

        try{
            JSONObject reviewObject = new JSONObject(reviewsJSON);
            JSONArray resultsArrObject = reviewObject.getJSONArray(TMDB_RESULTS);
            Vector<ContentValues> cVVect = new Vector<>(resultsArrObject.length());

            for ( int i=0 ; i<resultsArrObject.length() ; i++){
                String tmdb_review_id;
                String author;
                String content;
                String url;

                JSONObject  review = resultsArrObject.getJSONObject(i);
                tmdb_review_id = review.getString(TMDB_REVIEW_ID);
                author = review.getString(TMDB_AUTHOR);
                content = review.getString(TMDB_CONTENT);
                url = review.getString(TMDB_URL);

                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MovieContract.ReviewEntry.COLUMN_TMDB_MOVIE_ID, tmdb_id);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_TMDB_REVIEW_ID, tmdb_review_id);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, url);

                cVVect.add(reviewValues);
            }

            int inserted = 0;
            if(cVVect.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVect.size()];
                cVVect.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

}
