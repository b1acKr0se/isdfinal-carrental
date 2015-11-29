package io.b1ackr0se.carrental.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

    private int id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private int type;
    private int status;

    public User() {
        super();
    }

    public User(int id, String name, String email, String phone, int type, int status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.type = type;
        this.status = status;
    }

    public void saveUser(int id, String name, String password, String email, String phone, int type, int status) {
        saveId(id);
        saveName(name);
        savePassword(password);
        saveEmail(email);
        savePhone(phone);
        saveType(type);
        saveStatus(status);
        saveInBackground();
    }

    public int getId() {
        return getInt("userId");
    }

    public void saveId(int id) {
        put("userId", id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return getString("userName");
    }

    public void saveName(String name) {
        put("userName", name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return getString("Password");
    }

    public void savePassword(String password) {
        put("Password", password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return getString("Email");
    }

    public void saveEmail(String email) {
        put("Email", email);
    }

    public void seEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return getString("Phone");
    }

    public void savePhone(String phone) {
        put("Phone", phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return getInt("Type");
    }

    public void saveType(int type) {
        put("Type", type);
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return getInt("Status");
    }

    public void saveStatus(int status) {
        put("Status", status);
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
