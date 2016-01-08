package bh.edu.ahlia.placezy;

import android.R.layout;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSelect.SelectFragmentListener} interface
 * to handle interaction events.
 */
public class FragmentSelect extends Fragment implements AdapterView.OnItemSelectedListener{
    private static final String FRAGMENT_SELECT_TAG = "selectTag";

    private SelectFragmentListener mListener;

    private Spinner placeSpinner;
    public LinearLayout mLinearLayout;

    public FragmentSelect() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select, container, false);
        placeSpinner = (Spinner) view.findViewById(R.id.place_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.places, layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        placeSpinner.setAdapter(adapter);
        placeSpinner.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectFragmentListener) {
            mListener = (SelectFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SelectFragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String placeSelected = (String) parent.getItemAtPosition(position);
        System.out.println("place selectionnée = " + placeSelected);
        mListener.onPlaceSelected(placeSelected);
        //TODO faire les requetes au serveur google pour récupérer les informations de la place


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SelectFragmentListener {
        void onPlaceSelected(String placeSelected);
    }
}
