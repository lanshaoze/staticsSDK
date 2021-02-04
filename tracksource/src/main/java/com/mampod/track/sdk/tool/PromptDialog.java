package com.mampod.track.sdk.tool;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.mampod.tracksource.R;

/**
 * Description.
 *
 * @package com.mampod.track.sdk.tool
 * @author: jack
 * @date: 2021/2/4 下午4:17
 */
public class PromptDialog extends DialogFragment {

    public static String actions = "actions";

    private String e = null;

    private TextView content;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_prompt, container);
        content = (TextView) view.findViewById(R.id.tv_event1);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        e = getArguments().getString(actions);
        content.setText(e);
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String s = content.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    ClipText(getActivity(), s);
                }
                return true;
            }
        });
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
