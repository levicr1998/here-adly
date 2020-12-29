package com.here.adly.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.here.adly.R;
import com.here.adly.db.DatabaseFB;
import com.here.adly.viewmodels.FavItemViewModel;
import com.here.adly.viewmodels.ReviewItemViewModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewsAdapter extends FirebaseRecyclerAdapter<ReviewItemViewModel, ReviewsAdapter.ViewHolder> {

    private DatabaseReference mReviewWriterReference;
    private DatabaseFB databaseFB;

    public ReviewsAdapter(@NonNull FirebaseRecyclerOptions<ReviewItemViewModel> options) {
        super(options);
        databaseFB = new DatabaseFB();
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder viewHolder, int i, @NonNull ReviewItemViewModel reviewItemViewModel) {
        this.setWriterReview(viewHolder, reviewItemViewModel.getUserId());
        viewHolder.tvReviewMessage.setText(reviewItemViewModel.getMessage());
        viewHolder.rbReview.setRating(Float.parseFloat(reviewItemViewModel.getRating()));
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);

       return new ReviewsAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReviewWriter;
        private TextView tvReviewMessage;
        private RatingBar rbReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewWriter = itemView.findViewById(R.id.tv_review_item_writer);
            tvReviewMessage = itemView.findViewById(R.id.tv_review_item_message);
            rbReview = itemView.findViewById(R.id.rb_item_rate);
        }
    }

    private void setWriterReview(ViewHolder viewHolder, String userId) {
        mReviewWriterReference = databaseFB.mDatabase.child("Users").child(userId).child("fullName");
        mReviewWriterReference.keepSynced(true);
        mReviewWriterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String authorName  = snapshot.getValue(String.class);
                    viewHolder.tvReviewWriter.setText(authorName);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Fout");
            }
        });
    }
}
