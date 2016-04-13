package au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.ActivityJumper;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Controller.PartyActivity;
import au.com.airmaxstudios.maxyendall.assignmenttwo.Model.Movie;
import au.com.airmaxstudios.maxyendall.assignmenttwo.R;

import static android.graphics.BitmapFactory.decodeStream;
import static android.os.Process.setThreadPriority;
/*
Student Name:       Max Yendall
Student Number:     s3436993
Course:             Mobile Application Development
Version:            2.0
Project:            Movie Social Club
 */

/**
 * ArrayAdapter for displaying the movie list
 */
public class MovieListAdapter extends ArrayAdapter < Movie > {

    // Declare class data
    private ArrayList < Movie > mData;
    private Context context;
    String PosterURI;
    ImageView mPoster;
    MovieSQLAdapter movieSQLAdapter;

    public MovieListAdapter(Context context, ArrayList < Movie > data) {
        // Set globals
        super(context, 0, data);
        this.mData = data;
        this.context = context;
        this.movieSQLAdapter = new MovieSQLAdapter(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Declare immutable movie object for assignment
        final Movie mObj = getItem(position);

        // Inflate layout and assign current view
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_object, parent, false);
        }

        // Fetch all necessary fields for setting
        RatingBar movieRating = (RatingBar) convertView.findViewById(R.id.ratingBar);
        mPoster = (ImageView) convertView.findViewById((R.id.moviePoster));
        TextView titleLabel = (TextView) convertView.findViewById(R.id.labelTitle);
        TextView yearLabel = (TextView) convertView.findViewById(R.id.labelYear);
        TextView shortPlotLabel = (TextView) convertView.findViewById(R.id.labelShortPlot);
        Button nextButton = (Button) convertView.findViewById(R.id.viewButton);

        // Set all fields
        PosterURI = (mObj.getmPoster());
        titleLabel.setText(mObj.getmTitle());
        yearLabel.setText(mObj.getmYear());
        shortPlotLabel.setText(mObj.getmShortPlot());
        new DownloadImage(mPoster).execute(PosterURI);
        // Set change listener for rating bar to make it dynamic
        movieRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                mObj.setmRating(rating);
                ratingBar.setRating(rating);
                movieSQLAdapter.updateMovieRating(mObj.getmId(),rating);
            }
        });
        movieRating.setRating(mObj.getmRating());

        // Set Intent and Listener
        Intent activityIntent = new Intent(context, PartyActivity.class);
        activityIntent.putExtra("ID", mObj.getmId());
        nextButton.setOnClickListener(new ActivityJumper(activityIntent, context));

        return convertView;
    }

    /**
     * Download class for downloading an image from OMDB
     */
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        ImageView image_view;
        private DownloadImage(ImageView image_view_set)
        {
            image_view = image_view_set;
        }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null) {
                // Set the bitmap into ImageView
                image_view.setImageBitmap(result);
            }

        }
    }
}