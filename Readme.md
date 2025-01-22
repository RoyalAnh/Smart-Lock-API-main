
**xây dựng một API backend để quản lý hệ thống Smart Lock (khóa thông minh)**
*Xây dựng hệ thống quản lý authentication (xác thực) và lock (khóa) trong một hệ thống Smart Lock. Dự án cung cấp các dịch vụ backend để xử lý việc quản lý người dùng, khóa thông minh và các tác vụ liên quan*

# Tính năng chính:
-   Authentication: Xác thực người dùng, tạo token (có thể dùng JWT hoặc các cơ chế mã hóa khác).
-   Lock Management: Quản lý các trạng thái của khóa thông minh, quyền truy cập, mở khóa và đóng khóa.
-   Utility Classes: Các tiện ích như mã hóa (AES), tạo UUID để bảo mật và định danh

# Các hàm main
1.  **dao**      Data Access Object: thao tác với Database:     
                    **Xác thực người dùng + DL khóa**
2.  **entity** : chứa các lớp entity đại diện cho các bảng trong Database   
                    **Entity cho xác thực (thông tin người dùng, token) + Entity cho các khóa thông minh (trạng thái khóa, người dùng nào sở hữu)**
3.  **rest**  :  Chứa các RESTful API Controllers để xử lý các request từ client 
                    **API xử lý đăng nhập, đăng ký, xác thực người dùng + API xử lý trạng thái và quyền mở/đóng khóa**
4.  **service**: Chứa các lớp service dùng để xử lý logic nghiệp vụ
                    **Logic xử lý xác thực, mã hóa và token + Logic xử lý quản lý khóa thông minh**
5.  **utils**  : Các lớp tiện ích hỗ trợ
                    **AESUtil.java**: Mã hóa dữ liệu sử dụng AES để bảo mật.
                    **UUIDUtil.java**: Tạo các UUID để định danh duy nhất.

6.  **resources**: Chứa file **application.properties**: Cấu hình cho project (kết nối database, port, các thông số khác).

7.  **test**:   Testcase

# Công nghệ và công cụ sử dụng
        Spring Boot: Framework chính cho backend API.
        Maven: Công cụ quản lý dependencies (file pom.xml).
        AES: Sử dụng mã hóa AES để bảo mật dữ liệu.
        UUID: Để tạo định danh duy nhất.
        Junit: Dùng cho testing.

# Kết luận
Dự án Smart-Lock-API-main này tập trung vào việc xây dựng backend API để phục vụ cho hệ thống khóa thông minh (Smart Lock), bao gồm:
-   Quản lý xác thực người dùng.
-   Quản lý các khóa thông minh và quyền truy cập.
-   Bảo mật dữ liệu với AES encryption.
=>  Dự án rất phù hợp để làm phần backend cho các ứng dụng IoT hoặc hệ thống nhà thông minh sử dụng Smart Lock.