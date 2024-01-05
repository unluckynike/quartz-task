package org.example.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * @Package org.example.utils
 * @Author hailin
 * @Date 2024/1/5
 * @Description :时间格式校验
 */

public class DataUtil {

    // 验证日期格式的正则表达式
    private static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";

    // 日期时间格式
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static boolean isValidAndFormatTime(String timeExpression) {
        if (isValidDate(timeExpression)) {
            return true;
        } else if (isValidDateTime(timeExpression)) {
            return true;
        } else {
            System.out.println("Invalid time format. Performing correction or handling error.");
            return false;
        }
    }

    private static boolean isValidDate(String dateExpression) {
        Pattern pattern = Pattern.compile(DATE_REGEX);
        Matcher matcher = pattern.matcher(dateExpression);
        return matcher.matches();
    }

    private static boolean isValidDateTime(String dateTimeExpression) {
        try {
            new SimpleDateFormat(DATE_TIME_FORMAT).parse(dateTimeExpression);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String inputDate = "2024-01-05";
        String inputDateTime = "2024-01-05 10:41:55";
        String inputDateTimEerro = "2024-01";

        boolean isValidatedDate = isValidAndFormatTime(inputDate);
        boolean isValidatedDateTime = isValidAndFormatTime(inputDateTime);
        boolean validAndFormatTime = isValidAndFormatTime(inputDateTimEerro);

        System.out.println("Is Valid Date: " + isValidatedDate);
        System.out.println("Is Valid DateTime: " + isValidatedDateTime);
        System.out.println("validAndFormatTime: "+validAndFormatTime);
    }
}