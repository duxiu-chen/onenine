package me.mucool.testapplication.bean;


import java.util.HashSet;
import java.util.Set;

public class LoginResponse {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String token;
        private String hallIds;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getHallIds() {
            return hallIds;
        }

        public void setHallIds(String hallIds) {
            this.hallIds = hallIds;
        }
    }
}
