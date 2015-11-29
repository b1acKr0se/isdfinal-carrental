package io.b1ackr0se.carrental.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Category")
public class Category extends ParseObject {
    private int id;
    private String name;

    public Category() {
        super();
    }

    public Category(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return getInt("id");
    }

    public String getName() {
        return getString("name");
    }
}
