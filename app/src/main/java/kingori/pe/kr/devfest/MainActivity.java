package kingori.pe.kr.devfest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.exec_okhttp_json)
    public void getOkHttpJson() {
        execOkHttpReq("https://kingoritours.firebaseio.com/tours.json");
    }

    @OnClick(R.id.exec_okhttp_image)
    public void getOkHttpImage() {
        execOkHttpReq("https://festi.kr/media/festi/speaker/2016/10/10/9f/9f90146230a74d38bcecea49f5833f76.jpg");
    }

    private void execOkHttpReq(final String url) {
        new AsyncTask<String, Void, Response>() {
            @Override
            protected Response doInBackground(String... params) {
                try {
                    Response resp = Application.getOkHttpClient().newCall(
                            new Request.Builder().url(params[0]).build()).execute();
                    try (BufferedInputStream bis = new BufferedInputStream(resp.body().byteStream())) {
                        do {
                            bis.read();
                        } while (bis.available() > 0);
                    } catch (IOException e) {
                        resp = null;
                    }
                    return resp;
                } catch (IOException e) {
                    Log.e(MainActivity.class.getName(), "error", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Response response) {
                Toast.makeText(MainActivity.this, response != null ? "Success" : "Failed", Toast.LENGTH_SHORT).show();
            }
        }.execute(url);
    }

    @OnClick(R.id.link_socket)
    public void moveToSocket() {
        startActivity(new Intent(this, SocketIOActivity.class));
    }


}
