package com.wepay.android.internal.mock;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.roam.roamreaderunifiedapi.ConfigurationManager;
import com.roam.roamreaderunifiedapi.DeviceManager;
import com.roam.roamreaderunifiedapi.TransactionManager;
import com.roam.roamreaderunifiedapi.callback.AudioJackPairingListener;
import com.roam.roamreaderunifiedapi.callback.AudioJackPairingListenerWithDevice;
import com.roam.roamreaderunifiedapi.callback.CalibrationListener;
import com.roam.roamreaderunifiedapi.callback.DeviceResponseHandler;
import com.roam.roamreaderunifiedapi.callback.DeviceStatusHandler;
import com.roam.roamreaderunifiedapi.callback.ReleaseHandler;
import com.roam.roamreaderunifiedapi.callback.SearchListener;
import com.roam.roamreaderunifiedapi.constants.CalibrationResult;
import com.roam.roamreaderunifiedapi.constants.Command;
import com.roam.roamreaderunifiedapi.constants.CommunicationType;
import com.roam.roamreaderunifiedapi.constants.DeviceStatus;
import com.roam.roamreaderunifiedapi.constants.DeviceType;
import com.roam.roamreaderunifiedapi.constants.ErrorCode;
import com.roam.roamreaderunifiedapi.constants.Parameter;
import com.roam.roamreaderunifiedapi.constants.ResponseCode;
import com.roam.roamreaderunifiedapi.data.CalibrationParameters;
import com.roam.roamreaderunifiedapi.data.Device;
import com.wepay.android.models.MockConfig;

import java.util.HashMap;
import java.util.Map;

public class MockRoamDeviceManager implements DeviceManager{
    private static MockRoamDeviceManager deviceManager;
    private static MockRoamConfigurationManager configurationManager;
    private static MockRoamTransactionManager transactionManager;
    private static final long READER_CONNECTION_TIME_MS = 200;
    private static final long READER_DISCOVERED_TIME_MS = 500;
    private static final long READER_RELEASE_TIME_MS = 1000;
    private static final long DISCOVERY_COMPLETE_TIME_MS = 1000;
    private static final String MOCK_DEVICE_NAME = "AUDIOJACK";
    private static final String MOCK_DEVICE_IDENTIFIER = "RP350MOCK";

    private Context context;
    private MockConfig mockConfig;
    private DeviceStatusHandler deviceStatusHandler;
    private boolean isReady = false;
    private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private Runnable discoveredDeviceRunnable = null;
    private Runnable discoveryCompleteRunnable = null;

    private void runOnMainThread(Runnable runnable, long delayMillis) {
        mainThreadHandler.postDelayed(runnable, delayMillis);
    }

    public static MockRoamDeviceManager getDeviceManager() {
        if (deviceManager == null) {
            deviceManager = new MockRoamDeviceManager();
        }
        return deviceManager;
    }

    public void setMockConfig(MockConfig mockConfig) {
        this.mockConfig = mockConfig;
    }

    @Override
    public boolean initialize(Context context, DeviceStatusHandler deviceStatusHandler, CalibrationParameters params) {
        return initialize(context, deviceStatusHandler);
    }

    @Override
    public boolean initialize(Context context, final DeviceStatusHandler deviceStatusHandler) {
        this.context = context;
        this.deviceStatusHandler = deviceStatusHandler;
        this.isReady = true;

        // Update ConfigurationManager and TransactionManager singleton objects
        // so that states don't persist through tests
        ((MockRoamConfigurationManager) getConfigurationManager())
                .setMockConfig(mockConfig)
                .setDeviceStatusHandler(deviceStatusHandler);
        ((MockRoamTransactionManager) getTransactionManager())
                .setMockConfig(mockConfig)
                .setDeviceStatusHandler(deviceStatusHandler)
                .resetStates();

        if (!mockConfig.isCardReadTimeout()) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    deviceStatusHandler.onConnected();
                }
            }, READER_CONNECTION_TIME_MS);
        }

        return true;
    }

    @Override
    public ConfigurationManager getConfigurationManager() {
        if (configurationManager == null) {
            configurationManager = new MockRoamConfigurationManager(mockConfig, deviceStatusHandler);
        }
        return configurationManager;
    }

    @Override
    public TransactionManager getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = new MockRoamTransactionManager(mockConfig, deviceStatusHandler);
        }
        return transactionManager;
    }

    @Override
    public DeviceStatus getStatus() {
        return null;
    }

    @Override
    public boolean isReady() {
        return this.isReady;
    }

    /**
     * Releases the ROAM device and triggers status handler onDisconnected call back method if successful.
     */
    @Override
    public boolean release() {
        this.release(null);
        return true;
    }

    @Override
    public void release(final ReleaseHandler releaseHandler) {
        if (deviceStatusHandler != null) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    // call done on releasehandler
                    if (releaseHandler != null) {
                        releaseHandler.done();
                    }

                    deviceStatusHandler.onDisconnected();
                }
            }, READER_RELEASE_TIME_MS);
        }
    }

    @Override
    public void requestPairing(AudioJackPairingListener audioJackPairingListener) {

    }

    @Override
    public void requestPairing(AudioJackPairingListenerWithDevice pairListener) {

    }

    @Override
    public void confirmPairing(Boolean aBoolean) {

    }

    @Override
    public void searchDevices(Context ctx, Boolean includeBondedDevices, SearchListener searchListener) {
        this.searchDevices(ctx, includeBondedDevices, DISCOVERY_COMPLETE_TIME_MS, searchListener);
    }

    @Override
    public void searchDevices(Context ctx, Boolean includeBondedDevices, Long durationInMilliseconds, final SearchListener searchListener) {
        // Bypassing durationInMilliseconds so that the mock experience is quicker.

        // In Android, we will always return that this mock device was discovered because Roam
        // always "discovers" RP350x devices.
        discoveredDeviceRunnable = new Runnable() {
            @Override
            public void run() {
                Device device = new Device(DeviceType.RP350x, CommunicationType.AudioJack, MOCK_DEVICE_NAME, MOCK_DEVICE_IDENTIFIER);
                searchListener.onDeviceDiscovered(device);
            }
        };

        discoveryCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                searchListener.onDiscoveryComplete();
            }
        };

        this.runOnMainThread(discoveredDeviceRunnable, READER_DISCOVERED_TIME_MS);
        this.runOnMainThread(discoveryCompleteRunnable, DISCOVERY_COMPLETE_TIME_MS);
    }

    @Deprecated
    @Override
    public void searchDevices(Context ctx, SearchListener searchListener) {

    }

    @Override
    public void cancelSearch() {
        mainThreadHandler.removeCallbacks(discoveredDeviceRunnable);
        mainThreadHandler.removeCallbacks(discoveryCompleteRunnable);
    }

    @Override
    public void registerDeviceStatusHandler(DeviceStatusHandler deviceStatusHandler) {
        this.deviceStatusHandler = deviceStatusHandler;
    }

    @Override
    public void enableFirmwareUpdateMode(DeviceResponseHandler deviceResponseHandler) {

    }

    @Override
    public void updateFirmware(String s, DeviceResponseHandler deviceResponseHandler) {

    }

    @Override
    public CommunicationType getActiveCommunicationType() {
        throw new java.lang.UnsupportedOperationException("Unimplemented method: getActiveCommunicationType()");
    }

    @Override
    public void getBatteryStatus(final DeviceResponseHandler deviceResponseHandler) {
        if (deviceResponseHandler == null || mockConfig.isBatteryLevelError()) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {

                    final Map<Parameter, Object> res = new HashMap<>();
                    res.put(Parameter.Command, Command.BatteryInfo);
                    res.put(Parameter.ResponseCode, ResponseCode.Error);
                    res.put(Parameter.ErrorCode, ErrorCode.UnknownError);

                    deviceResponseHandler.onResponse(res);
                }
            }, READER_CONNECTION_TIME_MS);
        } else {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {

                    final Map<Parameter, Object> res = new HashMap<>();
                    res.put(Parameter.Command, Command.BatteryInfo);
                    res.put(Parameter.ResponseCode, ResponseCode.Success);
                    res.put(Parameter.BatteryLevel, 100);

                    deviceResponseHandler.onResponse(res);
                }
            }, READER_CONNECTION_TIME_MS);
        }
    }

    @Override
    public void getDeviceStatistics(DeviceResponseHandler deviceResponseHandler) {

    }

    @Override
    public void startCalibration(Context context, CalibrationListener calibrationListener) {
        CalibrationParameters parameters = new CalibrationParameters();
        calibrationListener.onComplete(CalibrationResult.Succeeded, parameters);
    }

    @Override
    public void stopCalibration(Context context) {

    }

    @Override
    public DeviceType getType() {
        return DeviceType.RP350x;
    }

}
