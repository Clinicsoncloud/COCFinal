package com.abhaybmicoctest.app.hemoglobin;

public interface GattClientActionListener {

    void log(String message);

    void logError(String message);

    void setConnected(boolean connected);

    void initializeTime();

    void initializeEcho();

    void disconnectGattServer();
    void showToast(String msg);
}
