package com.yeucheng.openim.util;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by Administrator on 2018/5/9.
 */

public class PinyinUtil {
    public static String getPinyin(String str) {
        return PinyinHelper.convertToPinyinString(str, "", PinyinFormat.WITHOUT_TONE);
    }
}
