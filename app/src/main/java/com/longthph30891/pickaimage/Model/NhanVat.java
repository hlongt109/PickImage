package com.longthph30891.pickaimage.Model;

import android.util.Log;

import java.util.HashMap;

public class NhanVat {
    private String maNv;
    private String tenNv;
    private String img;

    public NhanVat() {
    }

    public NhanVat(String maNv, String tenNv, String img) {
        this.maNv = maNv;
        this.tenNv = tenNv;
        this.img = img;
    }

    public String getMaNv() {
        return maNv;
    }

    public NhanVat setMaNv(String maNv) {
        this.maNv = maNv;
        return this;
    }

    public String getTenNv() {
        return tenNv;
    }

    public NhanVat setTenNv(String tenNv) {
        this.tenNv = tenNv;
        return this;
    }

    public String getImg() {
        return img;
    }

    public NhanVat setImg(String img) {
        this.img = img;
        return this;
    }
    public HashMap<String, Object> convertHashMap(){
        HashMap<String, Object> nhanvat = new HashMap<>();
        nhanvat.put("maNv",maNv);
        nhanvat.put("tenNv",tenNv);
        nhanvat.put("img", img);
        return nhanvat;
    }
}
