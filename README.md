#  Pop It Game - Đồ án Java Swing (Multiplayer LAN + Bot Offline)

Chào thầy và mọi người! Đây là sản phẩm game **Pop It** được mình tự tay lên ý tưởng thiết kế giao diện và lập trình hoàn toàn bằng ngôn ngữ Java (sử dụng thư viện đồ họa Swing). 

Không chỉ dừng lại ở một tựa game click chuột cơ bản, dự án này được mình đầu tư khá kỹ về mặt trải nghiệm người dùng, thuật toán xử lý của Bot và khả năng tương tác kết nối giữa hai người chơi từ xa.

---

##  Các tính năng tâm đắc trong dự án

* **Giao diện hiện đại, mướt mắt:** Mình không dùng các nút bấm mặc định thô cứng của Windows. Toàn bộ các nút bấm, bàn cờ Pop It đều được tùy biến (Custom Component) để bo góc mượt mà, đổ bóng 3D, hiệu ứng gradient cầu vồng và đổi màu động khi di chuột qua.
* **Chơi với Máy (Chế độ Offline):** Tích hợp một chú Bot thông minh. Bot biết quét toàn bộ bàn cờ, tự phân tích các đoạn bóng còn sót lại trên cùng một hàng để đưa ra nước đi tối ưu nhất nhằm dồn người chơi vào thế bí.
* **Đấu đôi qua mạng (Chế độ Online LAN):** Ứng dụng kiến trúc mạng **Client - Server** thông qua kết nối **Socket** ở cổng `12345`. Chỉ cần hai máy tính kết nối chung một mạng Wi-Fi hoặc mạng LAN là có thể tìm thấy nhau, đồng bộ bàn cờ theo thời gian thực để phân tài cao thấp.

---

##  Cấu trúc thư mục chuẩn khi chạy bài
Để chương trình không bị lỗi package (gói nhận diện lớp), cấu trúc các file trong thư mục dự án của bạn khi tải về máy phải trông như thế này:
text
📂 Thu-muc-goc-cua-ban/
 └── 📂 popitgame/              <-- Bắt buộc phải nằm trong thư mục tên là popitgame
      ├── PlayOffline.java
      ├── PlayOnline.java
      ├── PopItBoard.java
      ├── PopItMenu.java
      ├── PopItTheme.java
      └── ServerManager.java
