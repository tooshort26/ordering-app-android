package com.e.maiplaceapp.Models;

public class CustomerRequest {
    private String password;
    private String firstname;
    private String middlename;
    private String lastname;
    private String email;
    private String phone_number;
    private String address;

    public CustomerRequest(String password, String firstname, String middlename, String lastname, String email, String phone_number, String address) {
        this.setPassword(password);
        this.setFirstname(firstname);
        this.setMiddlename(middlename);
        this.setLastname(lastname);
        this.setEmail(email);
        this.setPhone_number(phone_number);
        this.setAddress(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
