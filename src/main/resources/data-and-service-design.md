# THIẾT KẾ DỮ LIỆU VÀ KẾT NỐI DỊCH VỤ (MONOLITHIC ARCHITECTURE)

## 1. Cấu trúc Cơ sở Dữ liệu (MySQL Schema)
Hệ thống quản lý rạp chiếu phim (`movie-sv`) sử dụng mô hình cơ sở dữ liệu quan hệ gồm các bảng chính:
* **`users`**: Lưu trữ thông tin tài khoản người dùng (`username`, `password_hash`, `role` [CUSTOMER/ADMIN], `active`).
* **`movies`**: Lưu trữ thông tin phim (`title`, `genre`, `releaseYear`, `status`).
* **`cinemas`**: Lưu thông tin các rạp chiếu.
* **`rooms`**: Lưu thông tin phòng chiếu thuộc về rạp (`cinema_id`), sức chứa (`capacity`).
* **`seats`**: Lưu thông tin ghế ngồi trong phòng (`room_id`), phân loại ghế và số ghế.
* **`showtimes`**: Lưu lịch chiếu phim (`movie_id`, `room_id`, `startTime`, `basePrice`).
* **`tickets`**: Lưu thông tin đặt vé (`user_id`, `showtime_id`, `seat_id`, `status` [PENDING/PAID/CANCELLED]).

---

## 2. Thiết kế Tầng Xử lý & Dịch vụ (Service & Controller Layers)
* **`Auth Service` & `AuthController`**: 
  * `POST /api/v1/auth/register`: Đăng ký tài khoản mới.
  * `POST /api/v1/auth/login`: Xác thực thông tin và cấp phát chuỗi **JWT Token** chứa định danh và quyền hạn (Roles).
* **`Movie Service` & `MovieController`**: 
  * Quản lý thông tin phim (Thêm, sửa, xóa, xem danh sách phim).
* **`Ticket Service` & `TicketController` (Lõi nghiệp vụ nâng cao)**: 
  * `POST /api/v1/tickets`: Đặt vé mới thông qua `TicketRequest` DTO. Hệ thống tự động trích xuất `userId` từ JWT Token của người đang đăng nhập, loại bỏ hoàn toàn việc truyền thủ công từ client.
  * `GET /api/v1/tickets/my-tickets`: Lấy danh sách vé cá nhân bảo mật dựa trên Token.
  * `GET /api/v1/tickets`: Xem toàn bộ vé trên hệ thống (Dành riêng cho `ADMIN`).
  * `GET /api/v1/users/{userId}/tickets`: Xem vé theo User ID (Có cơ chế kiểm tra quyền hạn 403 nếu cố tình xem vé người khác).
  * `PATCH /api/v1/tickets/{id}/status`: Cập nhật trạng thái thanh toán vé (`PENDING`/`PAID`).
  * `DELETE /api/v1/tickets/{id}`: Xóa/hủy vé và giải phóng ghế.

---

## 3. Cơ chế Bảo mật và Phân quyền (Security & RBAC)
* **Spring Security Filter & JWT Filter**: Tự động chặn mọi request chưa được xác thực, giải mã token trên mỗi request để thiết lập Authentication trong SecurityContext.
* **Quyền Customer**: 
  * Được phép xem phim, đặt vé cá nhân, tra cứu lịch sử vé của chính mình, cập nhật hoặc hủy vé của bản thân.
  * Bị từ chối truy cập (Lỗi `403 Forbidden`) khi cố tình truy cập vào các API quản trị hoặc xem dữ liệu của user khác.
* **Quyền Admin**: 
  * Sở hữu toàn quyền kiểm soát hệ thống, được phép xem toàn bộ danh sách vé của mọi khách hàng (`GET /api/v1/tickets`) và quản lý cấu hình dữ liệu nền tảng.

---

## 4. Xử lý Lỗi Tập trung (Global Exception Handler)
* Hệ thống cấu hình `@RestControllerAdvice` để bắt toàn bộ các ngoại lệ (`RuntimeException`, `BadCredentialsException`, v.v.) và trả về mã trạng thái HTTP chuẩn (201 Created, 200 OK, 400 Bad Request, 403 Forbidden, 404 Not Found) kèm thông báo lỗi rõ ràng dạng chuỗi hoặc JSON.