package com.epishie.btradar.bluetooth;

import android.bluetooth.BluetoothAdapter;

import com.epishie.btradar.Beacon;

public abstract class BeaconScanner {

    protected static final int SCAN_TIME = 5100;

    protected final BluetoothAdapter mAdapter;
    protected final BeaconScannerListener mListener;
    protected boolean mScanning;

    public BeaconScanner(BluetoothAdapter adapter, BeaconScannerListener listener) {
        mAdapter = adapter;
        mListener = listener;
    }

    public abstract void startScan();
    public abstract void stopScan();

    protected Beacon getBeacon(byte[] scanRecord, int rssi) {
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            Beacon.Builder builder = new Beacon.Builder();

            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //Here is your UUID
            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);
            builder.setUuid(uuid);

            //Here is your Major value
            int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
            builder.setMajor(major);

            //Here is your Minor value
            int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
            builder.setMinor(minor);

            int txPower = (int) scanRecord[29];
            builder.setTxPower(txPower);

            builder.setRssi(rssi);
            /*
            double distance = calculateDistance(txPower, rssi);
            String location;
            if (distance == -1.0) {
                location = "Unknown";
            } else if (distance < 1) {
                location = "Immediate";
            } else if (distance < 3) {
                location = "Near";
            } else {
                location = "Far";
            }
            */
            return builder.create();
        } else {
            return null;
        }
    }

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public interface BeaconScannerListener {
        void onScanUpdate(Beacon beacon);
    }
}
