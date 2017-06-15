package ch.ciip.appciip.models;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class Holiday implements Serializable {

    private String id;

    private String description;
    private String startTime;

    private String endTime;
    private String image;
    private String canton;
    private String weatherId;
    private String weatherSunrise;
    private String weatherDetails;
    private String weatherTemperature;
    private String weatherSunset;

    private String title;

    private Boolean inRadius = false;



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


    public Holiday() {
    }

    public Holiday(Long id) {
        this.id = Long.toString(id);
    }

    public Holiday(String id, String title) {
        this.id = id;
        this.title = title;

    }

    public Holiday(String id, String title, String description) {
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

    // Get medium sized holiday cover from covers API
    public String getCoverUrl() {
        return Constants.API_BASE_URL + "rest/holidays/images/" + image;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }


    @Override
    public String toString() {
        return "Holiday [id=" + id + ", title=" + title + ", description=" + description
                + "]";
    }

    public Boolean getInRadius() {
        return inRadius;
    }

    public void setInRadius(Boolean inRadius) {
        this.inRadius = inRadius;
    }




    // Returns a Holiday given the expected JSON
    public static Holiday fromJson(JSONObject jsonObject) {
        Holiday holiday = new Holiday();
        try {
            // Deserialize json into object fields
            // Check if a cover edition is available
            if (jsonObject.has("id")) {
                holiday.id = jsonObject.getString("id");
            }
            holiday.title = jsonObject.has("title") ? jsonObject.getString("title") : "";
            holiday.description = jsonObject.has("description") ? jsonObject.getString("description") : "";
            holiday.image = jsonObject.has("image") ? jsonObject.getString("image") : "";
            holiday.startTime = jsonObject.has("startTime") ? jsonObject.getString("startTime") : "";
            holiday.endTime = jsonObject.has("endTime") ? jsonObject.getString("endTime") : "";
            //String rue = canton.has("street") ? jsonObject.getString("street") : "";

            holiday.canton = getCanton(jsonObject);
            holiday.weatherDetails = getWeatherDetails(jsonObject);
            holiday.weatherTemperature = getWeatherTemperature(jsonObject);
            holiday.weatherId = getWeatherId(jsonObject);
            holiday.weatherSunrise = getWeatherSunrise(jsonObject);
            holiday.weatherSunset = getWeatherSunset(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return holiday;
    }


    private static String getCanton(final JSONObject jsonObject) {
        try {
            final JSONObject canton = jsonObject.getJSONObject("canton");

            String rue = canton.has("street") ? canton.getString("street") : "";
            String noRue = canton.has("number") ? canton.getString("number") : "";
            String npa = canton.has("npa") ? canton.getString("npa") : "";
            String localite = canton.has("city") ? canton.getString("city") : "";


            return rue + " " + noRue + ", " + npa + " " + localite;
        } catch (JSONException e) {
            return "";
        }
    }

    private static String getWeatherDetails(final JSONObject jsonObject) {
        try {
            final JSONObject canton = jsonObject.getJSONObject("canton");
            final JSONObject weather = canton.getJSONObject("weather");

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
            final JSONObject canton = jsonObject.getJSONObject("canton");
            final JSONObject weather = canton.getJSONObject("weather");

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
            final JSONObject canton = jsonObject.getJSONObject("canton");


            final JSONObject weather = canton.getJSONObject("weather");

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
            final JSONObject canton = jsonObject.getJSONObject("canton");
            final JSONObject weather = canton.getJSONObject("weather");

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
            final JSONObject canton = jsonObject.getJSONObject("canton");
            final JSONObject weather = canton.getJSONObject("weather");

            JSONObject content = new JSONObject(weather.has("content") ? weather.getString("content") : "");

            JSONObject details = content.getJSONArray("weather").getJSONObject(0);
            JSONObject main = content.getJSONObject("main");

            return Long.toString(content.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (JSONException e) {
            return "0";
        }
    }


    // Decodes array of holiday json results into business model objects
    public static ArrayList<Holiday> fromJson(JSONArray jsonArray) {
        ArrayList<Holiday> holidays = new ArrayList<Holiday>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject holidayJson = null;
            try {
                holidayJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Holiday holiday = Holiday.fromJson(holidayJson);
            if (holiday != null) {
                holidays.add(holiday);
            }
        }
        return holidays;
    }
}