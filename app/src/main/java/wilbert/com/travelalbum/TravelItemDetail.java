package wilbert.com.travelalbum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import database.DataBaseManager;
import model.FileRequest;
import model.TravelItem;
import util.AppUtil;
import util.BitmapUti;
import util.CustomConstans;
import util.FileUti;
import util.LogUti;
import util.UploadUti;

public class TravelItemDetail extends AppCompatActivity {
    private Uri photoUri;
    private ImageView imageView;
    private EditText descriptionEditText;
    private DataBaseManager dataBaseManager;
    private String travelId;
    private String imageString;
    private RequestQueue requestQueue;
    private File file;
    private boolean isEditable = false;
    private boolean isXinZen = true; //是否新增
    private TravelItem travelItem;
    public static final String EDITABLE_FLAG = "editable_flag";
    public static final String XINZEN_FLAG = "xin_zen_flag";
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
    private Response.Listener<String> fileResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            LogUti.d("操作成功：" + response);
            finish();
        }
    };
    private Response.ErrorListener fileErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            LogUti.d("操作失败：" + error.toString());
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            file = AppUtil.getOutputImageFile();
            photoUri = Uri.fromFile(file);
            AppUtil.dispatchTakePictureIntent(TravelItemDetail.this, file);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppUtil.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            imageView.setImageBitmap(BitmapUti.getBitmapFromUri(this, photoUri, 200));
            //TODO GET IMAGE STRING
            String filePath = file.getPath();
            imageString = filePath.substring(filePath.lastIndexOf(File.separator)+1);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_item_detail);

        imageView = (ImageView)findViewById(R.id.itemImageView);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        imageView.setOnClickListener(onClickListener);

        travelId = getIntent().getStringExtra(TravelDetail.TRAVEL_ID);
        String editableFlag = getIntent().getStringExtra(EDITABLE_FLAG);
        if (editableFlag != null && editableFlag.equals("true")) {
            isEditable = true;
        } else {
            isEditable = false;
        }
        String xinzenFlag = getIntent().getStringExtra(XINZEN_FLAG);
        if (xinzenFlag != null && xinzenFlag.equals("true")) {
            isXinZen = true;
        } else {
            isXinZen = false;
        }
        if (isXinZen) {
            String photoString = (String) getIntent().getExtras().getSerializable(TravelDetail.PHOTO_URI_KEY);
            photoUri = Uri.parse(photoString);
            file = new File(photoUri.getPath());
            imageString = getIntent().getStringExtra(TravelDetail.IMG);
        } else {
            travelItem = getIntent().getExtras().getParcelable(TravelDetail.TRAVEL_ITEM_KEY);
            file = FileUti.getTheFile(travelItem.getImage());
            photoUri = Uri.fromFile(file);
            descriptionEditText.setText(travelItem.getDescription());
        }
        if (isEditable) {
            descriptionEditText.setEnabled(true);
            imageView.setClickable(true);
//            TODO 设置image可编辑
        } else {
            descriptionEditText.setEnabled(false);
            imageView.setClickable(false);
        }

        dataBaseManager = DataBaseManager.getDataBaseManager(this);

        float px = 200 * (getResources().getDisplayMetrics().densityDpi / 160f);
        Bitmap bitmap = BitmapUti.getBitmapFromUri(this, photoUri, px);
        imageView.setImageBitmap(bitmap);

        requestQueue = Volley.newRequestQueue(this);
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
                String sql = null, url = null;
                if (isEditable) {
                    if (!isXinZen) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = format.format(new Date());
                        travelItem.setTime(time);
                        travelItem.setDescription(descriptionEditText.getText().toString());
                        travelItem.setImage(imageString);
                        sql = travelItem.getTravelItemUpdateSql();
                        url = CustomConstans.url + "TravelItem/update";
                    } else {
                        travelItem = new TravelItem(travelId, descriptionEditText.getText().toString(),
                                imageString);
                        sql = travelItem.getTravelItemInsertSql();
                        url = CustomConstans.url + "TravelItem/add";
                    }
                    dataBaseManager.execSQL(sql);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            url, responseListener, errorListener) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            return travelItem.getMap();
                        }
                    };
                    requestQueue.add(stringRequest);

                    UploadUti.upload(file.getName(), file, new UploadUti.UploadCallback() {
                        @Override
                        public void onResponse(okhttp3.Response response) {
                            LogUti.d("upload response");
                            finish();
                        }
                    });
                } else {
//                    不可编辑
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
