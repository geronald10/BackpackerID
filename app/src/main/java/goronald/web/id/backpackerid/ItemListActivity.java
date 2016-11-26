package goronald.web.id.backpackerid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.MesosferData;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import goronald.web.id.backpackerid.Database.DatabaseHelper;
import goronald.web.id.backpackerid.Object.City;
import goronald.web.id.backpackerid.Object.VisitObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    private Location mLoc;

    private GoogleApiClient googleApiClient;

    private boolean mRequestingLocationUpdate = false;

    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        if (googleApiClient == null) {
            buildGoogleApiClient();
        }

        displayLocation();

        String minBudget = getIntent().getStringExtra("budget");
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
//        mCities = myDatabase.getDataKota(query, loading);

        mAdapter = new SimpleItemRecyclerViewAdapter(mCities);


        updateAndShowDataList(minBudget);

//        Log.d("Size After Update", String.valueOf(mCities.size()));


        ((RecyclerView) recyclerView).setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {

        googleApiClient.disconnect();
        super.onStop();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLoc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (mLoc!= null){
            currLat = mLoc.getLatitude();
            currLng = mLoc.getLongitude();
            Log.d("Current Latitude",String.valueOf(currLat));
            Log.d("Current Longitude",String.valueOf(currLng));
        }else {
            Log.d("Error","not getting any data");
        }
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
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

                // clear all data list
//                mapDataList.clear();
                for (MesosferData data : list) {
                    City myCity = new City();
                    VisitObject myObject = new VisitObject();

                    Map<String, String> map = new HashMap<>();
                    map.put("id", "ID : " + data.getObjectId());
                    try {
                        map.put("data", data.toJSON().toString(1));
                        JSONObject dataJson = new JSONObject(data.toJSON().toString());
                        String namaKota = dataJson.getString("namaKota");
                        Log.d("Nama Kota",dataJson.getString("namaKota"));


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


//                        Log.d("Nama Kota",namaKota);

                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }
//                    mapDataList.add(map);
//                    Log.d("City Budget",myCity.getCityBudget());
//                    Log.d("budget City",myCity.getCityBudget());
//                    Log.d("vudget APp",minBudget);
                    if(Float.parseFloat(myCity.getCityBudget())< Float.parseFloat(minBudget)){
                        mCities.add(myCity);
                    }

//                    Log.d("mCities Size", String.valueOf(mCities.size()));
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(""));
    }

    private String budgetCalculation(Location origin,Location destination, String budget){
        Double distance = (double) origin.distanceTo(destination);
        Double minBudget = Double.valueOf(budget)*distance;

        return String.valueOf(minBudget);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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
