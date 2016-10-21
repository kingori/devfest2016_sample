package kingori.pe.kr.devfest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * copied from http://socket.io/blog/native-socket-io-and-android/
 * server: https://github.com/socketio/socket.io/tree/master/examples/chat
 */
public class SocketIOActivity extends BaseActivity {
    @BindView(R.id.socket_addr)
    EditText socketAddr;

    @BindView(R.id.socket_resp)
    TextView socketResp;

    @BindView(R.id.socket_input)
    EditText socketInput;

    @BindView(R.id.socket_send)
    Button sendBtn;

    @BindView(R.id.socket_connect)
    Button socketConnect;

    Socket socket = null;

    String myName = "and" + System.currentTimeMillis();

    SocketIOLogger logger = Application.getSocketIoLogger();

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            SocketIOActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    logger.onReceived(data.toString());
                    // add the message to view
                    addMessage(username, message);
                }
            });
        }
    };


    private Emitter.Listener onSocketConnChanged = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            SocketIOActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (socket.connected()) {
                        emmit("add user", myName);
                    }
                    sendBtn.setEnabled(socket.connected());
                    socketConnect.setEnabled(!socket.connected());
                }
            });
        }
    };

    private void addMessage(String username, String message) {
        socketResp.append(String.format("%s: %s\n", username, message));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        ButterKnife.bind(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        sendBtn.setEnabled(socket != null && socket.connected());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (socket != null) {
            socket.off("new message", onNewMessage);
            socket.off(Socket.EVENT_CONNECT, onSocketConnChanged);
            socket.off(Socket.EVENT_DISCONNECT, onSocketConnChanged);
            socket.disconnect();
        }
    }

    @OnClick(R.id.socket_connect)
    public void connectSocket() {
        try {
            socket = IO.socket(URI.create("http://" + socketAddr.getText().toString() + ":3000"));
            socket.on("new message", onNewMessage);
            socket.on(Socket.EVENT_CONNECT, onSocketConnChanged);
            socket.on(Socket.EVENT_DISCONNECT, onSocketConnChanged);
            if (socket.connected()) {
                onSocketConnChanged.call();
            } else {
                socket.connect();
            }
        } catch (Throwable e) {
            Log.e(SocketIOActivity.class.getName(), "error", e);
        }
    }

    void emmit(String event, Object arg) {
        final String reqId = "socket:" + System.currentTimeMillis();
        logger.onSent(reqId, event, arg.toString());
        socket.emit(event, arg, new Ack() {
            @Override
            public void call(Object... args) {
                logger.onReceivedAck(reqId, args[0].toString());
            }
        });
    }

    @OnClick(R.id.socket_send)
    public void sendToSocket() {
        String message = socketInput.getText().toString().trim();
        if (!message.isEmpty()) {
            emmit("new message", message);
            addMessage(myName, message);
            socketInput.setText("");
        }
    }
}