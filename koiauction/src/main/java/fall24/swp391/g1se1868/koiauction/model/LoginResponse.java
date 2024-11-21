package fall24.swp391.g1se1868.koiauction.model;


    public class LoginResponse {
        private String token;
        private String username;
        private String fullname;
        private String Role;
        private Integer userId;
        private int expire;
        private String address;

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public LoginResponse(String token, String username, String fullname, String role, Integer userId, String s, int expire,String address) {
            this.token = token;
            this.userId=userId;
            this.username = username;
            this.fullname = fullname;
            this.Role = role;
            this.expire = expire;
            this.address=address;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getRole() {
            return Role;
        }

        public void setRole(String role) {
            Role = role;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
        public Integer getUserId() {
            return userId;
        }
        public void setUserId(Integer userId) {
            this.userId = userId;
        }



    }


