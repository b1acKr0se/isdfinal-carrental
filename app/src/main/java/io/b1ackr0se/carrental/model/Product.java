package io.b1ackr0se.carrental.model;

import android.net.Uri;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

public class Product{
    private int id;
    private String name;
    private String description;
    private String image;
    private int price;
    private int category;

    public Product(int id, String name, String desc, String image, int price, int category) {
        super();
        this.id = id;
        this.name = name;
        this.description = desc;
        this.image = image;
        this.price = price;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
//        ParseFile imageFile = getParseFile("Image");
//        String imageUrl = imageFile.getUrl();
//        Uri imageUri = Uri.parse(imageUrl);
//        return imageUri.toString();
        return image;
    }

    public int getPrice() {
        return price;
    }

    public int getCategory() {
        return category;
    }

}
