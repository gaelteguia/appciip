package ch.ciip.appciip.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ch.ciip.appciip.models.Constants;

public class ManifestationClient {
    //private static final String API_BASE_URL = "http://83.68.193.66:8080/appciip/";
    private AsyncHttpClient client;

    public ManifestationClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return Constants.API_BASE_URL + relativeUrl;
    }

    // Method for accessing the search API
    public void getManifestations(final String query, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("rest/manifestations/");
            client.get(url + URLEncoder.encode(query, "utf-8"), handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Method for accessing manifestations API to get publisher and no. of pages in a manifestation.
    public void getExtraManifestationDetails(String openLibraryId, JsonHttpResponseHandler handler) {
        String url = getApiUrl("manifestations/");
        client.get(url + openLibraryId + ".json", handler);
    }
}