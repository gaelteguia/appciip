package ch.ciip.appciip;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import ch.ciip.appciip.activities.ManifestationDetailActivity;
import ch.ciip.appciip.models.GPSTracker;
import ch.ciip.appciip.models.Manifestation;
import ch.ciip.appciip.net.ManifestationClient;

import static ch.ciip.appciip.MainActivity.MANIFESTATION_DETAIL_KEY;

/**
 * Created by gagui on 27.05.2017.
 */
public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private ManifestationClient client;
    private Manifestation manifestation;
    private List<Manifestation> list = new ArrayList<Manifestation>();

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {

                manifestation = new Manifestation();
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void fetchManifestations(String query) {
        // Show progress bar before making network request
        ;

    }

    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        //List<Manifestation> list = new LinkedList<Manifestation>();

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                GPSTracker gpsTracker = GPSTracker.getInstance(getApplicationContext());

                if (gpsTracker.getIsGPSTrackingEnabled()) {


                    client = new ManifestationClient();

                    client.getManifestations(gpsTracker.latitude + "," + gpsTracker.longitude, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {


                            JSONArray docs = null;
                            if (response != null) {
                                // Get the docs json array
                                docs = response;
                                // Parse json array into array of model objects
                                final ArrayList<Manifestation> results = Manifestation.fromJson(docs);


                                Log.i("Result", results.get(0).getTitle());

                                final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                                builder.setContentTitle("Nouvelle manifestation à proximité")
                                        .setAutoCancel(true)

                                        .setContentText(results.get(0).getTitle() + " : " + results.get(0).getAddress())
                                        .setSmallIcon(R.drawable.ic_launcher);


                                Intent mainIntent = new Intent(getApplicationContext(), ManifestationDetailActivity.class);
                                mainIntent.putExtra(MANIFESTATION_DETAIL_KEY, results.get(0));
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                        NOTIFICATION_ID,
                                        mainIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(pendingIntent);
                                builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(getApplicationContext()));

                                final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.notify(NOTIFICATION_ID, builder.build());
                            }

                        }


                    });
                } else {

                    Toast.makeText(getApplicationContext(), "Please Enable GPS/WIFI to access.", Toast.LENGTH_SHORT).show();
                }


            }
        };
        mainHandler.post(myRunnable);


    }

    public void setList(List<Manifestation> list) {
        this.list = list;
    }
}
