package model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import util.CustomConstans;
import util.LogUti;

/**
 * Created by wilbert on 2016/11/25.
 */
public class User {
    private String name;
    private String password;
    private String id;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
    public User(String name, String password, String id) {
        this.name = name;
        this.password = password;
        this.id = id;
    }

    public User(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.password = jsonObject.getString("password");
            this.id = jsonObject.getString("id");
            this.status = jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        JSONObject jsonObject = null;
        User user = null;
        try {
            jsonObject = new JSONObject(json);
            user = new User(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String getId() {
        return id;
    }

    public Map getMap(){
        Map map = new HashMap<String,String>();
        map.put("name", name == null ? "" : name);
        map.put("password", password == null ? "" : password);
        map.put("id", id == null ? "": id);
        map.put("status", status == null ? "" : status);
        return map;
    }


}
