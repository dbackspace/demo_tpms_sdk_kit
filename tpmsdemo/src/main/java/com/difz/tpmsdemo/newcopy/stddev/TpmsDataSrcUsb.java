package com.difz.tpmsdemo.newcopy.stddev;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;

import com.difz.tpmsdemo.newcopy.TpmsApplication;
import com.difz.tpmsdemo.newcopy.decode.PackBufferFrame;
import com.difz.tpmsdemo.newcopy.modle.DeviceOpenEvent;
import com.difz.tpmsdemo.newcopy.utils.Log;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class TpmsDataSrcUsb extends TpmsDataSrc {
    private static final String ACTION_USB_PERMISSION = "com.android.cz.USB_PERMISSION";
    PendingIntent mPermissionIntent;
    UsbSerialPort mPort;
    SerialInputOutputManager mSerialIoManager;
    private String TAG = "TpmsDataSrcUsb";
    private UsbManager mUsbManager = null;
    private List<UsbSerialPort> mEntries = null;
    boolean mIsStart = false;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() { // from class: com.std.dev.TpmsDataSrcUsb.2
        @Override // com.hoho.android.usbserial.util.SerialInputOutputManager.Listener
        public void onRunError(Exception e) {
            Log.d(TpmsDataSrcUsb.this.TAG, "Runner stopped. 读取报错，可能是断开了");
            TpmsDataSrcUsb.this.mMainHander.post(new Runnable() { // from class: com.std.dev.TpmsDataSrcUsb.2.1
                @Override // java.lang.Runnable
                public void run() {
                    EventBus.getDefault().post(new DeviceOpenEvent(false));
                }
            });
        }

        @Override // com.hoho.android.usbserial.util.SerialInputOutputManager.Listener
        public void onNewData(byte[] data) {
            String str = TpmsDataSrcUsb.this.TAG;
            Log.e(str, "usb read onNewData" + HexDump.dumpHexString(data));
            byte[] recBytes = new byte[data.length];
            System.arraycopy(data, 0, recBytes, 0, data.length);
            TpmsDataSrcUsb.this.BufferFrame.addBuffer(recBytes, recBytes.length);
        }
    };
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() { // from class: com.std.dev.TpmsDataSrcUsb.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TpmsDataSrcUsb.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                    if (intent.getBooleanExtra("permission", false)) {
                        if (device != null) {
                            Log.i("UsbR", "permission granted for device " + device);
                            Log.i("UsbR", "getInterfaceCount:" + device.getInterfaceCount());
                            TpmsDataSrcUsb.this.onStartUsbConnent();
                        } else {
                            Log.i("usb", "no permission " + device);
                        }
                    } else {
                        Log.i("usb", "permission denied for device " + device);
                    }
                }
            }
        }
    };
    Handler mMainHander = new Handler();

    public TpmsDataSrcUsb(TpmsApplication app) {
        super(app);
    }

    @Override // com.std.dev.TpmsDataSrc
    public void init() {
        this.mUsbManager = (UsbManager) this.theapp.getSystemService("usb");
        this.mEntries = new ArrayList();
        this.mPermissionIntent = PendingIntent.getBroadcast(this.theapp, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter1 = new IntentFilter(ACTION_USB_PERMISSION);
        this.theapp.registerReceiver(this.mUsbReceiver, filter1);
        EventBus.getDefault().register(this);
    }

    @Override // com.std.dev.TpmsDataSrc
    public void setBufferFrame(PackBufferFrame frame) {
        this.BufferFrame = frame;
    }

    @Override // com.std.dev.TpmsDataSrc
    public void writeData(byte[] databuf) {
        SerialInputOutputManager serialInputOutputManager = this.mSerialIoManager;
        if (serialInputOutputManager != null) {
            try {
                serialInputOutputManager.writeAsync(databuf);
            } catch (Exception e) {
            }
            String str = this.TAG;
            Log.i(str, " usb writeAsync " + HexDump.dumpHexString(databuf));
            return;
        }
        Log.e(this.TAG, " usb writeAsync mSerialIoManager =null ");
    }

    @Override // com.std.dev.TpmsDataSrc
    public void start() {
        String str = this.TAG;
        Log.i(str, "start mIsStart:" + this.mIsStart);
        if (this.mIsStart) {
            return;
        }
        startReadThread();
        this.mIsStart = true;
    }

    @Override // com.std.dev.TpmsDataSrc
    public void stop() {
        String str = this.TAG;
        Log.i(str, "stop mIsStart:" + this.mIsStart);
        if (!this.mIsStart) {
            return;
        }
        stopIoManager();
        UsbSerialPort usbSerialPort = this.mPort;
        if (usbSerialPort != null) {
            try {
                usbSerialPort.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mPort = null;
        }
        this.mIsStart = false;
    }

    private void startReadThread() {
        if (this.mPort == null) {
            onStartUsbConnent();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.std.dev.TpmsDataSrcUsb$1] */
    public void onStartUsbConnent() {
        new AsyncTask<Void, Void, List<UsbSerialPort>>() { // from class: com.std.dev.TpmsDataSrcUsb.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<UsbSerialPort> doInBackground(Void... params) {
                Log.d(TpmsDataSrcUsb.this.TAG, "Refreshing device list 刷新设备列表 ...");
                TpmsDataSrcUsb.this.sleep(1000L);
                List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(TpmsDataSrcUsb.this.mUsbManager);
                List<UsbSerialPort> result = new ArrayList<>();
                for (UsbSerialDriver driver : drivers) {
                    List<UsbSerialPort> ports = driver.getPorts();
                    String str = TpmsDataSrcUsb.this.TAG;
                    Object[] objArr = new Object[3];
                    objArr[0] = driver;
                    objArr[1] = Integer.valueOf(ports.size());
                    objArr[2] = ports.size() == 1 ? "" : "s";
                    Log.d(str, String.format("+ %s: %s port%s", objArr));
                    result.addAll(ports);
                }
                return result;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<UsbSerialPort> result) {
                TpmsDataSrcUsb.this.mEntries.clear();
                TpmsDataSrcUsb.this.mEntries.addAll(result);
                String str = TpmsDataSrcUsb.this.TAG;
                Log.d(str, "Done refreshing, " + TpmsDataSrcUsb.this.mEntries.size() + " entries found.");
                if (TpmsDataSrcUsb.this.mEntries.size() == 0) {
                    EventBus.getDefault().post(new DeviceOpenEvent(false));
                }
                for (int i = 0; i < TpmsDataSrcUsb.this.mEntries.size(); i++) {
                    UsbSerialPort port = (UsbSerialPort) TpmsDataSrcUsb.this.mEntries.get(i);
                    if (port != null) {
                        if ("1027_24577".equals(port.getDriver().getDevice().getVendorId() + "_" + port.getDriver().getDevice().getProductId())) {
                            TpmsDataSrcUsb.this.openUsbPort(port);
                            Log.i(TpmsDataSrcUsb.this.TAG, "onPostExecute 1027_24577");
                        } else {
                            if ("1027_24597".equals(port.getDriver().getDevice().getVendorId() + "_" + port.getDriver().getDevice().getProductId())) {
                                TpmsDataSrcUsb.this.openUsbPort(port);
                                Log.i(TpmsDataSrcUsb.this.TAG, "onPostExecute 1027_24597");
                            } else {
                                if ("6790_29987".equals(port.getDriver().getDevice().getVendorId() + "_" + port.getDriver().getDevice().getProductId())) {
                                    TpmsDataSrcUsb.this.openUsbPort(port);
                                    Log.i(TpmsDataSrcUsb.this.TAG, "onPostExecute 6790_29987");
                                }
                            }
                        }
                    }
                }
            }
        }.execute((Runnable) null);
    }

    private void startIoManager() {
        if (this.mPort != null) {
            Log.i(this.TAG, "Starting io manager ..");
            this.mSerialIoManager = new SerialInputOutputManager(this.mPort, this.mListener);
            this.mExecutor.submit(this.mSerialIoManager);
        }
    }

    private void stopIoManager() {
        if (this.mSerialIoManager != null) {
            Log.i(this.TAG, "Stopping io manager ..");
            this.mSerialIoManager.stop();
            this.mSerialIoManager = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openUsbPort(UsbSerialPort Port) {
        UsbInterface mInterface = null;
        this.mPort = Port;
        if (this.mPort == null) {
            EventBus.getDefault().post(new DeviceOpenEvent(false));
            return;
        }
        String str = this.TAG;
        Log.i(str, "interfacecount:" + this.mPort.getDriver().getDevice().getInterfaceCount());
        if (0 < this.mPort.getDriver().getDevice().getInterfaceCount()) {
            UsbInterface usbInterface = this.mPort.getDriver().getDevice().getInterface(0);
            mInterface = usbInterface;
        }
        if (mInterface == null) {
            Log.e(this.TAG, "USB device NO  Interface");
            EventBus.getDefault().post(new DeviceOpenEvent(false));
            return;
        }
        UsbDevice dev = this.mPort.getDriver().getDevice();
        sysSetPerMission(dev);
        if (this.mUsbManager.hasPermission(dev)) {
            UsbDeviceConnection connection = this.mUsbManager.openDevice(this.mPort.getDriver().getDevice());
            if (connection == null) {
                String str2 = this.TAG;
                Log.e(str2, "Error openDevice:  connection " + connection);
                EventBus.getDefault().post(new DeviceOpenEvent(false));
                return;
            }
            try {
                this.mPort.open(connection);
                try {
                    this.mPort.setParameters(19200, 8, 1, 0);
                    EventBus.getDefault().post(new DeviceOpenEvent(true));
                    String str3 = this.TAG;
                    Log.i(str3, "port name:" + this.mPort.getClass().getSimpleName());
                    onDeviceStateChange();
                    return;
                } catch (Exception e) {
                    String str4 = this.TAG;
                    Log.e(str4, "Error setting up device: " + e.getMessage());
                    EventBus.getDefault().post(new DeviceOpenEvent(false));
                    return;
                }
            } catch (Exception e2) {
                String str5 = this.TAG;
                Log.e(str5, " usb open device: " + e2.getMessage());
                EventBus.getDefault().post(new DeviceOpenEvent(false));
                return;
            }
        }
        Log.e(this.TAG, "permission denied for device else");
        this.mUsbManager.requestPermission(this.mPort.getDriver().getDevice(), this.mPermissionIntent);
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sleep(long ms) {
        try {
            SystemClock.sleep(1000L);
        } catch (Exception e) {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DeviceOpenEvent open) {
        if (!open.mOpen) {
            this.theapp.stopTpms();
        }
    }

    private boolean sysSetPerMission(UsbDevice dev) {
        sleep(500L);
        return true;
    }

    @Override // com.std.dev.TpmsDataSrc
    public String getDevName() {
        try {
            return this.mPort.getDriver().getDevice().getDeviceName();
        } catch (Exception e) {
            return "";
        }
    }
}