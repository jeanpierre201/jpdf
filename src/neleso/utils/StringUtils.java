package neleso.utils;
        
import java.util.ArrayList;
import java.util.List;


        public class StringUtils {
        	
        	public static final String EMPTY = "";
        	
        	 public static boolean isEmpty(String str) {
                 return str == null || str.length() == 0;
             }
        
        	 public static String substringBetween(String str, String tag) {
                 return substringBetween(str, tag, tag);
             }
        	 
        	 public static String substringBetween(String str, String open,
                     String close) {
                 if (str == null || open == null || close == null) {
                     return null;
                 }
                 int start = str.indexOf(open);
                 if (start != -1) {
                     int end = str.indexOf(close, start + open.length());
                     if (end != -1) {
                         return str.substring(start + open.length(), end);
                     }
                 }
                 return null;
             }
        	 
        	 public static String[] substringsBetween(String str, String open,
                     String close) {
                 if (str == null || isEmpty(open) || isEmpty(close)) {
                     return null;
                 }
                 int strLen = str.length();
                 if (strLen == 0) {
                     return ArrayUtils.EMPTY_STRING_ARRAY;
                 }
                 int closeLen = close.length();
                 int openLen = open.length();
                 List<String> list = new ArrayList<String>();
                 int pos = 0;
                 while (pos < (strLen - closeLen)) {
                     int start = str.indexOf(open, pos);
                     if (start < 0) {
                         break;
                     }
                     start += openLen;
                     int end = str.indexOf(close, start);
                     if (end < 0) {
                         break;
                     }
                     list.add(str.substring(start, end));
                     pos = end + closeLen;
                 }
                 if (list.isEmpty()) {
                     return null;
                 }
                 return list.toArray(new String[list.size()]);
             }

        	 
        }