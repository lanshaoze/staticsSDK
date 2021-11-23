package com.mampod.track.sdk.tool;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.mampod.track.sdk.config.TrackSDK;

/**
 * package_name:
 * author：jack-lu on 18/3/1 13:56
 * class_name: PromptUtil
 */
public class PromptUtil {
    public static void showPromptDialog(Activity context, String actions) {
        if (context == null || actions == null) {
            return;
        }
        if (!TrackSDK.getInstance().isShowLog()) {
            return;
        }
        PromptDialog promptDialog = new PromptDialog();
        Bundle values = new Bundle();
        values.putSerializable(PromptDialog.actions, actions);
        promptDialog.setArguments(values);
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(promptDialog, PromptDialog.class.getSimpleName()).commitAllowingStateLoss();
        ((FragmentActivity) context).getSupportFragmentManager().executePendingTransactions();
    }
}
