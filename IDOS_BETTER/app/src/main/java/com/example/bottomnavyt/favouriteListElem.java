package com.example.bottomnavyt;


/*
 * Author: Michal Dohnal
 * Login : xdohna52
 *
 * */
public class favouriteListElem {
    private String text;
    private int id;

    public favouriteListElem(String text, int id) {
        this.text = text;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
}
