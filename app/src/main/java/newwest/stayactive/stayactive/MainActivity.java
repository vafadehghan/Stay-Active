package newwest.stayactive.stayactive;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ArrayList<String> commCenterNames = new ArrayList<>();
    ArrayList<Double> commCenterX = new ArrayList<>();
    ArrayList<Double> commCenterY = new ArrayList<>();
    JsonReader jsReader;
    ListView mainListView;

    private class getNames extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            mainListView = findViewById(R.id.mainListView);
            try {
                InputStream is = getResources().openRawResource(R.raw.community);
                InputStreamReader responseBodyReader = new InputStreamReader(is, "UTF-8");
                jsReader = new JsonReader(responseBodyReader);
                jsReader.beginArray();
                while (jsReader.hasNext()) {
                    jsReader.beginObject();
                    while (jsReader.hasNext()) {
                        String key = jsReader.nextName();
                        switch (key) {
                            case "Name":
                                commCenterNames.add(jsReader.nextString());
                                break;
                            case "X":
                                commCenterX.add(jsReader.nextDouble());
                                break;
                            case "Y":
                                commCenterY.add(jsReader.nextDouble());
                                break;
                            default:
                                jsReader.skipValue();
                                break;
                        }
                    }
                    jsReader.endObject();
                }
                jsReader.endArray();
                jsReader.close();

            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "No File found", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    for (int i = 0; i < commCenterX.size(); i++) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(commCenterY.get(i), commCenterX.get(i)))
                                .title(commCenterNames.get(i)));
                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(49.18588392554472, -122.9436168718548)));
                    googleMap.setMinZoomPreference(10.0f);
                    googleMap.setMaxZoomPreference(13.0f);

                    Toast.makeText(MainActivity.this, "Map Ready", Toast.LENGTH_SHORT).show();
                }
            });

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplication(), R.layout.support_simple_spinner_dropdown_item, commCenterNames);
            mainListView.setAdapter(arrayAdapter);
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra("Name", adapterView.getItemAtPosition(i).toString());
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new getNames().execute();


    }
}
