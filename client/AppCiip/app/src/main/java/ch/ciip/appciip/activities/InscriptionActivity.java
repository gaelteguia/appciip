package ch.ciip.appciip.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ch.ciip.appciip.MainActivity;
import ch.ciip.appciip.R;
import ch.ciip.appciip.models.Constants;
import ch.ciip.appciip.models.Manifestation;

/**
 * Created by gagui on 26.05.2017.
 */

public class InscriptionActivity extends AppCompatActivity {
    EditText name, forename, email, phone, message;
    Button send;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        name = (EditText) findViewById(R.id.name);
        forename = (EditText) findViewById(R.id.forename);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        message = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        final Manifestation manifestation = (Manifestation) getIntent().getSerializableExtra(MainActivity.MANIFESTATION_DETAIL_KEY);

        this.setTitle(manifestation.getTitle());
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                String s1 = name.getText().toString();
                //Log.e("s1", s1);
                String s2 = forename.getText().toString();
                String s3 = email.getText().toString();
                String s4 = phone.getText().toString();
                String s5 = message.getText().toString();
                String s6 = manifestation.getId();
                String s7 = manifestation.getTitle();

                new ExecuteTask().execute(s1, s2, s3, s4, s5, s6, s7);
            }
        });


    }


    class ExecuteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String res = PostData(params);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            finish();
        }

    }


    public String PostData(String[] values) {
        String s = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.API_BASE_URL +
                    "register");
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", values[0]));
            list.add(new BasicNameValuePair("forename", values[1]));
            list.add(new BasicNameValuePair("email", values[2]));
            list.add(new BasicNameValuePair("phone", values[3]));
            list.add(new BasicNameValuePair("message", values[4]));
            list.add(new BasicNameValuePair("idmanifestation", values[5]));
            list.add(new BasicNameValuePair("titlemanifestation", values[6]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            s = readResponse(httpResponse);
        } catch (Exception e) {
            System.out.println(e);
        }

        return "Merci pour votre inscription.";

    }

    public String readResponse(HttpResponse res) throws IOException {
        InputStream is = null;
        String return_text = "";
        try {
            is = res.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return_text = sb.toString();


        } catch (Exception e) {

        }
        return return_text;

    }
}
