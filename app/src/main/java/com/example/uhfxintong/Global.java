package com.example.uhfxintong;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ericecheverri on 11/6/16.
 */

public class Global implements Parcelable {
    public  Bitmap img;

    public Global(Bitmap img) {
        this.img = img;
    }

    protected Global(Parcel in) {
        img = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(img);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Global> CREATOR = new Parcelable.Creator<Global>() {
        @Override
        public Global createFromParcel(Parcel in) {
            return new Global(in);
        }

        @Override
        public Global[] newArray(int size) {
            return new Global[size];
        }
    };
}