package com.mampod.track.sdk.tool;

import android.content.Context;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;

/**
 * Created by dk on 18/2/24.
 */

public class EncryptedPreferencesUtil {
    static EncryptedPreferences encryptedPreferences;

    public static EncryptedPreferences getInstance(Context context) {
        if (encryptedPreferences == null) {
            encryptedPreferences = new EncryptedPreferences.Builder(context).withEncryptionPassword("69pD1oEx)NXIsQ2i^QUp0lUFG!HThk-Z").build();
        }
        return encryptedPreferences;
    }
}
