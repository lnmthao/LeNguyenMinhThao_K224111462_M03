package com.lnmt.models;

public class Task {
    private int id;
    private String title;
    private String dateAssigned;
    private boolean isCompleted;

    public Task(int id, String title, String dateAssigned, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.dateAssigned = dateAssigned;
        this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDateAssigned() { return dateAssigned; }
    public boolean isCompleted() { return isCompleted; }
}
