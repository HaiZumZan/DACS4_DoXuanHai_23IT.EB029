# Ứng dụng Screen Sharing P2P với WebRTC (Java + JS + HTML)

Ứng dụng này là ví dụ về chia sẻ màn hình (screen sharing) theo mô hình **peer-to-peer** sử dụng **WebRTC**.  
Backend dùng **Java (Maven)** để chạy **Signaling Server**, frontend dùng **JavaScript + HTML** hiển thị giao diện và thiết lập WebRTC.
---
## 🚀 Tính năng chính
- Chia sẻ màn hình trực tiếp giữa hai trình duyệt.
- Kết nối **P2P** bằng WebRTC.
- Signaling Server đơn giản bằng Java (WebSocket).
- Tương thích với hầu hết trình duyệt hỗ trợ WebRTC (Chrome, Edge, Firefox…).
---
## 🏗 Kiến trúc hệ thống

- **Signaling Server (Java + Maven)**  
  Trung gian truyền các message signaling (offer, answer, ICE) giữa các peer.

- **Frontend (HTML + JavaScript)**  
  Tạo kết nối WebRTC, chia sẻ màn hình bằng `getDisplayMedia`, render video nhận từ peer.

Toàn bộ kết nối media được truyền trực tiếp giữa hai client (P2P), không đi qua server.
---
## 📦 Yêu cầu hệ thống
- Java 11+
- Maven 3+
- Node.js 16+
- npm 8+
- Trình duyệt hỗ trợ WebRTC
---
## 🛠 Cài đặt & Chạy ứng dụng(khuyến khích sử dụng IntelliJ)
### **Bước 1 — Khởi chạy Signaling Server**
Trong terminal của IDE:
mvn exec:java "-Dexec.mainClass=org.example.SignalingServer"
### **Bước 2 — Khởi chạy ứng dụng**
npm run app
