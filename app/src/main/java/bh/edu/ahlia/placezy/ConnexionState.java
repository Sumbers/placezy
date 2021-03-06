package bh.edu.ahlia.placezy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sumbers on 28/09/2015.
 */
public class ConnexionState {
    private final ConnectivityManager _cm;
    private NetworkInfo _net;
    Context _context;
    Boolean _connected = false;

    public ConnexionState(Context co)
    {
        _context = co;
        _cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        monitor_network();
    }

//    on verifie l'état de la connexion
    private void monitor_network()
    {
        _net = _cm.getActiveNetworkInfo();
        _connected = (_net != null) ? _net.isConnected() : false;
    }

    public Boolean getStatus() {
        monitor_network();
        return (_connected);}
}
