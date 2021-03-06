package ysn.com.stock.utils;

/**
 * @Author yangsanning
 * @ClassName NumberUtils
 * @Description 控制小数点的工具类
 * @Date 2019/5/23
 * @History 2019/5/23 author: description:
 */
public class NumberUtils {

    /**
     * @param value 数值
     * @param digits 保留小数点后的位数
     * @return 格式化后的字符串
     */
    public static String numberFormat(float value, int digits) {
        return String.format("%." + digits + "f", formatDouble(value, digits));
    }

    /**
     * @param value 数值
     * @param digits 保留小数点后的位数
     * @return 格式化后的数值
     */
    private static double formatDouble(float value, int digits) {
        double digit = Math.pow(10, digits);
        return (double) Math.round(value * digit) / digit;
    }
}
