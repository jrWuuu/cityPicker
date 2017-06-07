package wuchen.com.citypicker.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by 巫晨 on 2017/6/3.
 */

public class PinYinUtils {

    private static StringBuilder sb = null;

    static {
        if (sb == null) {
            sb = new StringBuilder();
        }
    }

    public static String getPinYin(String cityName) {
        sb.delete(0, sb.length());
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        // 不需要音标
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 设置转换出大写字母
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        char[] chars = cityName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c >= -128 && c <= 127) {
                sb.append(c);
            } else {
                try {
                    String s = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
                    sb.append(s);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
