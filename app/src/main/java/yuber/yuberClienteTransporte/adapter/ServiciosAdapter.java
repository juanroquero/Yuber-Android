package yuber.yuberClienteTransporte.adapter;

/**
 * Created by Agustin on 28-Oct-16.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yuber.yuberClienteTransporte.R;
import yuber.yuberClienteTransporte.activity.Movie;

public class ServiciosAdapter extends RecyclerView.Adapter<ServiciosAdapter.MyViewHolder> {

    private List<Movie> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textNombreServicio;
       // public TextView textNombreServicio, year, genre;

        public MyViewHolder(View view) {
            super(view);
            textNombreServicio = (TextView) view.findViewById(R.id.textNombreServicio);
            //genre = (TextView) view.findViewById(R.id.genre);
            //year = (TextView) view.findViewById(R.id.year);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public ServiciosAdapter(List<Movie> myDataset) {

        moviesList = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ServiciosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.servicios_list_row, parent, false);

        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.textNombreServicio.setText(movie.getTitle());
        //holder.genre.setText(movie.getGenre());
       // holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}