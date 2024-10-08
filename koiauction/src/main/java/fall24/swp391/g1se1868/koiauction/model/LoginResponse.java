package fall24.swp391.g1se1868.koiauction.model;


    public class LoginResponse {
        private String token;
        private String username;
        private String fullname;
        private String Role;

        public LoginResponse(String token, String username,String fullname, String role) {
            this.token = token;
            this.username = username;
            this.fullname = fullname;
            Role = role;
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



    }


