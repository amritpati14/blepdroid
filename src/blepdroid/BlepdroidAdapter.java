package blepdroid;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

public class BlepdroidAdapter {
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothLeScanner mLEScanner;
	private ScanSettings settings;
    private List<ScanFilter> filters;
	
	PApplet parent;
	Blepdroid blepdroid;
	int sdkVersion;
	
	BlepdroidAdapter(PApplet _parent, Blepdroid _blepdroid)
	{
		sdkVersion = Build.VERSION.SDK_INT;
		
		parent = _parent;
		blepdroid = _blepdroid;
		
		final BluetoothManager bluetoothManager = (BluetoothManager) parent.getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		if(sdkVersion < 22)
		{
			
			// Checks if Bluetooth is supported on the device.
			if (mBluetoothAdapter == null) {
				Toast.makeText(parent.getActivity(),"BLE Not Supported", Toast.LENGTH_SHORT).show();
				PApplet.println(" BLE Not Supported ");
				return;
			}
		}
		else
		{
			mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            filters = new ArrayList<ScanFilter>();
            
		}
	}
	
	public void startScan(Handler handler)
	{
			handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);

                    }
                }
            }, 1000);
            
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
	}
	
	public void stopScan()
	{
		if (Build.VERSION.SDK_INT < 21) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        } else {
            mLEScanner.stopScan(mScanCallback);
        }
	}
	
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //BluetoothDevice btDevice = result.getDevice();
            //connectToDevice(btDevice);
        	if(result.getScanRecord() != null)
        	{
        		blepdroid.addDevice(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
        	}
        	else
        	{
        		byte empty[] = new byte[1];
        		blepdroid.addDevice(result.getDevice(), result.getRssi(), empty);
        	}
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
//                Log.i("ScanResult - Results", sr.toString());
            	blepdroid.addDevice(sr.getDevice(), sr.getRssi(), sr.getScanRecord().getBytes());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
//            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

	
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			PApplet.println(" mLeScanCallback returned ");
			parent.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					blepdroid.addDevice(device, rssi, scanRecord);
				}
			});
		}
	};
	
}
