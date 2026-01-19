register acc:
Register
 → validate email chưa tồn tại DB
 → generate OTP
 → save RegisterRequest vào Redis (TTL 5 phút)
 → gửi mail

Verify OTP
 → check OTP
 → lấy RegisterRequest từ Redis
 → save User vào DB
 → xoá Redis
