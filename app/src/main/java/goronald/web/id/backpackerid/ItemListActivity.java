package goronald.web.id.backpackerid;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.MesosferData;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import goronald.web.id.backpackerid.Database.DatabaseHelper;
import goronald.web.id.backpackerid.Fragments.ItemDetailFragment;
import goronald.web.id.backpackerid.Object.City;
import goronald.web.id.backpackerid.Object.VisitObject;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private DatabaseHelper myDatabase;
    private boolean mTwoPane;
    private AlertDialog dialog;
    private ArrayList<City> mCities;
    private ProgressDialog loading;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private TextView emptyText;
    private FrameLayout frameLayout;
    private View recyclerView;
    private double currLat;
    private double currLng;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private Location mLoc;

    private GoogleApiClient mGoogleClient;
    String minBudget;

    private boolean mRequestingLocationUpdate = false;

    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Tourism Spot");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                // PERMISSION_REQUEST_ACCESS_FINE_LOCATION can be any unique int
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

        if (mGoogleClient == null) {
            buildGoogleApiClient();
        }

        displayLocation();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        minBudget = getIntent().getStringExtra("budget");
        emptyText = (TextView) findViewById(R.id.tvEmpty);
        emptyText.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
//        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        myDatabase = new DatabaseHelper();
        mCities = new ArrayList<City>();

        mAdapter = new SimpleItemRecyclerViewAdapter(mCities);


        ((RecyclerView) recyclerView).setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleClient!= null){
            mGoogleClient.connect();
        }
    }

    @Override
    protected void onStop() {

        mGoogleClient.disconnect();
        super.onStop();
    }


    private void displayLocation() {

        mLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);

        if (mLoc!= null){
            currLat = mLoc.getLatitude();
            currLng = mLoc.getLongitude();
        }else {
            Log.d("Error","not getting any data");
        }
    }
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleClient, mLocationRequest, this);

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }


    private void updateAndShowDataList(final String minBudget) {
        final Location currentLoc = new Location("Now");
        final Location cityLoc = new Location("City");
        currentLoc.setLatitude(currLat);
        currentLoc.setLongitude(currLng);
        final Double distance;

        MesosferQuery<MesosferData> query = MesosferData.getQuery("Kota");

        // showing a progress dialog loading
        loading.setMessage("Querying kota...");
        loading.show();

        query.findAsync(new FindCallback<MesosferData>() {
            @Override
            public void done(List<MesosferData> list, MesosferException e) {
                // hide progress dialog loading
                loading.dismiss();

                // check if there is an exception happen
                if (e != null) {
                    // setup alert dialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);
                    builder.setNegativeButton(android.R.string.ok, null);
                    builder.setTitle("Error Happen");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    dialog = builder.show();
                    return;
                }

                for (MesosferData data : list) {
                    City myCity = new City();
                    VisitObject myObject = new VisitObject();

                    Map<String, String> map = new HashMap<>();
                    map.put("id", "ID : " + data.getObjectId());
                    try {
                        map.put("data", data.toJSON().toString(1));
                        JSONObject dataJson = new JSONObject(data.toJSON().toString());
                        String namaKota = dataJson.getString("namaKota");


                        myCity.setCityName(dataJson.getString("namaKota"));
                        myCity.setCityBudget(dataJson.getString("minimumBudget"));
                        myCity.setCityDescription(dataJson.getString("kotaDescription"));
                        myCity.setCityLat(dataJson.getString("kotaLat"));
                        myCity.setCityLong(dataJson.getString("kotaLng"));
                        myCity.setCityPhoto(dataJson.getString("kotaFoto"));

                        cityLoc.setLatitude(Double.parseDouble(myCity.getCityLat()));
                        cityLoc.setLongitude(Double.parseDouble(myCity.getCityLong()));

                        String budget = budgetCalculation(currentLoc,cityLoc,myCity.getCityBudget());
                        myCity.setCityBudget(budget);

                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }
                    if(Float.parseFloat(myCity.getCityBudget())< Float.parseFloat(minBudget)){
                        mCities.add(myCity);
                    }

                }
                if (mCities.size() == 0){

                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);

                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private String budgetCalculation(Location origin,Location destination, String budget){
        Double distance2 = distance(origin.getLatitude(),origin.getLongitude(),destination.getLatitude(),destination.getLongitude());
        distance2 = Math.floor(distance2);

        Double minBudget = Double.valueOf(budget)*distance2;

        NumberFormat nf = new DecimalFormat("#.####");

        String s1 = nf.format(minBudget);

        return s1;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
        updateAndShowDataList(minBudget);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        currLat = currentLatitude;
        currLng = currentLongitude;
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<City> mValues;

        public SimpleItemRecyclerViewAdapter(List<City> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
//            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentTextNamaKota.setText(mValues.get(position).getCityName());
            Picasso.with(getApplicationContext())
                    .load(mValues.get(position).getCityPhoto())
                    .resize(125,90)
                    .centerCrop()
                    .into(holder.mContentImageKota);
            holder.mContentTextBudget.setText("Budget: Rp. " + (mValues.get(position).getCityBudget()));
//            Log.d("Ini item",mValues.get(position).getCityName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mContentTextNamaKota.toString());
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mContentTextNamaKota.getText());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentTextNamaKota;
            public final ImageView mContentImageKota;
            public final TextView mContentTextBudget;
            public City mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentTextNamaKota = (TextView) view.findViewById(R.id.contentTextNamaKota);
                mContentImageKota = (ImageView) view.findViewById(R.id.contentImageKota);
                mContentTextBudget = (TextView) view.findViewById(R.id.contentTextBudget);
            }
        }
    }
}
