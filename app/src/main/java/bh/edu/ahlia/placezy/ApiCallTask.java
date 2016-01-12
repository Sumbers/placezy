package bh.edu.ahlia.placezy;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import okhttp3.OkHttpClient;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Sumbers on 05/01/2016.
 */
public class ApiCallTask extends AsyncTask<String, Integer, Places> {
    public static final int ARRAY = 0;
    public static final int OBJECT = 1;
    //the APi key of the application
    public static final String KEY = "key";
    //format of location is : latitude,longiture
    public static final String LOCATION = "location";
    //radius of research up to 50 000m
    public static final String RADIUS = "radius";
    //choosea language of displayed datas : ar (arabic), en (english), fr (french)
    public static final String LANGUAGE = "language";
    //separate types of place researched as : hospital|drug_store
    public static final String TYPE = "types";

    public static final String URL_API = "https://maps.googleapis.com/maps/api/";

    IApiCallTask _obj = null;
    private int type;
    private String action = null;

    public ApiCallTask(IApiCallTask obj, int typ, String act)
    {
        this._obj = obj;
        this.type = typ;
        this.action = act;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Places doInBackground(String... params) {
        Places places = null;
        try {
            Call<Places> listPlacesCall = doGet(params);
            System.out.println("j'ai recupéré la requète");

            Response reponse = listPlacesCall.execute();
            System.out.println("j'ai excecuté la requete");

            if (reponse.isSuccess())
            {
                places = (Places) reponse.body();
                System.out.println("status du succes = " + ((Places) reponse.body()).getStatus());
                System.out.println("body du succes = " + reponse.raw().toString());
                System.out.println("places google map = " + places);
            }
            else{
                System.err.println("la requète est un echec. Code d'erreur : " + reponse.code() + "\n message d'erreur = " + reponse.errorBody().string());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return (places);
    }

    protected Call<Places> doGet(String... params) throws UnsupportedEncodingException, IOException, URISyntaxException
    {
        System.out.println("creation de la requeete");
        OkHttpClient httpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        GetPlacesService service =  retrofit.create(GetPlacesService.class);
        System.out.println("key = " + params[0]);
        Call<Places> listPlacesCall = service.getPlaces(params[0], params[1], params[2], params[3], params[4]);
        System.out.println("j'ai passé la creation de la requete");
        return (listPlacesCall);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Places places) {
        super.onPostExecute(places);
        try {
            System.out.println("on retourne dans le thread principal");
            _obj.onBackgroundTaskCompleted(places, type, action);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface GetPlacesService {
        @GET("place/nearbysearch/json")
        Call<Places> getPlaces(@Query("key") String keyApi,
                             @Query("language") String langInfos,
                             @Query("location") String userLocation,
                             @Query("radius") String radiusResearch,
                             @Query("types") String placesResearch);
    }

    public interface getPlacesWithTokenService {
        @GET("place/nearbysearch/json")
        Call<Places> getPlaces(@Query("key") String keyApi,
                               @Query("pagetoken") String pageToken);
    }

}


