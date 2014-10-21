package com.jsmobile.plugins.sms;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.net.Uri;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

public class Sms extends CordovaPlugin {
    private static final String SMS_FEATURE_NOT_SUPPORTED = "SMS_FEATURE_NOT_SUPPORTED";
    private static final String CANCELLED = "CANCELLED";

    private Context context;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.context = cordova.getActivity().getApplicationContext();

        if (!action.equals("sendMessage")) {
            return false;
        }
        String phoneNumber = args.getString(0);
        String message = args.getString(1);


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", phoneNumber);
        intent.putExtra("sms_body", message);
        intent.setData(Uri.parse("smsto:" + phoneNumber));

        if (!canHandle(intent)) {
            return signalError(callbackContext, SMS_FEATURE_NOT_SUPPORTED, "SMS feature is not supported on this device");
        }

        cordova.startActivityForResult(this, intent, 1);

        PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
        r.setKeepCallback(true);
        callbackContext.sendPluginResult(r);
        return true;
    }

    private boolean canHandle(Intent intent) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            callbackContext.success("");
        } else if (resultCode == Activity.RESULT_CANCELED) {
            signalError(callbackContext, "CANCELLED", "Sending was cancelled");
        }
    }

    private boolean signalError(CallbackContext callbackContext, String code, String message) {
        callbackContext.sendPluginResult(new PluginResult(Status.ERROR, error(code, message)));
        return false;
    }

    private JSONObject error(String code, String message) {
        JSONObject errorObject = new JSONObject();
        try {
            errorObject.put("code", code);
            errorObject.put("message", message);
        } catch (JSONException e) {
        }
        return errorObject;
    }
}
