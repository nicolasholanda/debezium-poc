package com.github.nicolasholanda.debezium_poc.model;

public class UserChangeEventDTO {
    public String op;
    public User after;
    public User before;

    public static class User {
        public Integer id;
        public String name;
        public String email;
        public Long updated_at;
    }
}
