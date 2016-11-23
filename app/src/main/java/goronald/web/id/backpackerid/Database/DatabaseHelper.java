package goronald.web.id.backpackerid.Database;

import android.app.ProgressDialog;
import android.util.Log;

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

import goronald.web.id.backpackerid.Object.City;
import goronald.web.id.backpackerid.Object.VisitObject;

/**
 * Created by Zachary on 11/23/2016.
 */

public class DatabaseHelper {

    private List<City> mapCity = new ArrayList<City>();
    private List<VisitObject> mapObject = new ArrayList<VisitObject>();
    private final List<Map<String, String>> mapDataList = new ArrayList<>();
    private static final int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
    private static final String[] from = new String[] { "id", "data" };

    public DatabaseHelper (){

    }

    public List<City> getDataKota(MesosferQuery<MesosferData> query, final ProgressDialog loading){

//        MesosferQuery<MesosferData> query = MesosferData.getQuery("Kota");

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
//                    // setup alert dialog builder
//                    AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
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
                mapDataList.clear();
                for (MesosferData data : list) {
                    City myCity = new City();
                    VisitObject myObject = new VisitObject();

                    Map<String, String> map = new HashMap<>();
                    map.put("id", "ID : " + data.getObjectId());
                    try {
                        Log.d("Nama Kota",data.getDataObject("namaKota").toString());
                        map.put("data", data.toJSON().toString(1));
                        JSONObject dataJson = new JSONObject(data.toJSON().toString());
//                        String namaKota = dataJson.getString("namaKota");

                        myCity.setCityName(dataJson.getString("namaKota"));
                        myCity.setCityBudget(dataJson.getString("minimumBudget"));
                        myCity.setCityDescription(dataJson.getString("kotaDescription"));
                        myCity.setCityLat(dataJson.getString("kotaLat"));
                        myCity.setCityLong(dataJson.getString("kotaLng"));
                        myCity.setCityPhoto(dataJson.getString("kotaFoto"));

//                        Log.d("Nama Kota",namaKota);

                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }
                    mapDataList.add(map);
                    mapCity.add(myCity);

                }
            }
        });

        return mapCity;
    }

    public List<VisitObject> getDataVisitObject(MesosferQuery<MesosferData> query, final String key){

//        MesosferQuery<MesosferData> query = MesosferData.getQuery("Kota");

        // showing a progress dialog loading
//        loading.setMessage("Querying kota...");
//        loading.show();

        query.findAsync(new FindCallback<MesosferData>() {
            @Override
            public void done(List<MesosferData> list, MesosferException e) {
                // hide progress dialog loading
//                loading.dismiss();

                // check if there is an exception happen
                if (e != null) {
//                    // setup alert dialog builder
//                    AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
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
                mapDataList.clear();
                for (MesosferData data : list) {
                    VisitObject myObject = new VisitObject();

                    Map<String, String> map = new HashMap<>();
                    map.put("id", "ID : " + data.getObjectId());
                    try {
//                        Log.d("Nama Kota",data.getDataObject("namaKota").toString());
                        map.put("data", data.toJSON().toString(1));
                        JSONObject dataJson = new JSONObject(data.toJSON().toString());
//                        String namaKota = dataJson.getString("namaKota");

                        // do something else
                        myObject.setObjName(dataJson.getString(""));
                        myObject.setObjPrice(dataJson.getString(""));
                        myObject.setObjPhoto(dataJson.getString(""));
                        myObject.setObjLat(dataJson.getString(""));
                        myObject.setObjLong(dataJson.getString(""));

//                        Log.d("Nama Kota",namaKota);

                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }
                    mapDataList.add(map);
                    mapObject.add(myObject);
                }
            }
        });

        return mapObject;
    }


}
