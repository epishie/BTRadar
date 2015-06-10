package com.epishie.btradar.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import com.epishie.btradar.model.Beacon;

public abstract class BeaconScanner {

    protected static final int SCAN_TIME = 5000;

    protected final BluetoothAdapter mAdapter;
    protected final BeaconScannerListener mListener;
    protected boolean mScanning;

    /**
     * Creates scanner based on SDK version
     * @param adapter The BluetoothAdapter instance.
     * @param listener The callback for beacon events.
     * @return SDK-specific Bluetooth LE Scanner.
     */
    public static BeaconScanner createScanner(BluetoothAdapter adapter, BeaconScannerListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new BeaconLollipopScanner(adapter, listener);
        } else {
            return new BeaconJellyBeanScanner(adapter, listener);
        }
    };

    protected BeaconScanner(BluetoothAdapter adapter, BeaconScannerListener listener) {
        mAdapter = adapter;
        mListener = listener;
    }

    /**
     * Starts the scanner.
     */
    public abstract void startScan();

    /**
     * Stops the scanner.
     */
    public abstract void stopScan();

    /**
     * Get Beacon from raw data.
     * @param scanRecord The raw beacon data.
     * @param rssi The power of the radio signal.
     * @return
     */
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

            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);
            builder.setUuid(uuid);

            int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
            builder.setMajor(major);

            int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
            builder.setMinor(minor);

            int txPower = (int) scanRecord[29];
            builder.setTxPower(txPower);

            builder.setRssi(rssi);
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
