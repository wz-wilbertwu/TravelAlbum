package wilbert.com.travelalbum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import database.DataBaseManager;
import model.TravelItem;
import util.BitmapUti;
import util.CustomConstans;
import util.LogUti;

public class TravelItemDetail extends AppCompatActivity {
    private Uri photoUri;
    private ImageView imageView;
    private EditText descriptionEditText;
    private DataBaseManager dataBaseManager;
    private String travelId;
    private String imageString;
    private RequestQueue requestQueue;

    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            TravelItem travelItem = new TravelItem(response);
            if (travelItem != null && travelItem.getStatus() != null && travelItem.getStatus().equals("succ")) {
                LogUti.d("操作成功：" + response);
            } else {
                LogUti.d("操作失败：" + response);
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            LogUti.d("错误" + error.toString());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_item_detail);

        travelId = getIntent().getStringExtra(TravelDetail.TRAVEL_ID);

        imageView = (ImageView)findViewById(R.id.itemImageView);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        dataBaseManager = DataBaseManager.getDataBaseManager(this);

        String photoString = (String) getIntent().getExtras().getSerializable(TravelDetail.PHOTO_URI_KEY);
        photoUri = Uri.parse(photoString);
        float px = 200 * (getResources().getDisplayMetrics().densityDpi / 160f);
        Bitmap bitmap = BitmapUti.getBitmapFromUri(this, photoUri, px);
        imageView.setImageBitmap(bitmap);

        imageString = getIntent().getStringExtra(TravelDetail.IMG);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionConfirm:
                /*插入*/
                final TravelItem travelItem = new TravelItem(travelId, descriptionEditText.getText().toString(),
                        imageString);
                String sql = travelItem.getTravelItemInsertSql();
                dataBaseManager.execSQL(sql);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        CustomConstans.url + "TravelItem/add", responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return travelItem.getMap();
                    }
                };
                requestQueue.add(stringRequest);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
