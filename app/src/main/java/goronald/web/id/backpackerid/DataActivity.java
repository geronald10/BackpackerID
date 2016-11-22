package goronald.web.id.backpackerid;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.MesosferData;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DataActivity extends AppCompatActivity {

    private ListView listview;
    private SimpleAdapter adapter;
    private ProgressDialog loading;
    private AlertDialog dialog;
    private final List<Map<String, String>> mapDataList = new ArrayList<>();
    private static final int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
    private static final String[] from = new String[] { "id", "data" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Data Kota");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new SimpleAdapter(this, mapDataList, android.R.layout.simple_list_item_2, from, to);
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        updateAndShowDataList();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
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
                mapDataList.clear();
                for (MesosferData data : list) {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", "ID : " + data.getObjectId());
                    try {
                        map.put("data", data.toJSON().toString(4));
                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }
                    mapDataList.add(map);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
