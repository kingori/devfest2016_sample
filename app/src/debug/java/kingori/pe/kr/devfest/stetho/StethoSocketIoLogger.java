package kingori.pe.kr.devfest.stetho;

import android.util.Log;

import com.facebook.stetho.inspector.network.DefaultResponseHandler;
import com.facebook.stetho.inspector.network.NetworkEventReporter;
import com.facebook.stetho.inspector.network.NetworkEventReporterImpl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import kingori.pe.kr.devfest.SocketIOLogger;

public class StethoSocketIOLogger implements SocketIOLogger {

    NetworkEventReporter eventReporter;
    Map<String, InspectorRequestImpl> requestMap = new HashMap<>();

    public StethoSocketIOLogger() {
        eventReporter = NetworkEventReporterImpl.get();
    }

    static class InspectorRequestImpl implements NetworkEventReporter.InspectorRequest {
        String requestId;
        String input;
        String event;

        InspectorRequestImpl(String id, String event, String input) {
            this.requestId = id;
            this.input = input;
            this.event = event;
        }

        @Override
        public String id() {
            return requestId;
        }

        @Override
        public String friendlyName() {
            return requestId;
        }

        @Nullable
        @Override
        public Integer friendlyNameExtra() {
            return 0;
        }

        @Override
        public String url() {
            return event;
        }

        @Override
        public String method() {
            return "POST";
        }

        @Nullable
        @Override
        public byte[] body() throws IOException {
            return input.getBytes();
        }

        @Override
        public int headerCount() {
            return 0;
        }

        @Override
        public String headerName(int i) {
            return null;
        }

        @Override
        public String headerValue(int i) {
            return null;
        }

        @Nullable
        @Override
        public String firstHeaderValue(String s) {
            return null;
        }
    }

    static class InspectorResponseImpl implements NetworkEventReporter.InspectorResponse {
        String requestId;

        public InspectorResponseImpl(String requestId) {
            this.requestId = requestId;
        }

        @Override
        public String requestId() {
            return requestId;
        }

        @Override
        public String url() {
            return requestId;
        }

        @Override
        public int statusCode() {
            return 200;
        }

        @Override
        public String reasonPhrase() {
            return "conn success";
        }

        @Override
        public boolean connectionReused() {
            return false;
        }

        @Override
        public int connectionId() {
            return 0;
        }

        @Override
        public boolean fromDiskCache() {
            return false;
        }

        @Override
        public int headerCount() {
            return 1;
        }

        @Override
        public String headerName(int i) {
            return null;
        }

        @Override
        public String headerValue(int i) {
            return null;
        }

        @Nullable
        @Override
        public String firstHeaderValue(String s) {
            if("Content-Type".equals(s)) {
                return "application/json";
            } else {
                return "";
            }
        }
    }

    @Override
    public void onSent(String requestId, String event, String message) {
        InspectorRequestImpl request = new InspectorRequestImpl(requestId, event, message);
        requestMap.put(requestId, request);
        eventReporter.requestWillBeSent(request);
    }

    @Override
    public void onReceivedAck(String requestId, String ackMessage) {
        InspectorRequestImpl request = requestMap.get(requestId);
        eventReporter.responseHeadersReceived(new InspectorResponseImpl(requestId));
        eventReporter.dataSent(requestId, request.input.getBytes().length, request.input.getBytes().length);
        consumeAndCloseStream(eventReporter.interpretResponseStream(requestId, "text/plain", "utf-8",
                new ByteArrayInputStream(ackMessage.getBytes()),
                new DefaultResponseHandler(eventReporter, requestId)));
        requestMap.remove(requestId);
    }

    @Override
    public void onReceived(String message) {
        String requestId = "socket" + System.currentTimeMillis();
        String reqBody = "socket received";
        InspectorRequestImpl request = new InspectorRequestImpl(requestId, "received", reqBody);
        eventReporter.requestWillBeSent(request);
        eventReporter.responseHeadersReceived(new InspectorResponseImpl(requestId));
        eventReporter.dataSent(requestId, reqBody.length(), reqBody.length());
        consumeAndCloseStream(eventReporter.interpretResponseStream(requestId, "application/json", "utf-8", new ByteArrayInputStream(message.getBytes()),
                new DefaultResponseHandler(eventReporter, requestId)));
    }

    private void consumeAndCloseStream(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line;
            if ((line = br.readLine()) != null) {
                //do nothing: just consume stream
            }
        } catch (IOException e) {
            Log.e(StethoSocketIOLogger.class.getName(), "error", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(StethoSocketIOLogger.class.getName(), "error", e);
                }
            }
        }
    }
}
