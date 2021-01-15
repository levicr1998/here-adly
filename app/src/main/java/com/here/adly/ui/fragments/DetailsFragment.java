package com.here.adly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.adly.R;
import com.here.adly.db.DatabaseFB;
import com.here.adly.models.Feature;
import com.here.adly.models.FeatureCollection;
import com.here.adly.ui.activities.MainActivity;
import com.here.adly.viewmodels.FavItemViewModel;
import com.here.adly.viewmodels.ReviewItemViewModel;
import com.here.adly.webservices.APIServiceHERE;
import com.here.adly.webservices.ClientHERE;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment {

    private TextView tvAdName, tvAdPrice, tvAdDescription, tvAdRatingStatus;
    private Chip chAdType, chAdAvailability;
    private Button btnAdFavorite, btnAdReviews, btnBook;
    private ImageView ivAdImage;
    private APIServiceHERE apiServiceHERE;
    private ClientHERE clientHERE;
    private DatabaseFB databaseFB;
    private DatabaseReference mFavoritesReference, mReviewsReference;
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
        tvAdPrice = view.findViewById(R.id.details_ad_price);
        tvAdDescription = view.findViewById(R.id.details_ad_description);
        tvAdRatingStatus = view.findViewById(R.id.tv_review_item_rating_status);
        chAdType = view.findViewById(R.id.details_ad_type);
        chAdAvailability = view.findViewById(R.id.details_ad_availability);
        ivAdImage = view.findViewById(R.id.details_ad_picture);
        btnBook = view.findViewById(R.id.btn_details_book);
        btnAdFavorite = view.findViewById(R.id.btn_details_favorite);
        btnAdReviews = view.findViewById(R.id.btn_details_reviews);
        ratingBar = view.findViewById(R.id.rb_details_item_rate);
        clientHERE = new ClientHERE();
        apiServiceHERE = clientHERE.setupClient();
        String adSpaceId = dataBundle.getString("adSpaceId");
        String adName = dataBundle.getString("adName");
        String adId = dataBundle.getString("adId");
        String userId = mAuth.getCurrentUser().getUid();
        getDetailsAdvertisement(adId, adSpaceId, userId);
        btnAdFavorite.setOnClickListener(view1 -> {
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

    private void getDetailsAdvertisement(String adId, String adSpaceId, String userId) {

        getAdStatusFavorite(userId, adId);
        mReviewsReference = FirebaseDatabase.getInstance().getReference().child("adReviews").child(adId);
        getReviewsScoreAndCount();
        Call<Feature> callAdDetails = apiServiceHERE.getFeaturebyId(adSpaceId, adId);
        executeGetDetailsAd(callAdDetails, adSpaceId);

    }

    private void executeGetDetailsAd(Call<Feature> call, String adSpaceId) {
        call.enqueue(new Callback<Feature>() {
            @Override
            public void onResponse(Call<Feature> call, Response<Feature> response) {
                Feature feature = response.body();
                tvAdName.setText(feature.getProperties().getName());
                String price = "€" + feature.getProperties().getPrice() + " p/week";
                tvAdPrice.setText(price);
                setTypeDetails(adSpaceId);
                chAdAvailability.setText(checkAvailability(feature.getProperties().isAvailable()));
                ((MainActivity) getActivity()).loadingDialog.stopLoading();

            }

            @Override
            public void onFailure(Call<Feature> call, Throwable t) {
            }
        });
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

                if (isAdded()) {
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
    }

    public void setTypeDetails(String spaceId) {
        String typeName = "unknown";
        String typeDescription = "unknown";
        switch (spaceId) {
            case Feature.SPACE_NAME_EUROPANEL:
                typeName = "Europanel";
                typeDescription = getResources().getString(R.string.europanel_description);
                ivAdImage.setBackground(getResources().getDrawable(R.drawable.picture_details_europanel));
                break;
            case Feature.SPACE_NAME_TWOSIGN:
                typeName = "2-Sign";
                typeDescription = getResources().getString(R.string.twosign_description);
                ivAdImage.setBackground(getResources().getDrawable(R.drawable.picture_details_two_sign));
                break;
            case Feature.SPACE_NAME_ABRI:
                typeName = "Abri";
                typeDescription = getResources().getString(R.string.abri_description);
                ivAdImage.setBackground(getResources().getDrawable(R.drawable.picture_details_abri));
                break;

        }
        chAdType.setText(typeName);
        tvAdDescription.setText(typeDescription);
    }

    public String checkAvailability(boolean available) {
        if (available) {
            chAdAvailability.setChipIconVisible(true);
            return "Available";

        }
        chAdAvailability.setChipIconVisible(false);
        return "Not available";
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

    private void getReviewsScoreAndCount() {
        mReviewsReference.keepSynced(false);
        mReviewsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double reviewScoreTotal = 0;
                double reviewScore = 0;
                int reviewCount = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ReviewItemViewModel review = postSnapshot.getValue(ReviewItemViewModel.class);
                    reviewCount++;
                    reviewScoreTotal += Double.parseDouble(review.getRating());
                }
                if (reviewCount > 0) {
                    reviewScore = reviewScoreTotal / reviewCount;
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");

                    String reviewScoreResult = decimalFormat.format(reviewScore);
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    symbols.setDecimalSeparator(',');
                    decimalFormat.setDecimalFormatSymbols(symbols);
                    try {
                        Float reviewScoreResultFloat = decimalFormat.parse(reviewScoreResult).floatValue();
                        ratingBar.setRating(reviewScoreResultFloat);
                        tvAdRatingStatus.setText("Average rating");

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    tvAdRatingStatus.setText("No rating available");
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Fout");
            }
        });
        ((MainActivity) getActivity()).loadingDialog.stopLoading();
    }

}
