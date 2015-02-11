package org.apache.cordova.plugin;

import java.util.TimeZone;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Settings;
import android.util.Log;

import com.bionym.ncl.*;

public class Plugin extends CordovaPlugin {
    public static final String TAG = "NYMI";

    public static String platform;                            // Device OS
    public static String uuid;                                // Device UUID
    public boolean nymi;

    private static final String ANDROID_PLATFORM = "Android";
    private static final String AMAZON_PLATFORM = "amazon-fireos";
    private static final String AMAZON_DEVICE = "Amazon";

    /**
     * Constructor.
     */
    public Plugin() {
    }

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Plugin.uuid = getUuid();
        try {
            initNCL();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getDeviceInfo")) {
            JSONObject r = new JSONObject();
            r.put("uuid", Plugin.uuid);
            r.put("version", this.getOSVersion());
            r.put("platform", this.getPlatform());
            r.put("model", this.getModel());
            r.put("nymi", this.nymi);
            callbackContext.success(r);
        }
        else {
            return false;
        }
        return true;
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    private void initNCL(){
        /*
          * Initializes the NCL use Android's Bluetooth Low Energy Library
          */
        // NclHelper.initialize(this);
        /*
          * Initializes the NCL library
           
          * This will initialize the NCL to have the events handled by cb, and
          * communicate with Nymi Bands, and save all the logs to a text file
          */
        NclCallback nclCallback = new MyNclCallback();
        boolean result = Ncl.init(nclCallback, null, "NCLExample", NclMode.NCL_MODE_DEFAULT, this.cordova.getActivity().getApplicationContext());
        this.nymi = result;
    }

    /**
     * Callback for NclEventInit
     *
     */
    class MyNclCallback implements NclCallback {
        @Override
        public void call(NclEvent event, Object userData) {
            // Log.d(LOG_TAG, this.toString() + ": " + event.getClass().getName());
            if (event instanceof NclEventInit) {
                if (!((NclEventInit) event).success) {
                    // runOnUiThread(new Runnable() {
                    //     @Override
                    //     public void run() {
                    //         Toast.makeText(MainActivity.this, "Failed to initialize NCL library!", Toast.LENGTH_LONG).show();
                    //     }
                    // });
                }
            }
        }
    }




    // Device

    /**
     * Get the OS name.
     * 
     * @return
     */
    public String getPlatform() {
        String platform;
        if (isAmazonDevice()) {
            platform = AMAZON_PLATFORM;
        } else {
            platform = ANDROID_PLATFORM;
        }
        return platform;
    }

    /**
     * Get the device's Universally Unique Identifier (UUID).
     *
     * @return
     */
    public String getUuid() {
        String uuid = Settings.Secure.getString(this.cordova.getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return uuid;
    }

    public String getModel() {
        String model = android.os.Build.MODEL;
        return model;
    }

    public String getProductName() {
        String productname = android.os.Build.PRODUCT;
        return productname;
    }

    /**
     * Get the OS version.
     *
     * @return
     */
    public String getOSVersion() {
        String osversion = android.os.Build.VERSION.RELEASE;
        return osversion;
    }

    public String getSDKVersion() {
        @SuppressWarnings("deprecation")
        String sdkversion = android.os.Build.VERSION.SDK;
        return sdkversion;
    }

    public String getTimeZoneID() {
        TimeZone tz = TimeZone.getDefault();
        return (tz.getID());
    }

    /**
     * Function to check if the device is manufactured by Amazon
     * 
     * @return
     */
    public boolean isAmazonDevice() {
        if (android.os.Build.MANUFACTURER.equals(AMAZON_DEVICE)) {
            return true;
        }
        return false;
    }

}
