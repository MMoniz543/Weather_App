package edu.umb.cs443;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPDataHandler {

    static String stream = null;

    public HTTPDataHandler(){
    }

    public String GetHTTPData(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            if(urlConnection.getResponseCode() == 200)
            {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder thisString = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    thisString.append(line);
                }
                stream = thisString.toString();
                urlConnection.disconnect();
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally {

        }
        return stream;
    }
}