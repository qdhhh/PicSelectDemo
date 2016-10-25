package com.android.qdhhh.picselectdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qdhhh on 2016/10/25.
 */

public class FileBean implements Parcelable {
    /**
     * 文件夹下第一张图片路径
     */
    public String path;
    /**
     * 缩略图
     */
    public String thumbnailsPath;
    /**
     * 总图片数
     */
    public int pisNum = 0;
    /**
     * 文件夹名
     */
    public String fileName;

    /**
     * 当图片选择后，索引值
     */
    public int selectPosition;

    public int _id;

    /**
     * 当前图片在列表中顺序
     */
    public int position;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.thumbnailsPath);
        dest.writeInt(this.pisNum);
        dest.writeString(this.fileName);
        dest.writeInt(this.selectPosition);
        dest.writeInt(this._id);
        dest.writeInt(this.position);
    }

    public FileBean() {
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "path='" + path + '\'' +
                ", thumbnailsPath='" + thumbnailsPath + '\'' +
                ", pisNum=" + pisNum +
                ", fileName='" + fileName + '\'' +
                ", selectPosition=" + selectPosition +
                ", _id=" + _id +
                ", position=" + position +
                '}';
    }

    protected FileBean(Parcel in) {
        this.path = in.readString();
        this.thumbnailsPath = in.readString();
        this.pisNum = in.readInt();
        this.fileName = in.readString();
        this.selectPosition = in.readInt();
        this._id = in.readInt();
        this.position = in.readInt();
    }

    public static final Creator<FileBean> CREATOR = new Creator<FileBean>() {
        @Override
        public FileBean createFromParcel(Parcel source) {
            return new FileBean(source);
        }

        @Override
        public FileBean[] newArray(int size) {
            return new FileBean[size];
        }
    };
}
