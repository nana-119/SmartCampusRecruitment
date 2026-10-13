package com.example.smartcampusrecruitment.Tool;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinYinUtil {
    public static String getPinyin(String chinese) {
        StringBuilder sb = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null) {
                sb.append(pinyinArray[0]);
            } else {
                sb.append(c);
            }
        }
        return sb.toString().toLowerCase();
    }
}
