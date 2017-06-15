package ch.ciip.appciip.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import ch.ciip.appciip.MainActivity;
import ch.ciip.appciip.R;
import ch.ciip.appciip.models.Manifestation;
import ch.ciip.appciip.net.ManifestationClient;

import static ch.ciip.appciip.MainActivity.ManifestationsSectionFragment.MANIFESTATION_DETAIL_KEY;

/**
 * Created by gagui on 17.05.2017.
 */

public class ManifestationDetailActivity extends ActionBarActivity {
    Typeface weatherFont;
    private ImageView ivManifestationCover;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvWebsite;
    private TextView tvOrganizer;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvAddress;
    private TextView tvDistance;
    private TextView tvdetailsField;
    private TextView tvCurrentTemperatureField;
    private TextView weatherIcon;

    private Button btSubscribe;

    private ManifestationClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifestation_detail);
        weatherFont = Typeface.createFromAsset(getAssets(), "weathericons-regular-webfont.ttf");

        // Fetch views
        ivManifestationCover = (ImageView) findViewById(R.id.ivManifestationCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvWebsite = (TextView) findViewById(R.id.tvWebsite);
        tvOrganizer = (TextView) findViewById(R.id.tvOrganizer);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvdetailsField = (TextView) findViewById(R.id.details_field);
        tvCurrentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        btSubscribe = (Button) findViewById(R.id.btSubscribe);

        weatherIcon.setTypeface(weatherFont);
        // Use the manifestation to populate the data into our views
        final Manifestation manifestation = (Manifestation) getIntent().getSerializableExtra(MainActivity.MANIFESTATION_DETAIL_KEY);
        btSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InscriptionActivity.class);
                intent.putExtra(MANIFESTATION_DETAIL_KEY, manifestation);
                startActivity(intent);
            }
        });
        loadManifestation(manifestation);
    }

    // Populate data for the manifestation
    private void loadManifestation(Manifestation manifestation) {
        //change activity title
        this.setTitle(manifestation.getTitle());
        // Populate data
        Picasso.with(this).load(Uri.parse(manifestation.getCoverUrl())).error(R.drawable.ic_nocover).into(ivManifestationCover);
        tvTitle.setText(manifestation.getTitle());
        tvDescription.setText(manifestation.getDescription());
        tvWebsite.setText(manifestation.getWebsite());
        tvOrganizer.setText(manifestation.getOrganizer());
        tvStartTime.setText(manifestation.getStartTime());
        tvEndTime.setText(manifestation.getEndTime());
        tvAddress.setText(manifestation.getAddress());
        tvDistance.setText(manifestation.getDistance().substring(0, 4) + " Km");
        tvdetailsField.setText(manifestation.getWeatherDetails());
        tvCurrentTemperatureField.setText(manifestation.getWeatherTemperature());

        try {
            setWeatherIcon(Integer.parseInt(manifestation.getWeatherId()),
                    Long.parseLong(manifestation.getWeatherSunrise()),
                    Long.parseLong(manifestation.getWeatherSunset()));
        } catch (NumberFormatException ex) { // handle your exception
            setWeatherIcon(0, 0, 0);
        }
        // fetch extra manifestation data from manifestations API
        client = new ManifestationClient();
        client.getExtraManifestationDetails(manifestation.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("startTime")) {
                        // display comma separated list of startTime
                        final JSONArray publisher = response.getJSONArray("startTime");
                        final int numStartTimes = publisher.length();
                        final String[] startTime = new String[numStartTimes];
                        for (int i = 0; i < numStartTimes; ++i) {
                            startTime[i] = publisher.getString(i);
                        }
                        tvStartTime.setText(TextUtils.join(", ", startTime));
                    }
                    if (response.has("number_of_pages")) {
                        tvEndTime.setText(Integer.toString(response.getInt("number_of_pages")) + " pages");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manifestation_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            setShareIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent() {
        ImageView ivImage = (ImageView) findViewById(R.id.ivManifestationCover);
        final TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        // Construct a ShareIntent with link to image
        Intent shareIntent = new Intent();
        // Construct a ShareIntent with link to image
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, (String) tvTitle.getText());
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        // Launch share menu
        startActivity(Intent.createChooser(shareIntent, "Share Image"));

    }

    // Returns the URI path to the Bitmap displayed in cover imageview
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }


}