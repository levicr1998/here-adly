package com.here.adly.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.adly.R;
import com.here.adly.db.DatabaseFB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {

    TextView TVadName;
    Button BTNadFavorite;
    boolean isFavorited;
    public DatabaseFB databaseFB;
    public DatabaseReference mFavoritesReference;
    private FirebaseAuth mAuth;
    Bundle b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_advertisement, container, false);
        this.databaseFB = new DatabaseFB();
        this.mAuth = FirebaseAuth.getInstance();
        this.b = getArguments();
        TVadName = view.findViewById(R.id.details_ad_name);
        BTNadFavorite = view.findViewById(R.id.btn_details_favorite);
        getDetailsAdvertisement();
        BTNadFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorited == true) {
                    setAdStatusFavorite(mAuth.getCurrentUser().getUid(), b.getString("adId"), false);
                } else {
                    setAdStatusFavorite(mAuth.getCurrentUser().getUid(), b.getString("adId"), true);
                }
            }
        });

        return view;
    }

    private void getDetailsAdvertisement() {
        String nameAd = b.getString("adId");
        getAdStatusFavorite(mAuth.getCurrentUser().getUid(), b.getString("adId"));
        TVadName.setText(nameAd);
    }

    private void getAdStatusFavorite(String userId, String adId) {

        mFavoritesReference = FirebaseDatabase.getInstance().getReference().child("userFavorite").child(userId).child(adId);
        mFavoritesReference.keepSynced(true);
        mFavoritesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {

                    isFavorited = (Boolean) snapshot.getValue();
                    if (isFavorited == true) {
                        BTNadFavorite.setBackgroundResource(R.drawable.ic_favorite_red);
                    } else {
                        BTNadFavorite.setBackgroundResource(R.drawable.ic_favorite);
                    }
                } else {
                    setAdStatusFavorite(userId,adId,false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
System.out.println("Fout");
            }
        });
    }

    private void setAdStatusFavorite(String userId, String adId, boolean statusFavorite) {

        databaseFB.mDatabase.child("userFavorite").child(userId).child(adId).setValue(statusFavorite);
    }
}
