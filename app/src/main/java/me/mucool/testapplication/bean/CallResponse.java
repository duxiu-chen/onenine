package me.mucool.testapplication.bean;

import java.util.List;

public class CallResponse {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String createDate;
        private String updateDate;
        private String openid;
        private MahjongHallRoomBean mahjongHallRoom;
        private String mahjongHallRoomQrcode;
        private String voicePath;
        private Integer state;
        private Integer duration;
        private String hallName;
        private String hallRoomName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public MahjongHallRoomBean getMahjongHallRoom() {
            return mahjongHallRoom;
        }

        public void setMahjongHallRoom(MahjongHallRoomBean mahjongHallRoom) {
            this.mahjongHallRoom = mahjongHallRoom;
        }

        public String getMahjongHallRoomQrcode() {
            return mahjongHallRoomQrcode;
        }

        public void setMahjongHallRoomQrcode(String mahjongHallRoomQrcode) {
            this.mahjongHallRoomQrcode = mahjongHallRoomQrcode;
        }

        public String getVoicePath() {
            return voicePath;
        }

        public void setVoicePath(String voicePath) {
            this.voicePath = voicePath;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public String getHallName() {
            return hallName;
        }

        public void setHallName(String hallName) {
            this.hallName = hallName;
        }

        public String getHallRoomName() {
            return hallRoomName;
        }

        public void setHallRoomName(String hallRoomName) {
            this.hallRoomName = hallRoomName;
        }

        public static class MahjongHallRoomBean {
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
