package com.here.adly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.here.adly.R;
import com.here.adly.db.DatabaseFB;
import com.here.adly.ui.activities.MainActivity;
import com.here.adly.viewmodels.FavItemViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {

    private TextView tvAdName;
    private Button btnAdFavorite, btnAdReviews, btnBook;

    private DatabaseFB databaseFB;
    private DatabaseReference mFavoritesReference;
    private FavItemViewModel favItemViewModel;
    private RatingBar ratingBar;
    private FirebaseAuth mAuth;
    private Bundle dataBundle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_advertisement, container, false);
        this.databaseFB = new DatabaseFB();
        this.mAuth = FirebaseAuth.getInstance();
        this.dataBundle = getArguments();
        tvAdName = view.findViewById(R.id.details_ad_name);
        btnBook = view.findViewById(R.id.btn_details_book);
        btnAdFavorite = view.findViewById(R.id.btn_details_favorite);
        btnAdReviews = view.findViewById(R.id.btn_details_reviews);
        ratingBar = view.findViewById(R.id.rb_details_item_rate);
        ratingBar.setRating(5F);
        String adSpaceId = dataBundle.getString("adSpaceId");
        String adName = dataBundle.getString("adName");
        String adId = dataBundle.getString("adId");
        String userId = mAuth.getCurrentUser().getUid();
        getDetailsAdvertisement(adId, adName, userId);
        btnAdFavorite.setOnClickListener(view1 -> {
            ((MainActivity) getActivity()).loadingDialog.startLoading();
            if (favItemViewModel == null) {
                changeStatusFavItem(userId, adId, adSpaceId, adName, true);
            } else {
                changeStatusFavItem(userId, adId, adSpaceId, adName, false);
            }
        });

        btnAdReviews.setOnClickListener(view12 -> startReviewsFragment(adId));

        btnBook.setOnClickListener(view13 -> {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle(getResources().getString(R.string.book_notification_title))
                    .setMessage(getResources().getString(R.string.book_notification_message))
                    .setPositiveButton(getResources().getString(R.string.book_notification_action_ok), (dialogInterface, i) -> dialogInterface.dismiss()).show();
        });

        return view;
    }

    private void getDetailsAdvertisement(String adId, String adName, String userId) {

        getAdStatusFavorite(userId, adId);
        tvAdName.setText(adName);
    }

    private void startReviewsFragment(String adId) {
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("adId", adId);
        reviewsFragment.setArguments(bundle);
        ((MainActivity) getActivity()).loadingDialog.startLoading();
        this.getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, reviewsFragment).commit();
    }

    private void getAdStatusFavorite(String userId, String adId) {
        mFavoritesReference = databaseFB.mDatabase.child("userFavorites").child(userId).child(adId);
        mFavoritesReference.keepSynced(false);
        mFavoritesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean favStatus = false;
                if (snapshot.exists()) {
                    favItemViewModel = snapshot.getValue(FavItemViewModel.class);
                    favStatus = true;
                }

                if(isAdded()){
                    setStatusButtonColor(favStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Fout");
            }
        });
    }

    private void setStatusButtonColor(boolean status) {
        if (status) {
            btnAdFavorite.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        } else {
            btnAdFavorite.setBackgroundTintList(getResources().getColorStateList(R.color.colorNotSelected));
        }
        ((MainActivity) getActivity()).loadingDialog.stopLoading();
    }


    private void changeStatusFavItem(String userId, String adId, String spaceId, String adName, boolean favStatus) {
        if (favStatus) {
            addFavItem(userId, adId, spaceId, adName);
        } else {
            removeFavItem(userId, adId);
            favItemViewModel = null;
        }
    }

    private void addFavItem(String userId, String adId, String spaceId, String adName) {
        DatabaseReference newFavReference = databaseFB.mDatabase.child("userFavorites").child(userId).child(adId);
        newFavReference.setValue(new FavItemViewModel(adName, spaceId));
    }

    private void removeFavItem(String userId, String adId) {
        DatabaseReference removeFavReference = databaseFB.mDatabase.child("userFavorites").child(userId).child(adId);
        removeFavReference.keepSynced(false);
        removeFavReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                setStatusButtonColor(false);
                ((MainActivity) getActivity()).loadingDialog.stopLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
