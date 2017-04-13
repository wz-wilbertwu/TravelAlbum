package model;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wilbert on 2017/4/10.
 */
public class SessionRequest extends Request<String> {
    private Map<String, String> mMap;
    private Response.Listener<String> mListener;
    public String cookieFromResponse;
    private String mHeader;
    private Map<String, String> sendHeader=new HashMap<String, String>(1);
    public SessionRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map map) {
        super(Request.Method.POST, url, errorListener);
        mListener = listener;
        mMap = map;
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mMap;
    }
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            mHeader = response.headers.toString();
            Pattern pattern=Pattern.compile("Set-Cookie.*?;");
            Matcher m=pattern.matcher(mHeader);
            if(m.find()){
                cookieFromResponse =m.group();
            }
            cookieFromResponse = cookieFromResponse.substring(11,cookieFromResponse.length()-1);
            //将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得到
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Data", jsonString);
            jsonObject.put("Cookie",cookieFromResponse);
            return Response.success(jsonObject.toString(),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return sendHeader;
    }
    public void setSendCookie(String cookie){
        sendHeader.put("Cookie",cookie);
    }
}
