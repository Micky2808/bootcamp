package com.bootcamp.core.content;


public interface CustomerService {
    // int insertCustData(int customerId, String name, String shippingAddress, String state, int zipCode, String email);

    void insertCustData();
    String getCustomerData(String filter);
}
