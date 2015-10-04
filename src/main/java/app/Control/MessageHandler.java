package app.Control;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class MessageHandler {

    private static final String ERROR = "Error";
    private static final String OK = "OK";
    private static final String SUCCESS = "Success";
    private static final String INFO = "INFO";

    private static MessageHandler instance;
    private AlertDialog.Builder alertBuilder = null;
    private Context mContext = null;
    private ProgressDialog progressDialog = null;

    public static MessageHandler getInstance(Context c) {
        if (instance == null) {
            instance = new MessageHandler();
        }

        try {
            if (c != null) {
//			instance.alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(c, android.R.style.Theme_Holo_Light));
//          .setIcon(R.drawable.appicon);
                instance.alertBuilder = new AlertDialog.Builder(c);
                instance.mContext = c;
            } else {
                instance.alertBuilder = null;
                instance.mContext = null;
            }
        }catch (Exception e){
            instance.alertBuilder = null;
            instance.mContext = null;
        }

        return instance;
    }

    public static MessageHandler getInstance() {
        if (instance == null) {
            instance = new MessageHandler();
        }

        return instance;
    }

    private void centerMessage(AlertDialog dialog) {

        // Center Title
        ((TextView) dialog.findViewById(mContext.getResources().getIdentifier("alertTitle", "id", "android")))
                .setGravity(Gravity.CENTER);
        // Center Message
        TextView message = (TextView) dialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setGravity(Gravity.CENTER);
        }
    }

    public void putSimpleErrorMsg(String msg) {

        if (!isMessagePossible())
            return;

        alertBuilder.setTitle(ERROR);
        alertBuilder.setMessage(msg);
        alertBuilder.setPositiveButton(OK, null);
        AlertDialog dialog = alertBuilder.show();

        centerMessage(dialog);

        MessageHandler.getInstance(mContext).stopSimpleProgressDialog();
    }

    public void putSimpleErrorMsgAndFinish(String msg) {

        if (!isMessagePossible())
            return;

        alertBuilder.setTitle(ERROR);
        alertBuilder.setMessage(msg);
        alertBuilder.setPositiveButton(OK, null);
        AlertDialog dialog = alertBuilder.show();

        centerMessage(dialog);

        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                ((Activity) mContext).finish();
            }
        });

        dialog.show();

        MessageHandler.getInstance(mContext).stopSimpleProgressDialog();
    }

    public void putSimpleInfoMsg(String msg) {
        if (!isMessagePossible())
            return;

        alertBuilder.setTitle(INFO);
        alertBuilder.setMessage(msg);
        alertBuilder.setPositiveButton(OK, null);
        AlertDialog dialog = alertBuilder.show();

        centerMessage(dialog);
    }

    public void putSimpleSuccessMsg(String msg) {

        if (!isMessagePossible())
            return;

        alertBuilder.setTitle(SUCCESS);
        alertBuilder.setMessage(msg);
        alertBuilder.setPositiveButton(OK, null);
        AlertDialog dialog = alertBuilder.show();

        centerMessage(dialog);
    }

    public void putSimpleSuccessMsgAndFinish(String msg) {

        if (!isMessagePossible())
            return;

        alertBuilder.setTitle(SUCCESS);
        alertBuilder.setMessage(msg);
        alertBuilder.setPositiveButton(OK, null);
        AlertDialog dialog = alertBuilder.show();

        centerMessage(dialog);

        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                ((Activity) mContext).finish();
            }
        });

        dialog.show();

        MessageHandler.getInstance(mContext).stopSimpleProgressDialog();
    }

    public void showSimpleProcessingDialog(String title, String message) {
        progressDialog = ProgressDialog.show(mContext, title, message, true);
        // progressDialog.setCancelable(true);
    }

    public void stopSimpleProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private boolean isMessagePossible() {
        if (mContext == null || alertBuilder == null)
            return false;

        if (mContext instanceof Activity)
            if (((Activity) mContext).isFinishing()) {
                return false;
            }

        return true;
    }

    public void showShortToastMessage(String msg) {
        showToastMessage(msg, Toast.LENGTH_SHORT);
    }

    public void showLongToastMessage(String msg) {
        showToastMessage(msg, Toast.LENGTH_LONG);
    }

    private void showToastMessage(String msg, int length) {
    try {
        if (!isMessagePossible())
            return;

        Toast toast = Toast.makeText(mContext, msg, length);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null)
            v.setGravity(Gravity.CENTER);
        toast.show();
    }catch (Exception e){
        // TODO: inform server
    }
    }


}
