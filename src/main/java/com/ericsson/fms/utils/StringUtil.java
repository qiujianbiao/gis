package com.ericsson.fms.utils;

import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * 字符串
 * 
 * @author Peter
 */
public class StringUtil {

	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	/**
	 * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
	 * 
	 * @param s
	 *            s 需要得到长度的字符串
	 * @return int 得到的字符串长度
	 */
	public static int length(String s) {
		if (s == null)
			return 0;
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}
	
	/**
	 *  去除字符串制表符  \n \t \r
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
	
	/**
	 * 判断是否为空 
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null || "".equals(obj)) {
            return true;
        }
        if (obj instanceof String && obj.toString().trim().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        }
        return false;
	}

	public static String toString(Object[] arr) {
		StringBuilder b = new StringBuilder();
		if (arr != null && arr.length>0) {
			for (int i = 0; i<arr.length; i++) {
				b.append("'").append(String.valueOf(arr[i])).append("'");
				if(i+1!=arr.length){
					b.append(", ");
				}

			}
		}
		return b.toString();
	}
	
	public static String subString(String str, int beginIndex, int endIndex) {
		if(str == null || "".equals(str)){
			return "";
		}else{
			return str.substring(beginIndex, str.length() < endIndex ? str.length() : endIndex);
		}
	}
	
	public static String setFromBASE64(byte[] b) {    
        if (b == null)    
            return null;    
        BASE64Encoder encoder = new BASE64Encoder();    
        try {    
            String s = encoder.encode(b);    
            return s;    
        } catch (Exception e) {    
            return null;    
        }    
    }
	
	
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String dateToStamp(String s) throws ParseException {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		System.out.println("s="+s);
		Date date = simpleDateFormat.parse(s);
		long ts = date.getTime();
		res = String.valueOf(ts);
		return res;
	}


	public static void tree() {
		TreeMap<String, String> treeMap = new TreeMap<>();
		TreeMap<String, String> treeMap1 = new TreeMap<>();
		treeMap.put("2018-01-23T22:00:01.000Z", "h");
		treeMap.put("2018-01-23T22:00:02.000Z", "g");
		treeMap.put("2018-01-23T22:00:03.000Z", "f");
		treeMap.put("2018-01-23T22:00:04.000Z", "e");
		treeMap.put("2018-01-23T22:00:05.000Z", "a");
		treeMap.put("2018-01-23T22:00:06.000Z", "w");
		treeMap.put("2018-01-23T22:00:07.000Z", "v");
		treeMap.put("2018-01-23T22:00:08.000Z", "u");
		treeMap.put("2018-01-23T22:00:09.000Z", "d");
		treeMap.put("2018-01-23T22:00:10.000Z", "c");
		treeMap.put("2018-01-23T01:00:10.000Z", "c");
		System.out.println("----------------------*------------------------------");
		while (treeMap.size() != 0) {
			//treemap自动按照key进行递增排序
			System.out.println(treeMap.firstEntry().getKey() + " - " + treeMap.firstEntry().getValue());
			treeMap1.put(treeMap.firstEntry().getValue(), treeMap.firstEntry().getKey());
			treeMap.remove(treeMap.firstKey());
		}
		System.out.println("----------------------*------------------------------");
		while (treeMap1.size() != 0) {
			//treemap自动按照key进行递增排序
			System.out.println(treeMap1.firstEntry().getKey() + " - " + treeMap1.firstEntry().getValue());
			treeMap1.remove(treeMap1.firstKey());
		}
		System.out.println("----------------------*------------------------------");
	}

	public static void main(String[] args){
		try {

			String departureTime = "2018-01-23T22:00:00.000Z";

			departureTime = departureTime.replace("z","").replace("Z","");
			departureTime = departureTime + " UTC" ;
			System.out.println("date="+dateToStamp(departureTime));

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

			String d1 = simpleDateFormat.format(new Date());
			System.out.println("d1="+dateToStamp(departureTime));

//			2017-08-11T13:00:00.000+0000

//			String input = "2011-08-11T01:23:45.678Z";
//			TimeZone utc = TimeZone.getTimeZone("UTC");
//			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//			f.setTimeZone(utc);
//			GregorianCalendar cal = new GregorianCalendar(utc);
//			cal.setTime(f.parse(input));
//			System.out.println(cal.getTime());


			String kk  = "2018-01-24T08:18:18.413+0000";
			System.out.println("kk="+kk.substring(0, 10));

			long v = 1517539158000l;
			Timestamp ts = new Timestamp(v);
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			System.out.println(f.format(ts));

			TimeZone utc = TimeZone.getTimeZone("UTC");
			f.setTimeZone(utc);
			System.out.println(f.format(ts));



			GregorianCalendar cal = new GregorianCalendar(utc);
			cal.setTime(ts);
			System.out.println(cal.getTime());


			tree();

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}


