1️⃣ REGISTER ACCOUNT (Đăng ký tài khoản)

Register

→ Check spam đăng ký (Redis counter, TTL 10 phút)
→ Validate email chưa tồn tại trong DB
→ Generate OTP (6 số, random)
→ Lưu PendingRegister (RegisterRequest đã mã hoá password) vào Redis
    • Key: REGISTER:{email}
    • TTL: 5 phút
→ Lưu OTP vào Redis
    • Key: OTP:{email}
    • TTL: 5 phút
→ Gửi email chứa OTP (RabbitMQ → Mail Service)

2️⃣ VERIFY OTP (Xác thực email)

Verify OTP

→ Lấy OTP từ Redis (OTP:{email})
→ So sánh với OTP người dùng nhập
→ Nếu OTP sai hoặc hết hạn → reject
→ Lấy PendingRegister từ Redis (REGISTER:{email})
→ Tạo User entity
→ Lưu User vào DB (status = ACTIVE)
→ Xoá Redis keys:
    • OTP:{email}
    • REGISTER:{email}

3️⃣ FORGOT PASSWORD (Quên mật khẩu)

Forgot Password

→ Validate email tồn tại trong DB
→ Generate OTP
→ Lưu OTP reset vào Redis
    • Key: RESET:{email}
    • TTL: 5 phút
→ Gửi email OTP reset mật khẩu

4️⃣ RESET PASSWORD (Đặt lại mật khẩu)

Reset Password

→ Lấy OTP reset từ Redis (RESET:{email})
→ Validate OTP
→ Encode mật khẩu mới
→ Update password trong DB
→ Xoá Redis key RESET:{email}

5️⃣ CHỐNG SPAM ĐĂNG KÝ (Rate limit)

Register Spam Protection

→ Redis INCR REGISTER_COUNT:{email}
→ Nếu lần đầu → set TTL 10 phút
→ Nếu số lần > 5 → reject request
→ Redis tự xoá key khi hết TTL


BỔ SUNG LOG – TRUY VẾT HỆ THỐNG
🔹 A. Duyệt bài

Ai duyệt

Duyệt lúc nào

Kết quả

Lý do (AI flag / admin note)

🔹 B. Thay đổi trạng thái sản phẩm

AVAILABLE → RESERVED

RESERVED → SOLD

SOLD → CANCELLED

🔹 C. Xác nhận người mua

Seller chọn buyer nào

Thời điểm xác nhận

CƠ CHẾ CHỐNG SPAM & LẠM DỤNG

🎯 Nghiệp vụ

Mỗi user chỉ được đăng N bài/ngày (vd: 5)

Nếu vượt → chặn

🛠 Cách làm (ĐÚNG THỰC TẾ)

👉 Redis Counter

KEY: post_count:{userId}:{yyyyMMdd}
VALUE: số bài đã đăng
TTL: 24h

Flow

User đăng bài

Check Redis

Nếu > limit → throw AppException

if (postCount >= MAX_POST_PER_DAY) {
    throw new AppException(ErrorCode.POST_LIMIT_EXCEEDED);
}


📌 Không query DB → rất nhanh

2️⃣ Giới hạn số tin nhắn / inbox
🎯 Vấn đề

Spam inbox seller

Bot gửi hàng loạt

🛠 Giải pháp
A. Giới hạn tin nhắn / phút
KEY: msg_rate:{userId}
TTL: 1 phút

B. Giới hạn inbox / sản phẩm

Mỗi user chỉ được inbox 1 conversation / product

👉 Đã có Conversation(product_id, buyer_id) → UNIQUE

3️⃣ Chặn user rating thấp / bị report nhiều
🔹 A. Rating thấp
Nếu rating < 2.5 → hạn chế đăng bài / inbox

🔹 B. Report nhiều

➡ Cần thêm entity UserReport

UserReport(
    id,
    reported_user_id,
    reporter_id,
    reason,
    created_at
)


📌 Rule:

N report trong 7 ngày → auto block

Admin review sau

🧩 Đăng bài
User → Check status
     → Check limit Redis
     → Save Product (PENDING)
     → Log CREATE_PRODUCT

🧩 Admin duyệt
Admin → AI check
      → Approve / Reject
      → Update Product
      → Log APPROVE_PRODUCT

🧩 Inbox
Buyer → Check rate limit
      → Check rating
      → Create conversation
      → Send message
      → Log SEND_MESSAGE (optional)

🧩 Chọn người mua
Seller → Lock product
       → Confirm buyer
       → Update product SOLD
       → Create transaction
       → Log CONFIRM_BUYER

