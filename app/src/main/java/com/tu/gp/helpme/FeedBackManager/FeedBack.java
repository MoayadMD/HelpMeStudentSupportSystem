package com.tu.gp.helpme.FeedBackManager;

public class FeedBack
{
    String notes;
    int writerID,recipientID,rate,requestID;

    public FeedBack(int requestID,String notes, int writerID, int recipientID, int rate) {
        this.notes = notes;
        this.writerID = writerID;
        this.recipientID = recipientID;
        this.rate = rate;
        this.requestID = requestID;
    }


    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getWriterID() {
        return writerID;
    }

    public void setWriterID(int writerID) {
        this.writerID = writerID;
    }

    public int getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(int recipientID) {
        this.recipientID = recipientID;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
