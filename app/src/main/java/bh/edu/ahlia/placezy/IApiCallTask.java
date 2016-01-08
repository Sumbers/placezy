package bh.edu.ahlia.placezy;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Sumbers on 05/01/2016.
 */
public interface IApiCallTask {
    public void onBackgroundTaskCompleted(Places s, int type, String action) throws JSONException;
}
