package com.here.adly.viewmodels;

public class ReviewItemViewModel {

    private String message;
    private String rating;
    private String userId;

    public ReviewItemViewModel(){

    }

    public ReviewItemViewModel(String message, String rating) {
        this.message = message;
        this.rating = rating;
    }

    public ReviewItemViewModel(String message, String rating, String userId) {
        this.message = message;
        this.rating = rating;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
