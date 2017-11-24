package newwest.stayactive.stayactive;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    Double x;
    Double y;
    JsonReader jsReader;
    TextView commCenterName;
    TextView commCenterDesc;
    TextView commCenterPhone;
    TextView commCenterWebsite;
    TextView commCenterHours;
    Intent extra;
    String hours;
    String desc;
    String phone;
    String site;
    ProgressBar progressBar;
    FloatingActionButton fab;

    private class getDetail extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                InputStream is = getResources().openRawResource(R.raw.community);
                InputStreamReader responseBodyReader = new InputStreamReader(is, "UTF-8");
                jsReader = new JsonReader(responseBodyReader);
                jsReader.beginArray();
                while (jsReader.hasNext()) {
                    jsReader.beginObject();
                    while (jsReader.hasNext()) {
                        String key = jsReader.nextName();
                        if (key.equals("Name")) {
                            String name = jsReader.nextString();
                            if (name.equals(extra.getStringExtra("Name"))) {
                                while (jsReader.hasNext()) {
                                    String key2 = jsReader.nextName();
                                    switch (key2) {
                                        case "Description":
                                            desc = jsReader.nextString();
                                            break;
                                        case "Hours":
                                            hours = jsReader.nextString();
                                            break;
                                        case "Phone":
                                            phone = jsReader.nextString();
                                            break;
                                        case "Website":
                                            site = jsReader.nextString();
                                            break;
                                        case "X":
                                            x = jsReader.nextDouble();
                                            break;
                                        case "Y":
                                            y = jsReader.nextDouble();
                                            break;
                                        default:
                                            jsReader.skipValue();
                                            break;
                                    }
                                }
                            }
                        } else {
                            jsReader.skipValue();
                        }
                    }
                    jsReader.endObject();
                }
                jsReader.endArray();
                jsReader.close();

            } catch (IOException ignored) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            commCenterName.setText(extra.getStringExtra("Name"));
            commCenterDesc.setText(desc);
            commCenterHours.setText(hours);
            commCenterPhone.setText(phone);
            commCenterWebsite.setText(site);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    startActivity(i);
                }
            });
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detailMapFragment);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(y, x))
                            .title(extra.getStringExtra("Name")));


                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(y, x)));
                    googleMap.setMinZoomPreference(12.0f);

                }
            });
            progressBar.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        progressBar = findViewById(R.id.progressDetail);
        progressBar.setMax(10);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);


        extra = getIntent();
        fab = (FloatingActionButton) findViewById(R.id.callFab);
        commCenterName = findViewById(R.id.commCenterName);
        commCenterDesc = findViewById(R.id.descTV);
        commCenterPhone = findViewById(R.id.phoneTV);
        commCenterWebsite = findViewById(R.id.websiteTV);
        commCenterHours = findViewById(R.id.hoursTV);
        new getDetail().execute();
//        commCenterDesc.setText(extra.getStringExtra("Description"));
//        commCenterPhone.setText(extra.getStringExtra("Phone"));
//        commCenterWebsite.setText(extra.getStringExtra("Website"));
//        commCenterHours.setText(extra.getStringExtra("Hours"));


    }
}
