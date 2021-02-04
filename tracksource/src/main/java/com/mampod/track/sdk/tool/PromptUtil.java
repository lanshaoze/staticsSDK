package com.mampod.track.sdk.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.mampod.track.sdk.config.TrackSDK;
import com.mampod.tracksource.R;

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

        @SuppressLint("ValidFragment")
        DialogFragment promptDialog = new DialogFragment() {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
                    savedInstanceState) {
                getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
                View view = inflater.inflate(R.layout.fragment_prompt, container);
                final TextView tvEvent1 = (TextView) view.findViewById(R.id.tv_event1);
                tvEvent1.setText(actions);
                tvEvent1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String s = tvEvent1.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            ClipText(context, s);
                        }
                        return true;
                    }
                });
                return view;
            }
        };
        promptDialog.show(context.getFragmentManager(), "prompt");
    }


    private static void ClipText(Activity context, String text) {

        if (context == null || TextUtils.isEmpty(text)) {
            return;
        }
        //复制功能
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setPrimaryClip(ClipData.newPlainText("PromptUtil", text));
        Toast.makeText(context, "复制成功!!!", Toast.LENGTH_LONG).show();

    }

}
