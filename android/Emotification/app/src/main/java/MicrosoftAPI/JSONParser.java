package MicrosoftAPI;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import MicrosoftAPI.MicrosoftAPIResponse;

/**
 * Created by mdislam on 10/22/16.
 */
public class JSONParser {

    private String json;

    public JSONParser(String json) {
        this.json = json;
    }

    public MicrosoftAPIResponse getParsedResponse(){

        try {
            JSONObject rootObject = new JSONObject(json);
            JSONArray rootArray = rootObject.getJSONArray("documents");
            JSONObject scoreObj = (JSONObject) rootArray.get(0);

            MicrosoftAPIResponse response = new MicrosoftAPIResponse();
            response.setId(scoreObj.getString("id"));
            response.setScore(scoreObj.getString("score"));

            return response;

        } catch (JSONException e) {
            Log.d("Crash", "Not Working JSON");
        } catch (NullPointerException e){
            Log.d("Crash", "No response from api");
        }

        return null;
    }


}
