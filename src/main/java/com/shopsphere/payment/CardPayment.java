package com.shopsphere.payment;

public class CardPayment implements Payment {

    @Override
    public void pay(double amount) {
        System.out.println("Payment of ₹" + amount + " made using Card.");
    }
}