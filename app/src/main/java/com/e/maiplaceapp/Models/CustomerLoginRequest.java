package com.e.maiplaceapp.Models;

public class CustomerLoginRequest {
    private String email;
    private String password;


    public CustomerLoginRequest(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
