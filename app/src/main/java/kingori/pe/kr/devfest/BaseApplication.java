package kingori.pe.kr.devfest;

import android.content.SharedPreferences;

import okhttp3.OkHttpClient;

public abstract class BaseApplication extends android.app.Application {

    private static OkHttpClient okHttpClient;
    private static SocketIOLogger socketIOLogger;
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        okHttpClient = createOkHtpClientBuilder().build();
        socketIOLogger = createSocketIOLogger();

        SharedPreferences sf = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
        sf.edit().putLong(Constants.PREF_LAST_EXEC_TIME, System.currentTimeMillis()).commit();
    }

    protected SocketIOLogger createSocketIOLogger() {
        return new NoOpSocketIOLogger();
    }

    private static class NoOpSocketIOLogger implements SocketIOLogger {
        @Override
        public void onSent(String requestId, String event, String message) {
        }

        @Override
        public void onReceivedAck(String requestId, String ackMessage) {
        }

        @Override
        public void onReceived(String message) {
        }
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    static SocketIOLogger getSocketIoLogger() {
        return socketIOLogger;
    }

    protected OkHttpClient.Builder createOkHtpClientBuilder() {
        return new OkHttpClient.Builder();
    }

    public static BaseApplication getInstance() {
        return instance;
    }

}
