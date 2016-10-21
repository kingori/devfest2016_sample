package kingori.pe.kr.devfest;

import okhttp3.OkHttpClient;

public abstract class BaseApplication extends android.app.Application {

    private static OkHttpClient okHttpClient;
    private static SocketIOLogger socketIOLogger;

    @Override
    public void onCreate() {
        super.onCreate();
        okHttpClient = createOkHtpClientBuilder().build();
        socketIOLogger = createSocketIOLogger();
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

}
