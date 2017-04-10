package com.example.christopher_santana.cs457bookstore;

import java.util.Date;

/**
 * Created by Christopher on 4/5/2017.
 */

public class Book {
    private String title;
    private String ISBN;
    private double price;
    public Book(String ttl, String isbn, double prc){
        title = ttl;
        ISBN = isbn;
        price = prc;
    }

    public Book(){
        title = "";
        ISBN = "";
        price = 0;
    }

    public Book setTitle(String ttl){
        title = ttl;
        return this;
    }

    public Book setISBN(String ISBN) {
        this.ISBN = ISBN;
        return this;
    }

    public Book setPrice(double price) {
        this.price = price;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }
}
