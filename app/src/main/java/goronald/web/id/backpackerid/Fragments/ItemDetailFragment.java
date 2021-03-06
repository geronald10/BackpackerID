package goronald.web.id.backpackerid.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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

import goronald.web.id.backpackerid.Adapter.CustomVisitAdapter;
import goronald.web.id.backpackerid.ItemDetailActivity;
import goronald.web.id.backpackerid.ItemListActivity;
import goronald.web.id.backpackerid.Object.VisitObject;
import goronald.web.id.backpackerid.R;

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
    private CustomVisitAdapter objAdapter;
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

        RecyclerView recyclerViewObject = (RecyclerView) rootView.findViewById(R.id.rvObjects);

        assert recyclerViewObject != null;
//        setupRecyclerView
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem);
        }
        loading = new ProgressDialog(getActivity());
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        objAdapter = new CustomVisitAdapter(getContext(), mObject);
//        Log.d("nama kota visit",mItem);
        updateAndShowDataList(mItem);
//        Log.d()

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewObject.setLayoutManager(mLayoutManager);
        recyclerViewObject.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewObject.setItemAnimator(new DefaultItemAnimator());
        recyclerViewObject.setAdapter(objAdapter);

        return rootView;
    }

    private void updateAndShowDataList(final String key) {
//        Still Error Need the parameter
        MesosferQuery<MesosferData> query = MesosferData.getQuery("WorthVisit");

        // showing a progress dialog loading
        loading.setMessage("Querying Tempat Wisata...");
        loading.show();

        query.findAsync(new FindCallback<MesosferData>() {
            @Override



            public void done(List<MesosferData> list, MesosferException e) {
                // hide progress dialog loading
                loading.dismiss();

                // check if there is an exception happen
                if (e != null) {
                    return;
                }

                for (MesosferData data : list) {
                    VisitObject myObject = new VisitObject();

                    Map<String, String> map = new HashMap<>();
                    map.put("id", "ID : " + data.getObjectId());
                    try {
                        map.put("data", data.toJSON().toString(1));
                        JSONObject dataJson = new JSONObject(data.toJSON().toString());
//                        Log.d("Object",dataJson.getString("namaKota"));
                        myObject.setObjOrigin(dataJson.getString("namaKota"));
                        myObject.setObjName(dataJson.getString("worthName"));
                        myObject.setObjPrice(dataJson.getString("worthPrice"));
                        myObject.setObjPhoto(dataJson.getString("imageWorth"));
                        myObject.setObjLat(dataJson.getString("worthLat"));
                        myObject.setObjLong(dataJson.getString("worthLng"));

//                        Log.d("Url Object",myObject.getObjPhoto());

                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }
//                    mapDataList.add(map);
//                    Log.d("City Budget",myCity.getCityBudget());
                    if (myObject.getObjOrigin().equals(key)){
//                        Log.d("masuk", "iya");

                        mObject.add(myObject);
                    }
//                    Log.d("mObject Size", String.valueOf(mObject.size()));
                }
                objAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
