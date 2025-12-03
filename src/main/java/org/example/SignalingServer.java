package org.example;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SignalingServer extends WebSocketServer {

    private final Map<String, Set<WebSocket>> rooms = new HashMap<>();
    private final Map<WebSocket, String> socketRoomMap = new HashMap<>();
    // MỚI: Map lưu ID người dùng để báo khi họ thoát
    private final Map<WebSocket, String> socketIdMap = new HashMap<>();

    private final DatabaseManager dbManager;

    public SignalingServer(int port) {
        super(new InetSocketAddress(port));
        this.dbManager = new DatabaseManager();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client kết nối: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String room = socketRoomMap.get(conn);
        String userId = socketIdMap.get(conn);

        if (room != null && rooms.containsKey(room)) {
            Set<WebSocket> clients = rooms.get(room);
            clients.remove(conn);

            // MỚI: Thông báo cho những người còn lại là user này đã thoát
            if (userId != null) {
                JSONObject leaveMsg = new JSONObject();
                leaveMsg.put("type", "user_left");
                leaveMsg.put("id", userId);
                broadcast(room, leaveMsg.toString(), conn);
            }

            if (clients.isEmpty()) rooms.remove(room);
        }
        // Xóa dữ liệu rác
        socketRoomMap.remove(conn);
        socketIdMap.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JSONObject data = new JSONObject(message);
            String type = data.optString("type");

            // Xử lý Database (Register/Login) giữ nguyên
            if ("register".equals(type)) {
                boolean success = dbManager.registerUser(data.getString("username"), data.getString("password"));
                JSONObject response = new JSONObject();
                response.put("type", success ? "register_success" : "register_fail");
                conn.send(response.toString());
                return;
            }
            if ("login".equals(type)) {
                boolean success = dbManager.loginUser(data.getString("username"), data.getString("password"));
                JSONObject response = new JSONObject();
                if (success) {
                    response.put("type", "login_success");
                    response.put("username", data.getString("username"));
                } else {
                    response.put("type", "login_fail");
                }
                conn.send(response.toString());
                return;
            }

            // Xử lý WebRTC
            String room = data.optString("room");
            if (room.isEmpty()) return;

            if ("join".equals(type)) {
                rooms.computeIfAbsent(room, k -> Collections.synchronizedSet(new HashSet<>()));
                rooms.get(room).add(conn);
                socketRoomMap.put(conn, room);

                // MỚI: Lưu ID người dùng
                String id = data.optString("id");
                if (!id.isEmpty()) socketIdMap.put(conn, id);

                System.out.println("Client " + id + " vào phòng " + room);
            }

            broadcast(room, message, conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm gửi tin nhắn cho mọi người trong phòng trừ người gửi
    private void broadcast(String room, String text, WebSocket sender) {
        Set<WebSocket> clients = rooms.get(room);
        if (clients != null) {
            synchronized (clients) {
                for (WebSocket client : clients) {
                    if (client != sender && client.isOpen()) {
                        client.send(text);
                    }
                }
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) { ex.printStackTrace(); }

    @Override
    public void onStart() { System.out.println("Server đang chạy port: " + getPort()); }

    public static void main(String[] args) {
        new SignalingServer(3001).start();
    }
}