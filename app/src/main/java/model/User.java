package model;

import org.json.JSONException;
import org.json.JSONObject;

import util.CustomConstans;
import util.LogUti;

/**
 * Created by wilbert on 2016/11/25.
 */
public class User {
    private String name;
    private String password;
    private String id;
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
    public User(String name, String password, String id) {
        this.name = name;
        this.password = password;
        this.id = id;
    }

    public String getLoginUrl() {
        String url = CustomConstans.url + "?name=%s&password=%s&field=user&method=login";
        String result = String.format(url, name, password);
        LogUti.d(result);
        return result;
    }
    public String getRegisterUrl() {
        String url = CustomConstans.url + "?name=%s&password=%s&field=user&method=register";
        String result = String.format(url, name, password);
        LogUti.d(result);
        return result;
    }
    public static User getUserFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            User user = new User(jsonObject.getString("name"), jsonObject.getString("password"),
                    jsonObject.getString("id"));
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
