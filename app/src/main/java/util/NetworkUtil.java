package util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
public class NetworkUtil {
    private static OkHttpClient client = new OkHttpClient();

    public interface NetworkCallBack {
        void onResponse(Response response);
    }
    public static void upload(String name, File file, final NetworkCallBack networkCallBack){

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
                if (networkCallBack != null) {
                    networkCallBack.onResponse(response);
                }
            }
        });
    }

    public static void download(String name, final NetworkCallBack networkCallBack) {
        String url = CustomConstans.url + "download?fileName="+name;
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TravelAlbum");
        final File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                name);
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUti.d("download fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUti.d("download succ");
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    LogUti.d("total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(mediaFile);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        LogUti.d("current------>" + current);
                        fos.flush();
                    }
                    networkCallBack.onResponse(response);
                } catch (IOException e) {
                    LogUti.d(e.toString());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        LogUti.d(e.toString());
                    }
                }
            }
        });
    }
}
