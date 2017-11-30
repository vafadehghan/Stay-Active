package newwest.stayactive.stayactive;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
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
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    Double x;
    Double y;
    JsonReader jsReader;
    TextView commCenterName;
    TextView commCenterDesc;
    TextView commCenterPhone;
    TextView commCenterHours;
    Intent extra;
    String hours;
    String desc;
    String pc;
    String phone;
    String location;
    String site;
    ProgressBar progressBar;
    FloatingActionButton fab;
    Button commCenterWebsiteBtn;
    URL endpoint;
    HttpURLConnection myConn;

    private class getDetail extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {


                endpoint = new URL("http://opendata.newwestcity.ca/downloads/community-programming/PARKS_RECREATION_AND_COMMUNITY_SCHOOL_PROGRAMMING.json");
                myConn = (HttpURLConnection) endpoint.openConnection();
                myConn.setRequestMethod("GET");

//                InputStream is = getResources().openRawResource(R.raw.community);
                InputStream is = myConn.getInputStream();
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
                                        case "PC":
                                            pc = jsReader.nextString();
                                            break;
                                        case "Location":
                                            location = jsReader.nextString();
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
            commCenterDesc.setText(desc + " \n\n " + location + "\n" + pc);
            commCenterHours.setText(hours);
            commCenterPhone.setText(phone);
            commCenterWebsiteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(site);
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i);
                }
            });
            try {

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                        try {
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(DetailActivity.this, "Calling not supported", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(DetailActivity.this, "Calling not supported", Toast.LENGTH_SHORT).show();
            }

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detailMapFragment);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
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
        fab = findViewById(R.id.callFab);
        Typeface bold = Typeface.createFromAsset(getAssets(), "fonts/gotham_bold.otf");
        Typeface medium = Typeface.createFromAsset(getAssets(), "fonts/gotham_medium.otf");
        Typeface book = Typeface.createFromAsset(getAssets(), "fonts/gotham_book.otf");
        Typeface light = Typeface.createFromAsset(getAssets(), "fonts/gotham_light.otf");


        commCenterName = findViewById(R.id.commCenterName);
        commCenterDesc = findViewById(R.id.descTV);
        commCenterPhone = findViewById(R.id.phoneTV);
        commCenterHours = findViewById(R.id.hoursTV);
        commCenterPhone.setLinkTextColor(getResources().getColor(R.color.listBlue));
        commCenterName.setTypeface(bold);
        commCenterDesc.setTypeface(book);
        commCenterHours.setTypeface(book);
        commCenterPhone.setTypeface(book);
        commCenterWebsiteBtn = findViewById(R.id.websiteButton);
        commCenterWebsiteBtn.setTypeface(book);

        new getDetail().execute();

    }
}
