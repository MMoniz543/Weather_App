package edu.umb.cs443;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback{

	public final static String DEBUG_TAG="edu.umb.cs443.MYMSG";
    private static String urlString;
    private static String BitmapString;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText cityEntered = (EditText) findViewById(R.id.editText);
                String city= cityEntered.getText().toString();

                String APIKEY="9261b1aea45d92dff8577f813e8d7203";
                urlString = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+APIKEY;
                new StringTask().execute(urlString);
                new DownloadWebpageTask().execute(BitmapString);

            }
        });
        MapFragment mFragment=((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap=map;
    }


    private class StringTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... strings){
            String stream = null;
            String urlString = strings[0];

            edu.umb.cs443.HTTPDataHandler hh = new edu.umb.cs443.HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }

        protected void onPostExecute(String stream){
            TextView txtTemp = (TextView) findViewById(R.id.txtTemp);

            if(stream !=null){
                try{
                    ///Creates JSON Reader Object
                    JSONObject reader= new JSONObject(stream);

                    // 1. Get the JSONObject "coord"
                    JSONObject coord = reader.getJSONObject("coord");
                    Double lon = coord.getDouble("lon");
                    Double lat = coord.getDouble("lat");
                    CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
                    CameraUpdate zoom= CameraUpdateFactory.zoomTo(12);
                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);

                    //2. Gets the Temp
                    JSONObject temp = reader.getJSONObject("main");
                    String thisTemp = temp.getString("temp");
                    double temperature = ConvertTemperatureToFarenheit(Double.valueOf(thisTemp));
                    String myTemp =String.format("%.1f", temperature);
                    txtTemp.setText(myTemp+"f");

                    //3.gets icon data for BitMapString
                    JSONArray weatherArray = reader.getJSONArray("weather");
                    JSONObject weather_object_0 = weatherArray.getJSONObject(0);
                    String weather_0_icon = weather_object_0.getString("icon");
                    BitmapString = "http://openweathermap.org/img/w/" + weather_0_icon+".png";

                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        }
        private double ConvertTemperatureToFarenheit(double temperature) {
            double temp = (temperature - 273)*(9/5)+32;
            return temp;
        }
    }


   private class DownloadWebpageTask extends AsyncTask<String, Void, Bitmap> {
       @Override
       protected Bitmap doInBackground(String... urls) {
           // params comes from the execute() call: params[0] is the url.
           try {
               return downloadUrl(urls[0]);
           } catch (IOException e) {
               return null;
           }
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(Bitmap result) {
           ImageView img=(ImageView) findViewById(R.id.TempImageView);
           if(result!=null) img.setImageBitmap(result);
           else{
               Log.i(DEBUG_TAG, "returned bitmap is null");}
       }
   }

    private Bitmap downloadUrl(String BitmapString) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(BitmapString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.i(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}


