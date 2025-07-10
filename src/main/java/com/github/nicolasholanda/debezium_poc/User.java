package com.github.nicolasholanda.debezium_poc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    public Integer id;
    public String name;
    public String email;

    @JsonProperty("updated_at")
    public Long updatedAt;
}
