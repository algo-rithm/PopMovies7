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
 * Created by Peter Francisco Balanesi on 3/4/2016.
 */
public class FetchTrailerTask extends AsyncTask<String , Void, Void>{
    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    private final Context mContext;

    public FetchTrailerTask(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailersJsonStr;

        try{
            final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String TMDB_BASE_URL_WITH_ID = TMDB_BASE_URL + params[0] + "/trailers";
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

            trailersJsonStr = buffer.toString();
            getTrailersFromJson(trailersJsonStr, params[0]);

        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
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

    private void getTrailersFromJson(String trailersJSON, String tmdb_id) throws JSONException{
        final String TMDB_YOU_TUBE = "youtube";
        final String TMDB_NAME = "name";
        final String TMDB_SIZE = "size";
        final String TMDB_SOURCE = "source";
        final String TMDB_TYPE = "type";

        try{
            JSONObject trailerObject = new JSONObject(trailersJSON);
            JSONArray youtubeObject = trailerObject.getJSONArray(TMDB_YOU_TUBE);

            Vector<ContentValues> cVVect = new Vector<>(youtubeObject.length());

            for (int i = 0; i < youtubeObject.length(); i++){
                String name;
                String size;
                String source;
                String type;

                JSONObject you_tube_trailer = youtubeObject.getJSONObject(i);
                name = you_tube_trailer.getString(TMDB_NAME);
                size = you_tube_trailer.getString(TMDB_SIZE);
                source = you_tube_trailer.getString(TMDB_SOURCE);
                type = you_tube_trailer.getString(TMDB_TYPE);

                ContentValues trailerValues = new ContentValues();

                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TMDB_MOVIE_ID, tmdb_id);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, name);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, size);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_SOURCE, source);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, type);

                cVVect.add(trailerValues);
            }

            int inserted = 0;
            if(cVVect.size() > 0){
                ContentValues[] cvArray = new ContentValues[cVVect.size()];
                cVVect.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

