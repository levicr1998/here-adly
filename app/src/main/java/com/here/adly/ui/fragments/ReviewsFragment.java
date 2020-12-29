package com.here.adly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.here.adly.R;
import com.here.adly.adapters.ReviewsAdapter;
import com.here.adly.viewmodels.ReviewItemViewModel;

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reviews");
        recyclerView = view.findViewById(R.id.rv_reviews);
        btnWriteReview = view.findViewById(R.id.btn_reviews_write_review);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        String adId = dataBundle.getString("adId");
        getReviewsList(adId);

        btnWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWriteReviewFragment(adId);
            }
        });

        return view;
    }

    private void getReviewsList(String adId) {

        mReviewsReference = FirebaseDatabase.getInstance().getReference().child("adReviews").child(adId);
        FirebaseRecyclerOptions<ReviewItemViewModel> options = new FirebaseRecyclerOptions.Builder<ReviewItemViewModel>().setQuery(mReviewsReference, ReviewItemViewModel.class).build();
        for (ReviewItemViewModel item : options.getSnapshots()){
            System.out.println(item.toString());
        }
        reviewsAdapter = new ReviewsAdapter(options);
        recyclerView.setAdapter(reviewsAdapter);
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
        this.getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, writeReviewFragment ).commit();
    }
}
