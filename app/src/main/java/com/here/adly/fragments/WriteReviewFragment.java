package com.here.adly.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.here.adly.R;
import com.here.adly.db.DatabaseFB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WriteReviewFragment extends Fragment {

    private Button btnSubmit;
    private RatingBar rbReview;
    private EditText etMessage;
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
        etMessage = view.findViewById(R.id.et_writereview_message);
        btnSubmit = view.findViewById(R.id.btn_writereview_submit);
        String adId = dataBundle.getString("adId");

        btnSubmit.setOnClickListener(view1 -> {
            int rating = (int) rbReview.getRating();
            addUserReview(mAuth.getUid(), adId,etMessage.getText().toString(), Integer.toString(rating));
            getFragmentManager().popBackStack();
        });

        return view;
    }


    private void addUserReview(String userId, String adId, String message, String rating) {

        databaseFB.mDatabase.child("adReviews").child(adId).child(userId).child("message").setValue(message);
        databaseFB.mDatabase.child("adReviews").child(adId).child(userId).child("rating").setValue(rating);


    }
}
