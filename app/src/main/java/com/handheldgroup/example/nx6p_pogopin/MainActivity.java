package com.handheldgroup.example.nx6p_pogopin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.techinfo.devicemanager.IDeviceControlInterface;

import java.util.Arrays;

import javax.security.auth.login.LoginException;

/**ЭТО РАБОТАЕТ!!! только с новыми X6 (которые на 11 Android) и с новой головой
 *
 * Работает: NautizX6->Boi5->BTDU->BDKG-11M на запрос "1 11" блок отвечает (запрос и ответ выводится в LogCat)
 * */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "Demo";
    private static final int BYTE_ARRAY_MAX_SIZE = 4096;

    private IDeviceControlInterface mDeviceManagerService;
    private int mPogoSerialFd;

    private TextView textView;

    private Intent mDevicePogoIntent = new Intent("techinfo.intent.action.DEVICE_CONTROL")
            .setComponent(new ComponentName(
                    "com.techinfo.devicemanager",
                    "com.techinfo.devicemanager.service.DeviceControlService"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);

        Log.e(TAG, "onCreate: ");

        initService();
    }

    private final ServiceConnection mDeviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "Connect to device manager service succeed!");
            mDeviceManagerService = IDeviceControlInterface.Stub.asInterface(service);

            // Update the UI when the service is connected
            textView.post(() -> textView.setText("Connect to device manager service succeed!"));

            try {
                // Write a value to enable/disable power pins
                mDeviceManagerService.writeNode("/sys/class/ext_dev/function/ext_dev_3v3_enable",  "0");
                mDeviceManagerService.writeNode("/sys/class/ext_dev/function/ext_dev_5v_enable", "1");//вкл 5 в. не включает мгновенно, если сразу прочитать, выдаст старое значение

                // Read current state of a power pin
                String value = mDeviceManagerService.readNode("/sys/class/ext_dev/function/ext_dev_3v3_enable");
                String value2 = mDeviceManagerService.readNode("/sys/class/ext_dev/function/ext_dev_5v_enable");
                Log.e("tag", value+" "+value2);

                // Open the serial port with 230400 baud
                mPogoSerialFd = mDeviceManagerService.openSerial("/dev/ttyHSL1", 230400);

                // Write data to the serial port
                byte[] data = new byte[]{0x01, 0x11, 0x2c, (byte)0xc0};
                Log.e(TAG, ">> "+Arrays.toString(data));
                mDeviceManagerService.writeSerial(mPogoSerialFd, data, data.length);

                // Read data from the serial port (non blocking)
                byte[] buffer = new byte[BYTE_ARRAY_MAX_SIZE];
                int size = mDeviceManagerService.readSerial(mPogoSerialFd, buffer, BYTE_ARRAY_MAX_SIZE);

                byte[] answer = new byte[size];
                for (int i = 0; i < size; i++) {
                    answer[i] = buffer[i];
                }
                Log.e(TAG, "<< "+Arrays.toString(answer));

                // Close the serial port
//                mDeviceManagerService.closeSerial(mPogoSerialFd);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "Disconnect from device manager service succeed!");
            mDeviceManagerService = null;
        }
    };

    private void initService() {
        bindService(mDevicePogoIntent, mDeviceConnection, Context.BIND_AUTO_CREATE);
    }
}