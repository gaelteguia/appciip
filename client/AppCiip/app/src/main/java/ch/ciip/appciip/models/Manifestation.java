package ch.ciip.appciip.models;

import android.location.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class Manifestation implements Serializable {

    private String id;
    private String title;
    private String description;
    private String website;
    private String organizer;
    private String startTime;
    private String endTime;
    private String image;
    private String address;
    private String distance;
    private String weatherId;
    private String weatherSunrise;
    private String weatherDetails;
    private String weatherTemperature;
    private String weatherSunset;

    private Boolean inRadius = false;
    private Address resolvedAddress;


    public Manifestation() {
    }

    public Manifestation(Long id) {
        this.id = Long.toString(id);
    }

    public Manifestation(String id, String title) {
        this.id = id;
        this.title = title;

    }

    public Manifestation(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getDistance() {
        return distance;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWeatherDetails() {
        return weatherDetails;
    }

    public void setWeatherDetails(String weatherDetails) {
        this.weatherDetails = weatherDetails;
    }

    public String getWeatherTemperature() {
        return weatherTemperature;
    }

    public void setWeatherTemperature(String weatherTemperature) {
        this.weatherTemperature = weatherTemperature;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherSunrise() {
        return weatherSunrise;
    }

    public void setWeatherSunrise(String weatherSunrise) {
        this.weatherSunrise = weatherSunrise;
    }


    public String getWeatherSunset() {
        return weatherSunset;
    }

    public void setWeatherSunset(String weatherSunset) {
        this.weatherSunset = weatherSunset;
    }

    // Get medium sized manifestation cover from covers API
    public String getCoverUrl() {
        return Constants.API_BASE_URL + "rest/manifestations/images/" + image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "Manifestation [id=" + id + ", title=" + title + ", description=" + description
                + "]";
    }

    public Boolean getInRadius() {
        return inRadius;
    }

    public void setInRadius(Boolean inRadius) {
        this.inRadius = inRadius;
    }

    public Address getResolvedAddress() {
        return resolvedAddress;
    }

    public void setResolvedAddress(Address resolvedAddress) {
        this.resolvedAddress = resolvedAddress;
    }


    // Returns a Manifestation given the expected JSON
    public static Manifestation fromJson(JSONObject jsonObject) {
        Manifestation manifestation = new Manifestation();
        try {
            // Deserialize json into object fields
            // Check if a cover edition is available
            if (jsonObject.has("id")) {
                manifestation.id = jsonObject.getString("id");
            }
            manifestation.title = jsonObject.has("title") ? jsonObject.getString("title") : "";
            manifestation.description = jsonObject.has("description") ? jsonObject.getString("description") : "";
            manifestation.website = jsonObject.has("website") ? jsonObject.getString("website") : "";
            manifestation.organizer = jsonObject.has("organizer") ? jsonObject.getString("organizer") : "";
            manifestation.image = jsonObject.has("image") ? jsonObject.getString("image") : "";
            manifestation.startTime = jsonObject.has("startTime") ? jsonObject.getString("startTime") : "";
            manifestation.endTime = jsonObject.has("endTime") ? jsonObject.getString("endTime") : "";
            manifestation.distance = jsonObject.has("distance") ? jsonObject.getString("distance") : "";
            //String rue = address.has("street") ? jsonObject.getString("street") : "";

            manifestation.address = getAddress(jsonObject);
            manifestation.weatherDetails = getWeatherDetails(jsonObject);
            manifestation.weatherTemperature = getWeatherTemperature(jsonObject);
            manifestation.weatherId = getWeatherId(jsonObject);
            manifestation.weatherSunrise = getWeatherSunrise(jsonObject);
            manifestation.weatherSunset = getWeatherSunset(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return manifestation;
    }


    private static String getAddress(final JSONObject jsonObject) {
        try {
            final JSONObject address = jsonObject.getJSONObject("address");

            String rue = address.has("street") ? address.getString("street") : "";
            String noRue = address.has("number") ? address.getString("number") : "";
            String npa = address.has("npa") ? address.getString("npa") : "";
            String localite = address.has("city") ? address.getString("city") : "";


            return rue + " " + noRue + ", " + npa + " " + localite;
        } catch (JSONException e) {
            return "";
        }
    }

    private static String getWeatherDetails(final JSONObject jsonObject) {
        try {
            final JSONObject address = jsonObject.getJSONObject("address");
            final JSONObject weather = address.getJSONObject("weather");

            JSONObject content = new JSONObject(weather.has("content") ? weather.getString("content") : "");

            JSONObject details = content.getJSONArray("weather").getJSONObject(0);
            JSONObject main = content.getJSONObject("main");

            return details.getString("description").toUpperCase(Locale.US) +
                    "\n" + "Humidité: " + main.getString("humidity") + "%" +
                    "\n" + "Pression: " + main.getString("pressure") + " hPa";

        } catch (JSONException e) {
            return "";
        }
    }

    private static String getWeatherTemperature(final JSONObject jsonObject) {
        try {
            final JSONObject address = jsonObject.getJSONObject("address");
            final JSONObject weather = address.getJSONObject("weather");

            JSONObject content = new JSONObject(weather.has("content") ? weather.getString("content") : "");

            JSONObject details = content.getJSONArray("weather").getJSONObject(0);
            JSONObject main = content.getJSONObject("main");

            return String.format("%.2f", main.getDouble("temp")) + " ℃";

        } catch (JSONException e) {
            return "";
        }
    }

    private static String getWeatherId(final JSONObject jsonObject) {
        try {
            final JSONObject address = jsonObject.getJSONObject("address");


            final JSONObject weather = address.getJSONObject("weather");

            JSONObject content = new JSONObject(weather.has("content") ? weather.getString("content") : "");

            JSONObject details = content.getJSONArray("weather").getJSONObject(0);
            JSONObject main = content.getJSONObject("main");

            return details.getString("id");

        } catch (JSONException e) {
            return "0";
        }
    }

    private static String getWeatherSunrise(final JSONObject jsonObject) {
        try {
            final JSONObject address = jsonObject.getJSONObject("address");
            final JSONObject weather = address.getJSONObject("weather");

            JSONObject content = new JSONObject(weather.has("content") ? weather.getString("content") : "");

            JSONObject details = content.getJSONArray("weather").getJSONObject(0);
            JSONObject main = content.getJSONObject("main");

            return Long.toString(content.getJSONObject("sys").getLong("sunrise") * 1000);

        } catch (JSONException e) {
            return "0";
        }
    }

    private static String getWeatherSunset(final JSONObject jsonObject) {
        try {
            final JSONObject address = jsonObject.getJSONObject("address");
            final JSONObject weather = address.getJSONObject("weather");

            JSONObject content = new JSONObject(weather.has("content") ? weather.getString("content") : "");

            JSONObject details = content.getJSONArray("weather").getJSONObject(0);
            JSONObject main = content.getJSONObject("main");

            return Long.toString(content.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (JSONException e) {
            return "0";
        }
    }


    // Decodes array of manifestation json results into business model objects
    public static ArrayList<Manifestation> fromJson(JSONArray jsonArray) {
        ArrayList<Manifestation> manifestations = new ArrayList<Manifestation>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject manifestationJson = null;
            try {
                manifestationJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Manifestation manifestation = Manifestation.fromJson(manifestationJson);
            if (manifestation != null) {
                manifestations.add(manifestation);
            }
        }
        return manifestations;
    }
}