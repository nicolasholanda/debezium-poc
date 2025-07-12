package com.github.nicolasholanda.debezium_poc.model;

import com.github.nicolasholanda.debezium_poc.User;

public class UserChangeEventDTO {
    public String op;
    public User after;
    public User before;

    @Override
    public String toString() {
        return "UserChangeEventDTO{" +
                "op='" + op + '\'' +
                ", after=" + after +
                ", before=" + before +
                '}';
    }
}
