package com.here.adly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.here.adly.R;
import com.here.adly.adapters.FavoritesAdapter;
import com.here.adly.viewmodels.FavItemViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    public List<FavItemViewModel> favItemList;
    private FirebaseAuth mAuth;
    public FavoritesAdapter favoritesAdapter;
    public DatabaseReference mFavoritesReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        this.favItemList = new ArrayList<>();
        this.mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.rv_favorites);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getFavoritesList(mAuth.getUid());



        return view;
    }

    private void getFavoritesList(String userId) {
        mFavoritesReference = FirebaseDatabase.getInstance().getReference().child("userFavorite").child(userId);
        System.out.println(mFavoritesReference);
        FirebaseRecyclerOptions<FavItemViewModel> options = new FirebaseRecyclerOptions.Builder<FavItemViewModel>().setQuery(mFavoritesReference.orderByChild("status").equalTo(true), FavItemViewModel.class).build();
        favoritesAdapter = new FavoritesAdapter(options);
        recyclerView.setAdapter(favoritesAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();
        favoritesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        favoritesAdapter.stopListening();
    }
}
