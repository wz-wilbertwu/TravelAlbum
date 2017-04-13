package wilbert.com.travelalbum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.SessionRequest;
import model.User;
import util.CustomConstans;
import util.LogUti;

public class LoginActivity extends AppCompatActivity {
    EditText passwordEditText;
    EditText nameEditText;
    RequestQueue requestQueue;
    String cookie;
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
        final User user = new User(name, password);
        SessionRequest sessionRequest = new SessionRequest(CustomConstans.url + "login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            cookie = jsonObject.getString("Cookie");
                            String data = jsonObject.getString("Data");
                            if (data.indexOf("Hello") != -1) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra(USERMESSAGE, response);
                                intent.putExtra("Cookie", cookie);
                                startActivity(intent);
                            } else  {
                                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
            }
        }, user.getMap());
        requestQueue.add(sessionRequest);
/*        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                CustomConstans.url + "User/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        User user1 = User.getUserFromJson(response);
                        if (user1.getStatus() != null && user1.getStatus().equals("succ")) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(USERMESSAGE, response);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUti.d(error.toString());
                        Toast.makeText(LoginActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return user.getMap();
            }
        };
        requestQueue.add(stringRequest);*/

    }

    private void registerUser(String name, String password) {
        final User postUser = new User(name, password);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CustomConstans.url +
                "User/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        User user = User.getUserFromJson(response);
                        if (user != null && user.getStatus() != null && user.getStatus().equals("succ")) {
                            Toast.makeText(LoginActivity.this, "注册成功\n请登录", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(LoginActivity.this, "名称已被使用", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUti.d(error.toString());
                        Toast.makeText(LoginActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return postUser.getMap();
            }
        };
        requestQueue.add(stringRequest);
    }

    private boolean checkValid() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())
                || TextUtils.isEmpty(passwordEditText.getText().toString())) {
            return false;
        }
        return true;
    }
}
