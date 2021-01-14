package com.here.adly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.here.adly.R;
import com.here.adly.db.DatabaseFB;
import com.here.adly.viewmodels.ReviewItemViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class WriteReviewFragment extends Fragment {

    private Button btnSubmit;
    private RatingBar rbReview;
    private TextInputLayout etMessage;
    private DatabaseFB databaseFB;
    private FirebaseAuth mAuth;
    private Bundle dataBundle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_review, container, false);
        databaseFB = new DatabaseFB();
        mAuth = FirebaseAuth.getInstance();
        this.dataBundle = getArguments();
        rbReview = view.findViewById(R.id.rb_writereview_rating);
        rbReview.setRating(1.0f);
        etMessage = view.findViewById(R.id.et_writereview_message);
        btnSubmit = view.findViewById(R.id.btn_writereview_submit);
        String adId = dataBundle.getString("adId");

        btnSubmit.setOnClickListener(view1 -> {
            int rating = (int) rbReview.getRating();
            addUserReview(mAuth.getUid(), adId, etMessage.getEditText().getText().toString(), Integer.toString(rating));
            backToReviews();
        });

        rbReview.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating < 1.0f)
                ratingBar.setRating(1.0f);
        });


        return view;
    }


    private void addUserReview(String userId, String adId, String message, String rating) {

        DatabaseReference newReviewReference = databaseFB.mDatabase.child("adReviews").child(adId).push();
        newReviewReference.setValue(new ReviewItemViewModel(message, rating, userId));
    }

    private void backToReviews() {
        getFragmentManager().popBackStack();
    }
}
