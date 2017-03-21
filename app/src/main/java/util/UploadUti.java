package util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wilbert on 2017/3/21.
 */
public class UploadUti {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static OkHttpClient client = new OkHttpClient();

    public interface UploadCallback{
        void onResponse(Response response);
    }
    public static void upload(String name, File file, final UploadCallback uploadCallback){

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("file", name, fileBody)
                .addFormDataPart("name", name)
                .build();
        String url = CustomConstans.url + "uploadFile";
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                LogUti.d(" upload jsonString ="+jsonString);
                if (uploadCallback != null) {
                    uploadCallback.onResponse(response);
                }
            }
        });
    }
}
