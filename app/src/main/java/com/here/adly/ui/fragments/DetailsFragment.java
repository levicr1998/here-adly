package com.here.adly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.here.adly.R;
import com.here.adly.db.DatabaseFB;
import com.here.adly.viewmodels.FavItemViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {

    private TextView tvAdName;
    private Button btnAdFavorite, btnAdReviews;

    private DatabaseFB databaseFB;
    private DatabaseReference mFavoritesReference;
    private FavItemViewModel favItemViewModel;
    private FirebaseAuth mAuth;
    private Bundle dataBundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_advertisement, container, false);
        this.databaseFB = new DatabaseFB();
        this.mAuth = FirebaseAuth.getInstance();
        this.dataBundle = getArguments();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Details");
        tvAdName = view.findViewById(R.id.details_ad_name);
        btnAdFavorite = view.findViewById(R.id.btn_details_favorite);
        btnAdReviews = view.findViewById(R.id.btn_details_reviews);
        getDetailsAdvertisement();
        btnAdFavorite.setOnClickListener(view1 -> {
            if (favItemViewModel.getStatus() == true) {
                setAdStatusFavorite(mAuth.getCurrentUser().getUid(), dataBundle.getString("adId"), false);
            } else {
                setAdStatusFavorite(mAuth.getCurrentUser().getUid(), dataBundle.getString("adId"), true);
            }
        });

        btnAdReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startReviewsFragment(dataBundle.getString("adId"));
            }
        });

        return view;
    }

    private void getDetailsAdvertisement() {
        String adSpaceId = dataBundle.getString("adSpaceId");
        String adName = dataBundle.getString("adName");
        String adId = dataBundle.getString("adId");
        getAdStatusFavorite(mAuth.getCurrentUser().getUid(), adId, adSpaceId, adName);
        tvAdName.setText(adName);
    }

    private void startReviewsFragment(String adId) {
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("adId", adId);
        reviewsFragment.setArguments(bundle);
        this.getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, reviewsFragment).commit();
    }

    private void getAdStatusFavorite(String userId, String adId, String spaceId, String adName) {
        mFavoritesReference = databaseFB.mDatabase.child("userFavorite").child(userId).child(adId);
        mFavoritesReference.keepSynced(true);
        mFavoritesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    favItemViewModel = snapshot.getValue(FavItemViewModel.class);


                    if (favItemViewModel.getStatus() == true) {
                        btnAdFavorite.setBackgroundResource(R.drawable.ic_favorite_red);
                    } else {
                        btnAdFavorite.setBackgroundResource(R.drawable.ic_favorite);
                    }

                } else {
                    if (spaceId != null) {
                        addUserFavorite(userId, adId, spaceId, adName, false);
                    } else {
                        addSpaceId(userId, adId);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Fout");
            }
        });
    }


    private void setAdStatusFavorite(String userId, String adId, boolean statusFavorite) {

        databaseFB.mDatabase.child("userFavorite").child(userId).child(adId).child("status").setValue(statusFavorite);
    }

    private void addUserFavorite(String userId, String adId, String spaceId, String adName, boolean statusFavorite) {

        databaseFB.mDatabase.child("userFavorite").child(userId).child(adId).child("status").setValue(statusFavorite);
        databaseFB.mDatabase.child("userFavorite").child(userId).child(adId).child("spaceId").setValue(spaceId);
        databaseFB.mDatabase.child("userFavorite").child(userId).child(adId).child("adName").setValue(adName);
    }

    private void addSpaceId(String userId, String adId) {
        DatabaseReference spaceReference = databaseFB.mDatabase.child("userFavorite").child(userId).child(adId).child("spaceId");
        spaceReference.keepSynced(true);
        spaceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataBundle.putString("adSpaceId", snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
