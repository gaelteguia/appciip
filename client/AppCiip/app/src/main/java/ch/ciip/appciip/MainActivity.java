package ch.ciip.appciip;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import ch.ciip.appciip.activities.HolidayDetailActivity;
import ch.ciip.appciip.activities.ManifestationDetailActivity;
import ch.ciip.appciip.adapters.HolidayAdapter;
import ch.ciip.appciip.adapters.ManifestationAdapter;
import ch.ciip.appciip.models.GPSTracker;
import ch.ciip.appciip.models.Holiday;
import ch.ciip.appciip.models.Manifestation;
import ch.ciip.appciip.net.HolidayClient;
import ch.ciip.appciip.net.ManifestationClient;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    private static final String[] INITIAL_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final String[] LOCATION_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    public static final String MANIFESTATION_DETAIL_KEY = "manifestation";
    public static final String HOLIDAY_DETAIL_KEY = "holiday";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new ManifestationsSectionFragment();

                default:
                    return new HolidaysSectionFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Manifestations";

                case 1:
                    return "Vacances";


            }

            return "";
        }
    }

    public static class ManifestationsSectionFragment extends Fragment {

        public static final String MANIFESTATION_DETAIL_KEY = "manifestation";
        private ListView lvManifestations;
        private ManifestationAdapter manifestationAdapter;
        private ManifestationClient client;
        private ProgressBar progress;


        private List<Manifestation> ManifestationList;


        private List<String> listTitle = new ArrayList<String>();

        private Timer timer;
        private final int TIMER_DELAY = 10000;

        ProgressBar progressBar;

        Button btnShowLocation;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View rootView = inflater.inflate(R.layout.activity_manifestation_list, container, false);
            // setContentView(R.layout.activity_manifestation_list);


            lvManifestations = (ListView) rootView.findViewById(R.id.lvManifestations);
            ArrayList<Manifestation> aManifestations = new ArrayList<Manifestation>();
            // initialize the adapter
            manifestationAdapter = new ManifestationAdapter(getActivity(), aManifestations);
            // attach the adapter to the ListView
            lvManifestations.setAdapter(manifestationAdapter);
            progress = (ProgressBar) rootView.findViewById(R.id.progress);
            setupManifestationSelectedListener();
            setHasOptionsMenu(true);
            // Fetch the data remotely
            GPSTracker gpsTracker = GPSTracker.getInstance(getActivity().getApplicationContext());

            fetchManifestations(gpsTracker.latitude + "," + gpsTracker.longitude);

            return rootView;
        }

        public void setupManifestationSelectedListener() {
            lvManifestations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Launch the detail view passing manifestation as an extra
                    Intent intent = new Intent(getActivity(), ManifestationDetailActivity.class);
                    intent.putExtra(MANIFESTATION_DETAIL_KEY, manifestationAdapter.getItem(position));
                    startActivity(intent);
                }
            });
        }

        // Executes an API call to the OpenLibrary search endpoint, parses the results
        // Converts them into an array of manifestation objects and adds them to the adapter
        private void fetchManifestations(String query) {
            // Show progress bar before making network request
            progress.setVisibility(ProgressBar.VISIBLE);
            client = new ManifestationClient();
            client.getManifestations(query, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    // hide progress bar
                    progress.setVisibility(ProgressBar.GONE);
                    JSONArray docs = null;
                    if (response != null) {
                        // Get the docs json array
                        docs = response;
                        // Parse json array into array of model objects
                        final ArrayList<Manifestation> manifestations = Manifestation.fromJson(docs);


                        // Remove all manifestations from the adapter
                        manifestationAdapter.clear();
                        // Load model objects into the adapter
                        for (Manifestation manifestation : manifestations) {
                            manifestationAdapter.add(manifestation); // add manifestation through the adapter
                        }
                        manifestationAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progress.setVisibility(ProgressBar.GONE);
                }
            });
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_manifestation_list, menu);
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Fetch the data remotely
                    fetchManifestations(query);
                    // Reset SearchView
                    searchView.clearFocus();
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    searchItem.collapseActionView();
                    // Set activity title to search query
                    getActivity().setTitle(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_search) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }


        private boolean canAccessLocation() {
            return (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION));
        }

        private boolean hasPermission(String perm) {
            return (PackageManager.PERMISSION_GRANTED == getActivity().checkSelfPermission(perm));
        }


    }

    public static class HolidaysSectionFragment extends Fragment {
        public static final String HOLIDAY_DETAIL_KEY = "holiday";
        private ListView lvHolidays;
        private HolidayAdapter holidayAdapter;
        private HolidayClient client;
        private ProgressBar progress;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View rootView = inflater.inflate(R.layout.activity_holiday_list, container, false);
            //setContentView(R.layout.activity_holiday_list);
            lvHolidays = (ListView) rootView.findViewById(R.id.lvHolidays);
            ArrayList<Holiday> aHolidays = new ArrayList<Holiday>();
            // initialize the adapter
            holidayAdapter = new HolidayAdapter(getActivity(), aHolidays);
            // attach the adapter to the ListView
            lvHolidays.setAdapter(holidayAdapter);
            progress = (ProgressBar) rootView.findViewById(R.id.progress);
            setupHolidaySelectedListener();

            fetchHolidays("");
            setHasOptionsMenu(true);
            return rootView;
        }

        public void setupHolidaySelectedListener() {
            lvHolidays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Launch the detail view passing holiday as an extra
                    Intent intent = new Intent(getActivity(), HolidayDetailActivity.class);
                    intent.putExtra(HOLIDAY_DETAIL_KEY, holidayAdapter.getItem(position));
                    startActivity(intent);
                }
            });
        }

        // Executes an API call to the OpenLibrary search endpoint, parses the results
        // Converts them into an array of holiday objects and adds them to the adapter
        private void fetchHolidays(String query) {
            // Show progress bar before making network request
            progress.setVisibility(ProgressBar.VISIBLE);
            client = new HolidayClient();
            client.getHolidays(query, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    // hide progress bar
                    progress.setVisibility(ProgressBar.GONE);
                    JSONArray docs = null;
                    if (response != null) {
                        // Get the docs json array
                        docs = response;
                        // Parse json array into array of model objects
                        final ArrayList<Holiday> holidays = Holiday.fromJson(docs);
                        // Remove all holidays from the adapter
                        holidayAdapter.clear();
                        // Load model objects into the adapter
                        for (Holiday holiday : holidays) {
                            holidayAdapter.add(holiday); // add holiday through the adapter
                        }
                        holidayAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    progress.setVisibility(ProgressBar.GONE);
                }
            });
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.menu_holiday_list, menu);
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Fetch the data remotely
                    fetchHolidays(query);
                    // Reset SearchView
                    searchView.clearFocus();
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    searchItem.collapseActionView();
                    // Set activity title to search query
                    getActivity().setTitle(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();

            if (id == R.id.action_search) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

    }


    public void onSendNotificationsButtonClick(View view) {
        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST:
                if (canAccessLocation()) {

                } else {

                }
                break;
        }
    }

    private boolean canAccessLocation() {
        return (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }


}
