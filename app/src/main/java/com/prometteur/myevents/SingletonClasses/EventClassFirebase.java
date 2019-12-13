package com.prometteur.myevents.SingletonClasses;


import java.io.Serializable;
import java.util.ArrayList;

public class EventClassFirebase implements Serializable {
    private String eventProfileImage, eventStartTime, eventDate, eventEventAddress, eventEndTime, eventInvitees, eventTitle, eventComments, eventCreatorProfile, eventCreatorName, eventCreatorUserName, eventCreateDate,eventKey;
    ArrayList<EventImageFirebase> eventImages;
    private ArrayList<String> membersNumber;


    public EventClassFirebase() {
    }

    public EventClassFirebase (
            String eventProfileImage,
            String eventTitle,
            ArrayList<EventImageFirebase> eventImages,
            String eventComments,
            String eventCreatorProfile,
            String eventCreatorName,
            String eventCreatorUserName,
            String eventCreateDate,
            String eventStartTime,
            String eventDate,
            String eventEventAddress,
            String eventEndTime,
            String eventInvitees,
            String eventKey,
            ArrayList<String> membersNumber

    ) {

        this.eventProfileImage = eventProfileImage;
        this.eventTitle = eventTitle;
        this.eventImages = eventImages;
        this.eventComments = eventComments;
        this.eventCreatorProfile = eventCreatorProfile;
        this.eventCreatorName = eventCreatorName;
        this.eventCreatorUserName = eventCreatorUserName;
        this.eventCreateDate = eventCreateDate;
        this.eventStartTime = eventStartTime;
        this.eventDate = eventDate;
        this.eventEventAddress = eventEventAddress;
        this.eventEndTime = eventEndTime;
        this.eventInvitees = eventInvitees;
        this.eventKey = eventKey;
        this.membersNumber = membersNumber;
    }

    public String getEventComments() {
        return eventComments;
    }

    public void setEventComments(String eventComments) {
        this.eventComments = eventComments;
    }

    public String getEventInvitees() {
        return eventInvitees;
    }

    public void setEventInvitees(String eventInvitees) {
        this.eventInvitees = eventInvitees;
    }

    public String getEventCreatorUserName() {
        return eventCreatorUserName;
    }

    public void setEventCreatorUserName(String eventCreatorUserName) {
        this.eventCreatorUserName = eventCreatorUserName;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventEventAddress() {
        return eventEventAddress;
    }

    public void setEventEventAddress(String eventEventAddress) {
        this.eventEventAddress = eventEventAddress;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public String getEventProfileImage() {
        return eventProfileImage;
    }

    public void setEventProfileImage(String eventProfileImage) {
        this.eventProfileImage = eventProfileImage;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }


    public ArrayList<EventImageFirebase> getEventImages() {
        return eventImages;
    }

    public void setEventImages(ArrayList<EventImageFirebase> eventImages) {
        this.eventImages = eventImages;
    }

    public String getEventCreatorProfile() {
        return eventCreatorProfile;
    }

    public void setEventCreatorProfile(String eventCreatorProfile) {
        this.eventCreatorProfile = eventCreatorProfile;
    }

    public String getEventCreatorName() {
        return eventCreatorName;
    }

    public void setEventCreatorName(String eventCreatorName) {
        this.eventCreatorName = eventCreatorName;
    }

    public String getEventCreateDate() {
        return eventCreateDate;
    }

    public void setEventCreateDate(String eventCreateDate) {
        this.eventCreateDate = eventCreateDate;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public ArrayList<String> getMembersNumber() {
        return membersNumber;
    }

    public void setMembersNumber(ArrayList<String> membersNumber) {
        this.membersNumber = membersNumber;
    }
}