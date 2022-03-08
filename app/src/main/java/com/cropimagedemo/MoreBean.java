package com.cropimagedemo;

import android.os.Parcel;
import android.os.Parcelable;

public class MoreBean implements Parcelable {

    private int id;
    private String name;
    private int image;

    public MoreBean(int id, String name, int image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    protected MoreBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readInt();
    }

    public static final Creator<MoreBean> CREATOR = new Creator<MoreBean>() {
        @Override
        public MoreBean createFromParcel(Parcel in) {
            return new MoreBean(in);
        }

        @Override
        public MoreBean[] newArray(int size) {
            return new MoreBean[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(image);
    }
}
