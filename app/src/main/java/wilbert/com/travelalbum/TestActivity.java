package wilbert.com.travelalbum;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

import okhttp3.Response;
import util.AppUtil;
import util.LogUti;
import util.NetworkUtil;

public class TestActivity extends AppCompatActivity {
    private File file;
    public static final String MULTIPART_FORM_DATA = "image/jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button takeBtn = (Button) findViewById(R.id.takeBtn);
        Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
        Button downloadBtn = (Button) findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(clickListener);
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
                    NetworkUtil.upload(file.getName(), file, null);
                    break;
                case R.id.downloadBtn:
                    NetworkUtil.download("1.jpg", new NetworkUtil.NetworkCallBack() {
                        @Override
                        public void onResponse(Response response) {
                            LogUti.d(response.toString());
                        }
                    });
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
