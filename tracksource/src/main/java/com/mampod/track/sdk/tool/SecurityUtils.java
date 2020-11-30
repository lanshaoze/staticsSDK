package com.mampod.track.sdk.tool;

import android.util.Log;

import java.util.UUID;
import java.util.zip.CRC32;

/**
 * crc32算法类
 *
 * @package com.mampod.track.sdk.tool
 * @author: Jack-Lu
 * @date: 2020/11/20 下午4:13
 */
public class SecurityUtils {
    public static String[] chars = new String[]{"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9"};


    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0xa]);
        }
        Log.e("TrackAgent:", "random->" + shortBuffer.toString());
        return shortBuffer.toString();

    }

    public static long getCrc32Value(String data) {
        byte[] b = data.getBytes();//用于验证的数据
        CRC32 c = new CRC32();
        c.reset();//Resets CRC-32 to initial value.
        c.update(b, 0, b.length);//将数据丢入CRC32解码器
        return c.getValue();
    }
}
