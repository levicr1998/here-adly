package com.here.adly.viewmodels;

public class UserFavoriteViewModel {



    public boolean favorite;

    public UserFavoriteViewModel(){

    }

    public UserFavoriteViewModel(boolean isFavorite) {
        this.favorite = isFavorite;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
