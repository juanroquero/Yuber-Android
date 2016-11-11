package yuber.yuberClienteTransporte.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yuber.yuberClienteTransporte.R;
import yuber.yuberClienteTransporte.adapter.ServiciosAdapter;

;

public class ServiciosFragment extends Fragment {

    private List<Movie> movieList = new ArrayList<>();
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String IdServicioKey = "IdServicioKey";

    public ServiciosFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_servicios, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);

        //LO NUEVO QUE HICE 30-OCT

        prepareMovieData();
        ServiciosAdapter adapter = new ServiciosAdapter(movieList);



        // termina lo nuevo



        //HistorialAdapter adapter = new HistorialAdapter(new String[]{"test one", "test two", "test three", "test four", "test five" , "test six" , "test seven", "test eight", "test nine", "test ten"});
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        //AGREGA LA RAYITA AL MEDIO
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());

/*      QUE ONDA CON ESTO? NO ES MAS NECESARIO?
        rv.addOnItemTouchListener(new HistoricRecyclerTouchListener(rootView.getApplicationContext(), rv, new HistoricRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Movie movie = movieList.get(position);
               //Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity().getApplicationContext(), position + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

*/

        rv.addOnItemTouchListener(new HistoricRecyclerTouchListener(getActivity().getApplicationContext(), rv, new HistoricRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie movie = movieList.get(position);
                // Seteo en el SharedPreferences el ID del servicio generado y voy a mapa
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_MULTI_PROCESS);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(IdServicioKey, position);
                Toast.makeText(getActivity().getApplicationContext(), "Ha seleccionado viajar en " + " servicio con ID:"+ position , Toast.LENGTH_SHORT).show();
                cambiarAMain(position);

                // set the toolbar textNombreServicio
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        return rootView;
    }


    public void cambiarAMain(int id){
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setmIdServicio(id);
        mainActivity.displayView(1);
        //Intent goToMainIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
       // goToMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(goToMainIntent);
    }


    private void prepareMovieData() {
        Movie movie = new Movie("Auto", "5 km", "$250");
        movieList.add(movie);


        movie = new Movie("Moto", "4 km", "$200");
        movieList.add(movie);

        movie = new Movie("Limusina", "2,5 km", "$125");
        movieList.add(movie);

        movie = new Movie("Helicoptero", "3,5 km", "$175");
        movieList.add(movie);

        movie = new Movie("Tren", "1 km", "$50");
        movieList.add(movie);

        movie = new Movie("Bicicleta", "10 km", "$500");
        movieList.add(movie);

        movie = new Movie("Bote", "1 km", "$50");
        movieList.add(movie);

        movie = new Movie("Monopatin", "12 km", "$600");
        movieList.add(movie);

        movie = new Movie("Taxi", "3 km", "$150");
        movieList.add(movie);

        movie = new Movie("MotoTaxi", "5 km", "$250");
        movieList.add(movie);

        movie = new Movie("10/01/2016", "2,2km", "$110");
        movieList.add(movie);

        movie = new Movie("19/07/2015", "2 km", "$100");
        movieList.add(movie);

        movie = new Movie("01/01/2015", "12 km", "$600");
        movieList.add(movie);


/*
        movie = new Movie("Inside Out", "Animation, Kids & Family", "2015");
        movieList.add(movie);

        movie = new Movie("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        movieList.add(movie);

        movie = new Movie("Shaun the Sheep", "Animation", "2015");
        movieList.add(movie);

        movie = new Movie("The Martian", "Science Fiction & Fantasy", "2015");
        movieList.add(movie);

        movie = new Movie("Mission: Impossible Rogue Nation", "Action", "2015");
        movieList.add(movie);

        movie = new Movie("Up", "Animation", "2009");
        movieList.add(movie);

        movie = new Movie("Star Trek", "Science Fiction", "2009");
        movieList.add(movie);

        movie = new Movie("The LEGO Movie", "Animation", "2014");
        movieList.add(movie);

        movie = new Movie("Iron Man", "Action & Adventure", "2008");
        movieList.add(movie);

        movie = new Movie("Aliens", "Science Fiction", "1986");
        movieList.add(movie);

        movie = new Movie("Chicken Run", "Animation", "2000");
        movieList.add(movie);

        movie = new Movie("Back to the Future", "Science Fiction", "1985");
        movieList.add(movie);

        movie = new Movie("Raiders of the Lost Ark", "Action & Adventure", "1981");
        movieList.add(movie);

        movie = new Movie("Goldfinger", "Action & Adventure", "1965");
        movieList.add(movie);

        movie = new Movie("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        movieList.add(movie);
*/
        //mAdapter.notifyDataSetChanged();
    }



}