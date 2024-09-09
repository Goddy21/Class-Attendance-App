package com.example.attendancesystem;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("23496bb0-b47d-11ee-99dd-678a21ab4fec");
    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThreat;
    private BluetoothDevice mDevice;

    private ProgressDialog mProgressDialog;

    private UUID deviceUUID;

    private ConnectedThread mConnectedThread;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mServerSocket;
        @SuppressLint("MissingPermission")
        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptTread: setting up server using: "+ MY_UUID_INSECURE);
            }catch (IOException e){
            }
            mServerSocket = tmp;
        }
        public void run(){
            Log.d(TAG, "run: AcceptThread Running.");
            BluetoothSocket socket = null;
            try{
                Log.d(TAG, "run: RFCOM server socket start....");
                socket = mServerSocket.accept();
                Log.d(TAG, "run: RFCOM server socket accepted connection.");
            }catch (IOException e){
                Log.e(TAG, "AcceptThread. IOException: "+ e.getMessage());
            }
            if (socket != null){
                connected(socket, mDevice);
            }
            Log.d(TAG, "END mAcceptThread");
        }
        public void cancel(){
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try{
                mServerSocket.close();
            }catch (IOException e){
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. "+e.getMessage());
            }
        }
    }
    private class ConnectThread extends Thread{
        private BluetoothSocket msocket;
        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread: Started.");
            mDevice = device;
            deviceUUID = uuid;
        }
        @SuppressLint("MissingPermission")
        public void run(){
            BluetoothSocket tmp = null;
            Log.d(TAG, "RUN mConnectThread");
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRFconnSocket using UUID: "+ MY_UUID_INSECURE);
                tmp = mDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.d(TAG, "ConnectThread: could not creat InsecureRFconnSocket "+e.getMessage());
            }
            msocket = tmp;
            mBluetoothAdapter.cancelDiscovery();
            try {
                msocket.connect();
                Log.d(TAG, "run: ConnectThread Connected.");
            } catch (IOException e) {
                try {
                    msocket.close();
                } catch (IOException ex) {
                    Log.d(TAG, "mConnectThread: run: Unable to close connection in socket "+ e.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: could not connect to UUID: "+ MY_UUID_INSECURE);
            }
                connected(msocket, mDevice);
        }
        public void cancel(){
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                msocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mSocket ConnectThread failed. "+ e.getMessage());
            }
        }
    }

    public synchronized void start(){
        Log.d(TAG, "start");

        if (mConnectThreat != null){
            mConnectThreat.cancel();
            mConnectThreat = null;
        }
        if (mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }
    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Started.");

        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "please wait....", true);
        mConnectThreat = new ConnectThread(device, uuid);
        mConnectThreat.start();
    }
    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectedThread: Starting");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                mProgressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
                mmInStream = tmpIn;
                mmOutStream = tmpOut;
        }
        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            while(true){
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: "+ incomingMessage);
                    Intent incomingMessageIntent = new Intent("incomingMessage");
                    incomingMessageIntent.putExtra("theMessage", incomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading to inputstream. "+ e.getMessage());
                    break;
                }
            }
        }
        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: writing to outputstream: "+ text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to outputstream. "+ e.getMessage());
            }
        }
        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void connected(BluetoothSocket msocket, BluetoothDevice mDevice) {
        Log.d(TAG, "connected: Starting");

        mConnectedThread = new ConnectedThread(msocket);
        mConnectedThread.start();
    }
    public void write(byte[] out){
        ConnectedThread r;
        Log.d(TAG, "write: Write Called");
        mConnectedThread.write(out);
    }
}
