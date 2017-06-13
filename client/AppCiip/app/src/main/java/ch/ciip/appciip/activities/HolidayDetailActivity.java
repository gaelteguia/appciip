package ch.ciip.appciip.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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

import ch.ciip.appciip.MainActivity;
import ch.ciip.appciip.R;
import ch.ciip.appciip.models.Holiday;
import ch.ciip.appciip.net.HolidayClient;

public class HolidayDetailActivity extends ActionBarActivity {
    private ImageView ivHolidayCover;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvCanton;
    private HolidayClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_detail);
        // Fetch views
        ivHolidayCover = (ImageView) findViewById(R.id.ivHolidayCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvCanton = (TextView) findViewById(R.id.tvCanton);
        // Use the holiday to populate the data into our views
        Holiday holiday = (Holiday) getIntent().getSerializableExtra(MainActivity.HOLIDAY_DETAIL_KEY);
        loadHoliday(holiday);
    }

    // Populate data for the holiday
    private void loadHoliday(Holiday holiday) {
        //change activity title
        this.setTitle(holiday.getTitle());
        // Populate data
        Picasso.with(this).load(Uri.parse(holiday.getCoverUrl())).error(R.drawable.ic_nocover).into(ivHolidayCover);
        tvTitle.setText(holiday.getTitle());
        tvDescription.setText(holiday.getDescription());
        tvStartTime.setText(holiday.getStartTime());
        tvEndTime.setText(holiday.getEndTime());
        tvCanton.setText(holiday.getCanton());

        // fetch extra holiday data from holidays API
        client = new HolidayClient();
        client.getExtraHolidayDetails(holiday.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("publishers")) {
                        // display comma separated list of publishers
                        final JSONArray publisher = response.getJSONArray("publishers");
                        final int numStartTimes = publisher.length();
                        final String[] publishers = new String[numStartTimes];
                        for (int i = 0; i < numStartTimes; ++i) {
                            publishers[i] = publisher.getString(i);
                        }
                        tvStartTime.setText(TextUtils.join(", ", publishers));
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
        getMenuInflater().inflate(R.menu.menu_holiday_detail, menu);
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
        ImageView ivImage = (ImageView) findViewById(R.id.ivHolidayCover);
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
}