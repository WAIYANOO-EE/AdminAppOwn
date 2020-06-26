package com.waiyanoo.moviesadmin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.waiyanoo.moviesadmin.R;
import com.waiyanoo.moviesadmin.adapters.MovieAdapter;
import com.waiyanoo.moviesadmin.models.Movie;
import com.waiyanoo.moviesadmin.ui.MovieBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Movie> movies;
    private RecyclerView recyclerView;


    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         final Context context = container.getContext();
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        FloatingActionButton createGenreFAB = view.findViewById(R.id.fab_create_movie);
        recyclerView = view.findViewById(R.id.recycler_view);
        EditText searchEditText = view.findViewById(R.id.et_search);

        movies = new ArrayList<>();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int count) {
                String search = s.toString().toLowerCase().trim();

                if (count > 0) {
                    ArrayList<Movie> filterMovies = new ArrayList<>();

                    for (Movie movie : movies) {
                        if (movie.getName().toLowerCase().contains(search)) {
                            filterMovies.add(movie);
                        }
                    }
                    MovieAdapter adapter = new MovieAdapter(MoviesFragment.this, filterMovies);
                    recyclerView.setAdapter(adapter);

                } else {
                    MovieAdapter adapter = new MovieAdapter(MoviesFragment.this, movies);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createGenreFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieBottomSheet bottomSheet = new MovieBottomSheet();
                bottomSheet.show(getFragmentManager(),bottomSheet.getTag());
            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        db.collection(Movie.COLLECTION_NAME)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ArrayList<Movie> movies = new ArrayList<>();
                        for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                            Movie movie = doc.toObject(Movie.class);
                            movies.add(movie);
                        }
                        MovieAdapter adapter = new MovieAdapter(MoviesFragment.this, movies);
                        recyclerView.setAdapter(adapter);
                    }
                });

        return view;
    }
}
