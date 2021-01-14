package com.here.adly.ui.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.adly.R;
import com.here.adly.adapters.ReviewsAdapter;
import com.here.adly.ui.activities.MainActivity;
import com.here.adly.viewmodels.FavItemViewModel;
import com.here.adly.viewmodels.ReviewItemViewModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    public List<ReviewItemViewModel> reviewItemList;
    private Button btnWriteReview;
    private TextView tvAverageScore, tvAmountReviewers;
    private RatingBar rbAverageScore;
    private FirebaseAuth mAuth;
    public ReviewsAdapter reviewsAdapter;
    public DatabaseReference mReviewsReference;
    private Bundle dataBundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        this.reviewItemList = new ArrayList<>();
        this.mAuth = FirebaseAuth.getInstance();
        this.dataBundle = getArguments();
        recyclerView = view.findViewById(R.id.rv_reviews);
        tvAverageScore = view.findViewById(R.id.tv_average_review_score);
        tvAmountReviewers = view.findViewById(R.id.tv_amount_reviews);
        rbAverageScore = view.findViewById(R.id.rb_average_review_score);
        btnWriteReview = view.findViewById(R.id.btn_reviews_write_review);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        String adId = dataBundle.getString("adId");
        mReviewsReference = FirebaseDatabase.getInstance().getReference().child("adReviews").child(adId);
        getReviewsList();
        getReviewsScoreAndCount();

        btnWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWriteReviewFragment(adId);
            }
        });

        return view;
    }

    private void getReviewsList() {
        FirebaseRecyclerOptions<ReviewItemViewModel> options = new FirebaseRecyclerOptions.Builder<ReviewItemViewModel>().setQuery(mReviewsReference, ReviewItemViewModel.class).build();
        reviewsAdapter = new ReviewsAdapter(options);
        recyclerView.setAdapter(reviewsAdapter);
    }

    private void getReviewsScoreAndCount() {
        mReviewsReference.keepSynced(true);
        mReviewsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double reviewScoreTotal = 0;
                double reviewScore = 0;
                int reviewCount = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ReviewItemViewModel review = postSnapshot.getValue(ReviewItemViewModel.class);
                    reviewCount++;
                    System.out.println(review.getRating());
                    reviewScoreTotal += Double.parseDouble(review.getRating());
                }
                if (reviewCount > 0) {
                    reviewScore = reviewScoreTotal / reviewCount;
                }


                DecimalFormat decimalFormat = new DecimalFormat("#.#");

                String reviewScoreResult = decimalFormat.format(reviewScore);
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                decimalFormat.setDecimalFormatSymbols(symbols);
                try {
                    Float reviewScoreResultFloat = decimalFormat.parse(reviewScoreResult).floatValue();
                    rbAverageScore.setRating(reviewScoreResultFloat);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tvAverageScore.setText(reviewScoreResult);
                String amountReviewers = "based on " + reviewCount + " reviews";
                tvAmountReviewers.setText(amountReviewers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Fout");
            }
        });
        ((MainActivity) getActivity()).loadingDialog.stopLoading();
    }

    @Override
    public void onStart() {
        super.onStart();
        reviewsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        reviewsAdapter.stopListening();
    }


    private void startWriteReviewFragment(String adId) {
        WriteReviewFragment writeReviewFragment = new WriteReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("adId", adId);
        writeReviewFragment.setArguments(bundle);
        this.getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, writeReviewFragment).commit();
    }
}
