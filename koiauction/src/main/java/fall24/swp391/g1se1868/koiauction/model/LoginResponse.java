package fall24.swp391.g1se1868.koiauction.model;


    public class LoginResponse {
        private String token;
        private String username;
        private String fullName;

        public LoginResponse(String token, String username, String fullName) {
            this.token = token;
            this.username = username;
            this.fullName = fullName;
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

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }


