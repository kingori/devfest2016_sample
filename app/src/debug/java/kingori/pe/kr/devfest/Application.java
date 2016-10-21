package kingori.pe.kr.devfest;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import kingori.pe.kr.devfest.stetho.StethoSocketIOLogger;
import okhttp3.OkHttpClient;

public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

    @Override
    protected OkHttpClient.Builder createOkHtpClientBuilder() {
        return super.createOkHtpClientBuilder()
                .addNetworkInterceptor(new StethoInterceptor());
    }

    @Override
    protected SocketIOLogger createSocketIOLogger() {
        return new StethoSocketIOLogger();
    }
}
