package com.here.adly.adapters;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.here.adly.ui.fragments.DetailsFragment;
import com.here.adly.viewmodels.FavItemViewModel;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesAdapter extends FirebaseRecyclerAdapter<FavItemViewModel, FavoritesAdapter.ViewHolder> {
    private List<FavItemViewModel> favoriteViewModelList = new ArrayList<>();
    private DatabaseReference mFavoriteReference;

    public FavoritesAdapter(@NonNull FirebaseRecyclerOptions<FavItemViewModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull FavItemViewModel favItemViewModel) {
            favItemViewModel.setAdId(getSnapshots().getSnapshot(position).getKey());
            viewHolder.tvItemName.setText(favItemViewModel.getAdName());
            favoriteViewModelList.add(favItemViewModel);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item, parent, false);

        return new FavoritesAdapter.ViewHolder(view);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private FirebaseAuth mAuth;
        private DatabaseFB databaseFB;
        private TextView tvItemName;
        private Button btnFavItem;
        private CardView cvItemCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_favorites_item_name);
            btnFavItem = itemView.findViewById(R.id.btn_favorites_item_favorite);
            cvItemCard = itemView.findViewById(R.id.cv_item_card);
            mAuth = FirebaseAuth.getInstance();
            databaseFB = new DatabaseFB();
            btnFavItem.setOnClickListener(view -> {

                int position = getAdapterPosition();

                final FavItemViewModel favItem = favoriteViewModelList.get(position);
                removeFavoriteStatus(mAuth.getUid(), favItem.getAdId(), position);

            });

            cvItemCard.setOnClickListener(view -> {
                int position = getAdapterPosition();
                final FavItemViewModel favItem = favoriteViewModelList.get(position);
                startFavoritesFragment(favItem.getAdName(), favItem.getAdId());
            });

        }


        private void removeFavoriteStatus(String userId, String adId, int position) {
            mFavoriteReference = databaseFB.mDatabase.child("userFavorites").child(userId).child(adId);
            mFavoriteReference.keepSynced(true);
            mFavoriteReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().removeValue();
                    favoriteViewModelList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, favoriteViewModelList.size());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        private void startFavoritesFragment(String featureName, String featureId) {


            Bundle bundle = new Bundle();
            bundle.putString("adName", featureName);
            bundle.putString("adId", featureId);
            DetailsFragment detailsFragment = new DetailsFragment();
            AppCompatActivity mainActivity = (AppCompatActivity) itemView.getContext();
            detailsFragment.setArguments(bundle);

            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailsFragment).commit();
        }

    }
}
