package kingori.pe.kr.devfest;

public interface SocketIOLogger {

    void onSent(String requestId, String event, String message);

    void onReceivedAck(String requestId, String ackMessage);

    void onReceived(String message);
}
