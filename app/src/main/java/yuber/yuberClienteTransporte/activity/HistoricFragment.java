package yuber.yuberClienteTransporte.activity;

/**
 * Created by Agustin on 28-Oct-16.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import yuber.yuberClienteTransporte.R;
import yuber.yuberClienteTransporte.adapter.HistorialAdapter;

;

public class HistoricFragment extends Fragment {

    private List<Historial> historialList = new ArrayList<>();
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String EmailKey = "emailKey";
    public static final String HistorialKey = "historialKey";
    public static final String TAG = "FRAGMENTO HISTORIAL";
    SharedPreferences sharedpreferences;
    private String Ip = "";
    private String Puerto = "8080";


    public HistoricFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_historic, container, false);
        Ip = getResources().getString(R.string.IP);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view_historic);
        rv.setHasFixedSize(true);

        MainActivity mainActivity = (MainActivity)getActivity();
        List<Historial> lista = mainActivity.getListaHistorial();
        Historial htemp;
        Iterator<Historial> it = lista.iterator();
        while (it.hasNext()) {
            htemp = it.next();
            historialList.add(htemp);
        }

        HistorialAdapter adapter = new HistorialAdapter(historialList);

        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        rv.addOnItemTouchListener(new HistoricRecyclerTouchListener(getActivity().getApplicationContext(), rv, new HistoricRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Historial historial = historialList.get(position);
                sendBodyToMapFragment(historial);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        return rootView;
    }

    private void sendBodyToMapFragment(Historial historial) {
        Bundle args = new Bundle();
        args.putString("DatosHistorial", historial.toString());
        FragmentDialogYuberHistorial newFragmentDialog = new FragmentDialogYuberHistorial();
        newFragmentDialog.setArguments(args);
        newFragmentDialog.show(getActivity().getSupportFragmentManager(), "TAG");
    }

}