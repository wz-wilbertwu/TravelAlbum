package wilbert.com.travelalbum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.User;
import util.CustomConstans;
import util.LogUti;

public class LoginActivity extends AppCompatActivity {
    EditText passwordEditText;
    EditText nameEditText;
    RequestQueue requestQueue;
    public static final String USERMESSAGE = "USERMESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        requestQueue = Volley.newRequestQueue(this);

        loginBtn.setOnClickListener(onClickListener);
        registerBtn.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.registerBtn:
                    if (!checkValid()) {
                        Toast.makeText(LoginActivity.this,
                                "请输入正确的用户名和密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    registerUser(nameEditText.getText().toString(), passwordEditText.getText().toString());
                    break;
                case R.id.loginBtn:
                    if (!checkValid()) {
                        Toast.makeText(LoginActivity.this,
                                "请输入正确的用户名和密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LoginUser(nameEditText.getText().toString(), passwordEditText.getText().toString());
                    break;
            }
        }
    };

    private void LoginUser(final String name, final String password) {
        User user = new User(name, password);
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.GET, user.getLoginUrl(),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {
                        LogUti.d("login success:");
                        LogUti.d(response.toString());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(USERMESSAGE, response.toString());
                        startActivity(intent);
                    } else {
                        LogUti.d("login fail");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        requestQueue.add(loginRequest);
    }

    private void registerUser(String name, String password) {
        User user = new User(name, password);
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.GET, user.getRegisterUrl(),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("result").equals("success")) {
                        LogUti.d("register success:");
                        LogUti.d(response.toString());
                    } else {
                        LogUti.d("register fail");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        requestQueue.add(loginRequest);
    }

    private boolean checkValid() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())
                || TextUtils.isEmpty(passwordEditText.getText().toString())) {
            return false;
        }
        return true;
    }
}
