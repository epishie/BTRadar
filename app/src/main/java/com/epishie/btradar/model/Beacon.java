package com.epishie.btradar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Beacon implements Parcelable {

    public static final double UNKNOWN_DISTANCE = -1.0;

    private final String mUuid;
    private final int mMajor;
    private final int mMinor;
    private final int mRssi;
    private final int mTxPower;
    private final double mDistance;
    private final Proximity mProximity;
    private final long mLastUpdateTime;

    private Beacon(String uuid, int major, int minor, int rssi, int txPower) {
        mUuid = uuid;
        mMajor = major;
        mMinor = minor;
        mRssi = rssi;
        mTxPower = txPower;

        mDistance = calculateProximity(rssi, txPower);
        if (mDistance == UNKNOWN_DISTANCE) {
            mProximity = Proximity.UNKNOWN;
        } else if (mDistance < 1) {
            mProximity = Proximity.IMMEDIATE;
        } else if (mDistance < 3) {
            mProximity = Proximity.NEAR;
        } else {
            mProximity = Proximity.FAR;
        }

        mLastUpdateTime = System.currentTimeMillis();
    }

    public Beacon(Parcel parcel) {
        mUuid = parcel.readString();
        mMajor = parcel.readInt();
        mMinor = parcel.readInt();
        mTxPower = parcel.readInt();
        mRssi = parcel.readInt();
        mDistance = parcel.readDouble();
        mProximity = Proximity.values()[parcel.readInt()];
        mLastUpdateTime = parcel.readLong();
    }

    public String getUuid() {
        return mUuid;
    }

    public int getMajor() {
        return mMajor;
    }

    public int getMinor() {
        return mMinor;
    }

    public int getRssi() {
        return mRssi;
    }

    public int getTxPower() {
        return mTxPower;
    }

    public double getDistance() {
        return mDistance;
    }

    public Proximity getProximity() {
        return mProximity;
    }

    public long getLastUpdateTime() {
        return mLastUpdateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Beacon)) {
            return false;
        }
        Beacon otherBeacon = (Beacon)o;
        return getUuid().equals(otherBeacon.getUuid());
    }

    private static double calculateProximity(int rssi, int txPower) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            return ((0.89976)*Math.pow(ratio,7.7095) + 0.111);
        }
    }

    @Override
    public int describeContents() {
        return mUuid.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUuid);
        dest.writeInt(mMajor);
        dest.writeInt(mMinor);
        dest.writeInt(mTxPower);
        dest.writeInt(mRssi);
        dest.writeDouble(mDistance);
        dest.writeInt(mProximity.ordinal());
        dest.writeLong(mLastUpdateTime);
    }

    public static final Parcelable.Creator<Beacon> CREATOR = new Creator<Beacon>() {
        @Override
        public Beacon createFromParcel(Parcel source) {
            return new Beacon(source);
        }

        @Override
        public Beacon[] newArray(int size) {
            return new Beacon[0];
        }
    };

    public enum Proximity {
        UNKNOWN,
        IMMEDIATE,
        NEAR,
        FAR
    }

    public static class Builder {
        private String mUuid;
        private int mMajor;
        private int mMinor;
        private int mRssi;
        private int mTxPower;

        public Builder() {}

        public Builder(Beacon beacon) {
            mUuid = beacon.getUuid();
            mMajor = beacon.getMajor();
            mMinor = beacon.getMinor();
        }

        public void setUuid(String uuid) {
            mUuid = uuid;
        }

        public void setMajor(int major) {
            mMajor = major;
        }

        public void setMinor(int minor) {
            mMinor = minor;
        }

        public void setRssi(int rssi) {
            mRssi = rssi;
        }

        public void setTxPower(int txPower) {
            mTxPower = txPower;
        }

        public Beacon create() {
            return new Beacon(mUuid, mMajor, mMinor, mRssi, mTxPower);
        }
    }
}
