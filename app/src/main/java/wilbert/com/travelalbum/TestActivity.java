package wilbert.com.travelalbum;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import model.FileRequest;
import model.MultiplePartRequest;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import util.AppUtil;
import util.CustomConstans;
import util.LogUti;
import util.UploadUti;

public class TestActivity extends AppCompatActivity {
    private File file;
    public static final String MULTIPART_FORM_DATA = "image/jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button takeBtn = (Button) findViewById(R.id.takeBtn);
        Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
        takeBtn.setOnClickListener(clickListener);
        uploadBtn.setOnClickListener(clickListener);
    }

    Button.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.takeBtn:
                    file = AppUtil.getOutputImageFile();
                    /*AppUtil.dispatchTakePictureIntent(TestActivity.this, file);*/
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        Uri photoURI;
                        if (file != null) {
                            photoURI = Uri.fromFile(file);
                        } else {
                            photoURI = AppUtil.getOutImageFileUri();
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, AppUtil.REQUEST_TAKE_PHOTO);
                    }
                    break;
                case R.id.uploadBtn:
/*                    List<Part> partList = new ArrayList<>();
                    partList.add(new StringPart("name", file.getName()));
                    try {
                        partList.add(new FilePart("file", file));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    RequestQueue requestQueue = Volley.newRequestQueue(TestActivity.this);
                    String url = CustomConstans.url + "uploadFile";
                    FileRequest fileRequest = new FileRequest(url, partList.toArray(new Part[partList.size()]),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    LogUti.d(response.toString());
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LogUti.d(error.toString());
                        }
                    });
                    requestQueue.add(fileRequest);*/
                    UploadUti.upload(file.getName(), file, null);
                    break;
            }
            }
        };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUti.d("on result");
        if (requestCode == AppUtil.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            LogUti.d("拍照成功");
        }
    }
}
