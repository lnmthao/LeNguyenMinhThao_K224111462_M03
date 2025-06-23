package com.lnmt.models;

public class TaskDetail {
    private int id;
    private int taskId;
    private int customerId;
    private int isCalled;

    private String customerName;
    private String phoneNumber;

    public TaskDetail(int id, int taskId, int customerId, int isCalled) {
        this.id = id;
        this.taskId = taskId;
        this.customerId = customerId;
        this.isCalled = isCalled;
    }

    public int getId() {
        return id;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getIsCalled() {
        return isCalled;
    }

    public void setIsCalled(int isCalled) {
        this.isCalled = isCalled;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
