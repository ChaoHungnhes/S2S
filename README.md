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
