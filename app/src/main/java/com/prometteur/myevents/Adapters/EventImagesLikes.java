package com.prometteur.myevents.Adapters;

import java.io.Serializable;

public class EventImagesLikes implements Serializable {

    private String strlike;

    public EventImagesLikes() {
    }

    public String getLikeorName() {
        return likeorName;
    }

    public void setLikeorName(String likeorName) {
        this.likeorName = likeorName;
    }

    public String getLikeorUserName() {
        return likeorUserName;
    }

    public void setLikeorUserName(String likeorUserName) {
        this.likeorUserName = likeorUserName;
    }

    public String getLikeorProfile() {
        return likeorProfile;
    }

    public void setLikeorProfile(String likeorProfile) {
        this.likeorProfile = likeorProfile;
    }

    public String getLikeDateTime() {
        return likeDateTime;
    }

    public void setLikeDateTime(String likeDateTime) {
        this.likeDateTime = likeDateTime;
    }

    private String likeorName;
    private String likeorUserName;
    private String likeorProfile;
    private String likeDateTime;

    public EventImagesLikes(String strlike, String likeorName, String likeorUserName, String likeorProfile, String likeDateTime) {
        this.strlike = strlike;
        this.likeorName = likeorName;
        this.likeorUserName = likeorUserName;
        this.likeorProfile = likeorProfile;
        this.likeDateTime = likeDateTime;
    }

    public String getStrlike() {
        return strlike;
    }

    public void setStrlike(String strlike) {
        this.strlike = strlike;
    }
}