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
import com.here.adly.ui.activities.MainActivity;
import com.here.adly.utils.MapMarkerPlacer;
import com.here.adly.viewmodels.FavItemViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerViewEuropanel, recyclerViewTwoSign, recyclerViewAbri;
    public List<FavItemViewModel> favItemList;
    private FirebaseAuth mAuth;
    public FavoritesAdapter favoritesAdapterEuropanel, favoritesAdapterTwoSign, favoritesAdapterAbri;
    public DatabaseReference mFavoritesReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        this.favItemList = new ArrayList<>();
        this.mAuth = FirebaseAuth.getInstance();
        recyclerViewEuropanel = view.findViewById(R.id.rv_favorites_europanel);
        recyclerViewAbri = view.findViewById(R.id.rv_favorites_abri);
        recyclerViewTwoSign = view.findViewById(R.id.rv_favorites_twosign);
        setUpRecyclerView(recyclerViewEuropanel);
        setUpRecyclerView(recyclerViewAbri);
        setUpRecyclerView(recyclerViewTwoSign);


        favoritesAdapterEuropanel = new FavoritesAdapter(getOptionsAdapter(mAuth.getUid(), MapMarkerPlacer.SPACE_NAME_EUROPANEL));
        recyclerViewEuropanel.setAdapter(favoritesAdapterEuropanel);
        favoritesAdapterAbri = new FavoritesAdapter(getOptionsAdapter(mAuth.getUid(), MapMarkerPlacer.SPACE_NAME_ABRI));
        recyclerViewAbri.setAdapter(favoritesAdapterAbri);
        favoritesAdapterTwoSign = new FavoritesAdapter(getOptionsAdapter(mAuth.getUid(), MapMarkerPlacer.SPACE_NAME_TWOSIGN));
        recyclerViewTwoSign.setAdapter(favoritesAdapterTwoSign);


        return view;
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private FirebaseRecyclerOptions<FavItemViewModel> getOptionsAdapter(String userId, String spaceId) {
        mFavoritesReference = FirebaseDatabase.getInstance().getReference().child("userFavorites").child(userId);
        FirebaseRecyclerOptions<FavItemViewModel> options = new FirebaseRecyclerOptions.Builder<FavItemViewModel>().setQuery(mFavoritesReference.orderByChild("spaceId").equalTo(spaceId), FavItemViewModel.class).build();
        return options;
    }

    @Override
    public void onStart() {
        super.onStart();
        favoritesAdapterEuropanel.startListening();
        favoritesAdapterAbri.startListening();
        favoritesAdapterTwoSign.startListening();
        ((MainActivity) getActivity()).loadingDialog.stopLoading();
    }

    @Override
    public void onStop() {
        super.onStop();
        favoritesAdapterEuropanel.stopListening();
        favoritesAdapterAbri.stopListening();
        favoritesAdapterTwoSign.stopListening();
    }
}
