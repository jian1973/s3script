package org.songjian.utils;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class StringConvertUtils {

	private static String fDBCharset;
	private static String fJavaCharset;
	private static String fClientCharset;

	public static void init(Properties aProperties) {
		fDBCharset = aProperties.getProperty("DBCharset");
		fClientCharset = aProperties.getProperty("ClientCharset");
		fJavaCharset = aProperties.getProperty("JavaCharset");
	}

	public static void init() {
		if (fDBCharset == null)
			fDBCharset = "UTF-8";
		if (fJavaCharset == null)
			fJavaCharset = System.getProperty("file.encoding");
		if (fClientCharset == null)
			fClientCharset = "UTF-8";
	}

	public static String getJavaStringCharset() {
		init();
		return fJavaCharset;
	}

	public static String getBytesCharset() {
		init();
		return fClientCharset;
	}

	public static byte[] javaString2Bytes(String aString) {
		init();
		try {
			return aString.getBytes(getBytesCharset());
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String bytes2JavaString(byte[] aBytes, int aStart, int aLen) throws UnsupportedEncodingException {
		init();
		if (fClientCharset.equalsIgnoreCase(fJavaCharset))
			return new String(aBytes, aStart, aLen);
		else {
			byte[] ftemp;
			ftemp = new byte[aLen];
			System.arraycopy(aBytes, aStart, ftemp, 0, aLen);
			return new String(ftemp, fClientCharset);
		}
	}

	public static String DBString2JavaString(String aString) {
		init();
		return aString;
	}

	public static String javaString2DBString(String aString) {
		init();
		// try {
		// return new String( aString.getBytes(fClientCharset),fDBCharset);
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		return aString;
	}

	public static CallableStatement createPrepareCall(Connection aCon, String aSQL) throws SQLException {
		String fSQL;
		fSQL = javaString2DBString(aSQL);
		return aCon.prepareCall(fSQL);
	}

	public static String getCallString(CallableStatement aCall, int aIndex) throws SQLException {
		String result = aCall.getString(aIndex);
		result = DBString2JavaString(result);
		return result;
	}

	public static PreparedStatement createPs(Connection aCon, String aSQL) throws SQLException {
		String fSQL;
		fSQL = javaString2DBString(aSQL);
		return aCon.prepareStatement(fSQL);
	}

	public static String getRsString(ResultSet aRs, int aIndex) throws SQLException {
		String result = aRs.getString(aIndex);
		result = DBString2JavaString(result);
		return result;
	}

	public static String getRsString(ResultSet aRs, String aFieldName) throws SQLException {
		String result = aRs.getString(aFieldName);
		result = DBString2JavaString(result);
		return result;
	}

	public static void setPsString(PreparedStatement aPs, int aIndex, String aValue) throws SQLException {
		String fSetValue;
		fSetValue = javaString2DBString(aValue);
		aPs.setString(aIndex, fSetValue);
	}

	/**
	 * 字符到字节转换
	 * 
	 * @param ch
	 * @return
	 */
	private static void putChar(byte[] bb, char ch, int index) {
		int temp = (int) ch;
		for (int i = 0; i < 2; i++) {
			bb[index + i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
	}

	/**
	 * 字节到字符转换
	 * 
	 * @param b
	 * @return
	 */
	private static char getChar(byte[] b, int index) {
		short ftemp;
		ftemp = (short) (((b[index + 0] & 0xFF) << 0) + ((b[index + 1] & 0xFF) << 8));

		char ch = (char) ftemp;
		return ch;
	}

	public static byte[] charArrayToByteArray(char[] aCh) {
		if (aCh == null)
			return null;
		byte[] result;
		result = new byte[aCh.length * 2];
		for (int i = 0; i < aCh.length; i++) {
			putChar(result, aCh[i], i * 2);
		}
		return result;
	}

	public static char[] byteArrayToCharArray(byte[] aByte) {
		if (aByte == null)
			return null;
		char[] result;
		result = new char[aByte.length / 2];
		for (int i = 0; i < result.length; i++) {
			result[i] = getChar(aByte, i * 2);
		}
		return result;
	}

	/**
	 * 根据charArrayToByteArray 生成的Byte数组生成字符串
	 * 
	 * @param aByte
	 * @return
	 */
	public static String getByteArrayStr_CharArray(byte[] aByte) {
		if (aByte == null)
			return null;
		char[] result;
		result = byteArrayToCharArray(aByte);
		return new String(result);
	}

	public static byte[] getStringToByte_CharArray(String aValue) {
		if (aValue == null)
			return null;
		byte[] result;
		result = charArrayToByteArray(aValue.toCharArray());
		return result;
	}

	/**
	 * 去掉零字符后面的数据
	 * 
	 * @param aStr
	 * @return
	 */
	public static String clearStringZeroEnd(String aStr) {
		StringBuilder fb = new StringBuilder();
		char fChar;
		if (aStr == null)
			return null;
		for (int i = 0; i < aStr.length(); i++) {
			fChar = aStr.charAt(i);
			if (fChar != 0)
				fb.append(fChar);
			else
				break;

		}
		return fb.toString();
	}
}
