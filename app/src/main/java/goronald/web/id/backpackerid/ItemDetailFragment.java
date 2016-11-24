package goronald.web.id.backpackerid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.MesosferData;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import goronald.web.id.backpackerid.Object.VisitObject;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private String mItem;
    private ProgressDialog loading;
    private List<VisitObject> mObject;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mObject = new ArrayList<VisitObject>();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            mItem = getArguments().getString(ARG_ITEM_ID);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
            appBarLayout.setTitle(mItem);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem);
        }
        loading = new ProgressDialog(getActivity());
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        return rootView;
    }

    private void updateAndShowDataList() {
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
//                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemListActivity.this);
//                    builder.setNegativeButton(android.R.string.ok, null);
//                    builder.setTitle("Error Happen");
//                    builder.setMessage(
//                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
//                                    e.getCode(), e.getMessage())
//                    );
//                    dialog = builder.show();
                    return;
                }

                // clear all data list
//                mapDataList.clear();
                for (MesosferData data : list) {
//                    City myCity = new City();
                    VisitObject myObject = new VisitObject();

                    Map<String, String> map = new HashMap<>();
                    map.put("id", "ID : " + data.getObjectId());
                    try {
                        map.put("data", data.toJSON().toString(1));
                        JSONObject dataJson = new JSONObject(data.toJSON().toString());
                        String namaKota = dataJson.getString("namaKota");
                        Log.d("Nama Kota",dataJson.getString("namaKota"));

                        myObject.setObjName(dataJson.getString(""));
                        myObject.setObjPrice(dataJson.getString(""));
                        myObject.setObjPhoto(dataJson.getString(""));
                        myObject.setObjLat(dataJson.getString(""));
                        myObject.setObjLong(dataJson.getString(""));

//                        Log.d("Nama Kota",namaKota);

                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }
//                    mapDataList.add(map);
//                    Log.d("City Budget",myCity.getCityBudget());
                    mObject.add(myObject);
                    Log.d("mObject Size", String.valueOf(mObject.size()));
                }
//                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
