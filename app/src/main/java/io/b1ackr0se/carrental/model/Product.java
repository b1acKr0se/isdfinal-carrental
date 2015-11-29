package io.b1ackr0se.carrental.model;

import android.net.Uri;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Product")
public class Product extends ParseObject {
    private int id;
    private String name;
    private String description;
    private String image;
    private int price;
    private int category;

    public Product() {
        super();
    }

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
        return getInt("productId");
    }

    public String getName() {
        return getString("Name");
    }

    public String getDescription() {
        return getString("Description");
    }

    public String getImage() {
        ParseFile imageFile = getParseFile("Image");
        String imageUrl = imageFile.getUrl();
        Uri imageUri = Uri.parse(imageUrl);
        return imageUri.toString();
    }

    public int getPrice() {
        return getInt("Price");
    }

    public int getCategory() {
        return getInt("CategoryId");
    }

}
