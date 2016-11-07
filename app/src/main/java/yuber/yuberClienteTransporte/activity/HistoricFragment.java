package yuber.yuberClienteTransporte.activity;

/**
 * Created by Agustin on 28-Oct-16.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;;import java.util.ArrayList;
import java.util.List;

import yuber.yuberClienteTransporte.R;
import yuber.yuberClienteTransporte.adapter.HistorialAdapter;

public class HistoricFragment extends Fragment {

    private List<Movie> movieList = new ArrayList<>();


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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);

        //LO NUEVO QUE HICE 30-OCT

        prepareMovieData();
        HistorialAdapter adapter = new HistorialAdapter(movieList);



        // termina lo nuevo




        //HistorialAdapter adapter = new HistorialAdapter(new String[]{"test one", "test two", "test three", "test four", "test five" , "test six" , "test seven", "test eight", "test nine", "test ten"});
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);


/*
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
                Toast.makeText(getActivity().getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        return rootView;
    }




    private void prepareMovieData() {
        Movie movie = new Movie("23/10/2016", "5 km", "$250");
        movieList.add(movie);


        movie = new Movie("20/010/2016", "4 km", "$200");
        movieList.add(movie);

        movie = new Movie("19/09/2016", "2,5 km", "$125");
        movieList.add(movie);

        movie = new Movie("19/09/2016", "3,5 km", "$175");
        movieList.add(movie);

        movie = new Movie("29/07/2016", "1 km", "$50");
        movieList.add(movie);

        movie = new Movie("19/05/2016", "10 km", "$500");
        movieList.add(movie);

        movie = new Movie("02/05/2016", "12 km", "$600");
        movieList.add(movie);

        movie = new Movie("29/03/2016", "3 km", "$150");
        movieList.add(movie);

        movie = new Movie("19/02/2016", "5 km", "$250");
        movieList.add(movie);

        movie = new Movie("19/01/2016", "1 km", "$50");
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