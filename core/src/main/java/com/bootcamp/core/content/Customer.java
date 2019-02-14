package com.bootcamp.core.content;

public class Customer {
    private int customerId;
    private String name;
    private String shippingAddress;
    private String state;
    private int zipCode;
    private String email;

    public Customer() {
    }

    public Customer(int customerId, String name, String shippingAddress, String state, int zipCode, String email) {
        this.customerId = customerId;
        this.name = name;
        this.shippingAddress = shippingAddress;
        this.state = state;
        this.zipCode = zipCode;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", state='" + state + '\'' +
                ", zipCode=" + zipCode +
                ", email='" + email + '\'' +
                '}';
    }
}
