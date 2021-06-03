package me.mucool.testapplication.bean;


public class UserInfoResponse {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String remarks;
        private String createDate;
        private UpdateByBean updateBy;
        private String updateDate;
        private MahjongHallBean mahjongHall;
        private String name;
        private String mobile;
        private String avatar;
        private Integer receiveCall;
        private Integer workState;
        private String openid;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public UpdateByBean getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(UpdateByBean updateBy) {
            this.updateBy = updateBy;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public MahjongHallBean getMahjongHall() {
            return mahjongHall;
        }

        public void setMahjongHall(MahjongHallBean mahjongHall) {
            this.mahjongHall = mahjongHall;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Integer getReceiveCall() {
            return receiveCall;
        }

        public void setReceiveCall(Integer receiveCall) {
            this.receiveCall = receiveCall;
        }

        public Integer getWorkState() {
            return workState;
        }

        public void setWorkState(Integer workState) {
            this.workState = workState;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public static class UpdateByBean {
            private String id;
            private Integer money;
            private String loginFlag;
            private String roleNames;
            private Boolean admin;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public Integer getMoney() {
                return money;
            }

            public void setMoney(Integer money) {
                this.money = money;
            }

            public String getLoginFlag() {
                return loginFlag;
            }

            public void setLoginFlag(String loginFlag) {
                this.loginFlag = loginFlag;
            }

            public String getRoleNames() {
                return roleNames;
            }

            public void setRoleNames(String roleNames) {
                this.roleNames = roleNames;
            }

            public Boolean getAdmin() {
                return admin;
            }

            public void setAdmin(Boolean admin) {
                this.admin = admin;
            }
        }

        public static class MahjongHallBean {
            private String id;
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
