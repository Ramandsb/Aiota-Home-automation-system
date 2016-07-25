package in.tagplug.tagplug.WeatherReport;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import in.tagplug.tagplug.R;
import in.tagplug.tagplug.pojo.Notify;

public class RemoteFetch {

	private static final String OPEN_WEATHER_MAP_API =
			"http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
	
	public static JSONObject getJSON(Context context, String city){
		try {
			URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
			Notify.log("urlis",url.toString());
			HttpURLConnection connection =
					(HttpURLConnection)url.openConnection();
			
			connection.addRequestProperty("x-api-key", 
					context.getString(R.string.open_weather_maps_app_id));
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			
			StringBuffer json = new StringBuffer(1024);
			String tmp="";
			while((tmp=reader.readLine())!=null)
				json.append(tmp).append("\n");
			reader.close();
			
			JSONObject data = new JSONObject(json.toString());
			Log.d("jsonData",data.toString());
			
			if(data.getInt("cod") != 200){
				return null;
			}
			
			return data;
		}catch(Exception e){
			Log.d("error",e.toString());
			return null;
		}
	}
	
}
