package com.gemini.toolkit.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FunctionUtility {
	
	/** 1つ半角空白文字 */
	private static final char SPACE_ENG = ' ';
	/** 全角空白文字 */
	private static final char SPACE_JP = '　';

	/**
	 * 去掉字符串两边多余的空格
	 * 
	 * @param 字符串
	 * @return 处理后的字符串
	 */
	public static String getTrimW(String stringIn) {
		StringBuffer leftTrimString;
		StringBuffer rightTrimString;

		try {
			if (stringIn == null) {
				return null;
			}
			if (stringIn.equals("")) {
				return "";
			}
			leftTrimString = new StringBuffer(getLeftTrimW(stringIn));
			rightTrimString = new StringBuffer(getRightTrimW(leftTrimString.toString()));
			return rightTrimString.toString();
		} catch (Exception ex) {
			return null;
		}
	}
	
	/**
	 * 去掉字符串左边的空格
	 * 
	 * @param stringIn 字符串
	 * @return 处理后的字符串
	 */
	public static String getLeftTrimW(String stringIn) {

		char[] arrayString;
		int countNum;

		try {
			if (stringIn == null) {
				return null;
			}
			if (stringIn.equals("")) {
				return "";
			}
			arrayString = stringIn.toCharArray();
			countNum = -1;
			for (int i = 0; i < stringIn.length(); i++) {
				if (arrayString[i] != SPACE_JP && arrayString[i] != SPACE_ENG) {
					countNum = i;
					break;
				}
			}
			if (countNum == -1) {
				return "";
			} else {
				return stringIn.substring(countNum);
			}
		} catch (Exception ex) {
			return "";
		}
	}
	
	/**
	 * 去掉字符串右边的空格
	 * 
	 * @param stringIn 字符串
	 * @return 处理后的字符串
	 */
	public static String getRightTrimW(String stringIn) {
		char[] arrayString;
		int countNum;

		try {
			if (stringIn == null) {
				return null;
			}
			if (stringIn.equals("")) {
				return "";
			}
			arrayString = stringIn.toCharArray();
			countNum = -1;
			for (int i = stringIn.length() - 1; i >= 0; i--) {
				if (arrayString[i] != SPACE_JP && arrayString[i] != SPACE_ENG) {
					countNum = i;
					break;
				}
			}
			if (countNum == -1) {
				return "";
			} else {
				return stringIn.substring(0, countNum + 1);
			}
		} catch (Exception ex) {
			return "";
		}
	}
	
	/**
	 * 字符串前处理，变为指定长度的字符串
	 * 
	 * @param baseValue 待处理的字符串
	 * @param length 处理后字符串的长度
	 * @param supplyValue 若待处理的字符串小于指定长度时，待处理字符串前面空缺位要填充的字符
	 * @exception NullPointerException 第一个和第三个参数为null的场合
	 * @exception IllegalArgumentException 第二个参数小于等于0，第三个参数不为一位的字符串
	 * @return 处理后的文字串 <br/>
	 * <br/>
	 * 例如: supplyFront("1",2,"0") return "01"; 用于日期的处理； supplyFront("01",2,"0") return "01";
	 */
	public static String supplyFront(String baseValue, int length, String supplyValue) {
		if (baseValue == null || supplyValue == null) {
			throw new NullPointerException();
		}
		if (supplyValue.length() != 1) {
			throw new IllegalArgumentException();
		}
		if (length <= 0) {
			throw new IllegalArgumentException();
		}

		int baseValueSize = baseValue.length();

		StringBuffer stringBuffer = new StringBuffer();

		if (baseValueSize < length) {
			int loopCount = length - baseValueSize;
			for (int i = 0; i < loopCount; i++) {
				stringBuffer.append(supplyValue);
			}
		}
		stringBuffer.append(baseValue);

		return stringBuffer.toString();
	}
	/**
	 * 字符串转时间戳
	 * @param datetime
	 * @return 时间戳
	 */
	public static long parseTimestamp(String datetime){
		try{
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date	= dateformat.parse(datetime);
			return date.getTime()/1000;			
		}catch(Exception e){
			 e.printStackTrace();
		}
		
		return 0;
	}
}
