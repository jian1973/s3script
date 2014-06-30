package org.songjian.utils;

import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import static org.songjian.utils.IConst.*;

public class StringUtils {
	private static Random fRandom;
	private static int fN=48;
	private static int fA=65;

	public static boolean isBlank(String aStr) {
		if (aStr == null)
			return true;
		if (aStr.trim().equalsIgnoreCase(C_sBlank))
			return true;
		return false;
	}
	public final static boolean Str2bool(String aStr){
		if (aStr==null) return false;
		return aStr.trim().equalsIgnoreCase("true");
	}

	public static boolean isNoBlank(String aStr) {
		if (aStr == null)
			return false;
		if (aStr.trim().equalsIgnoreCase(C_sBlank))
			return false;
		return true;
	}


	public static final String getBlankStr(String aValue) {
		if (aValue == null)
			return C_sBlank;
		return aValue;
	}

	public static final String deleteHead(String aSource, String aHead) {
		if (aSource == null)
			return null;
		if (aHead == null)
			return aSource;
		if (aSource.startsWith(aHead))
			return aSource.substring(aHead.length(), aSource.length());
		else
			return aSource;
	}

	public static final String deleteEnd(String aSource, String aEnd) {
		if (aSource == null)
			return null;
		if (aEnd == null)
			return aSource;
		if (aSource.endsWith(aEnd))
			return aSource.substring(0, aSource.length() - aEnd.length());
		else
			return aSource;
	}

	public static final String deleteBracket(String aSource, String aLeft, String aRight) {
		if (aSource == null)
			return null;
		String result;
		result = deleteHead(aSource, aLeft);
		result = deleteEnd(result, aRight);
		return result;
	}

	public static final boolean isBracketed(String aSource, String aLeft, String aRight) {
		if (aSource == null)
			return false;
		if (aLeft == null)
			return false;
		if (aRight == null)
			return false;
		if (!aSource.startsWith(aLeft))
			return false;
		if (!aSource.endsWith(aRight))
			return false;
		return true;
	}

	public static final String addBracketed(String aSource, String aLeft, String aRight) {
		if (aSource == null)
			return aLeft + aRight;
		String result;
		result = aSource;
		if (!aSource.startsWith(aLeft))
			result = aLeft + result;
		if (!aSource.endsWith(aRight))
			result = result + aRight;

		return result;
	}

	public static final void addString(StringBuilder aSB, String aAppend) {
		aSB.append(aAppend);
	}

	public static final void addStringByCondition(boolean aCondition, StringBuilder aSB, String aAppend, String aSplit) {
		if (!aCondition)
			return;
		aSB.append(aSplit);
		aSB.append(aAppend);
	}

	public static final void addStringByConditionFmt(boolean aCondition, StringBuilder aSB, String aFormat,
			String aSplit, Object... aParams) {
		if (!aCondition)
			return;
		String aData;
		aData = String.format(aFormat, aParams);
		aSB.append(aSplit);
		aSB.append(aData);
	}

	public static final String addStringByCondition(String aSource, String aAppend, String aSplit, boolean aCondition) {
		if (!aCondition)
			return aSource;
		return addString(aSource, aAppend, aSplit);
	}

	public static final String addString(String aSource, String aAppend, String aSplit) {
		if (isBlank(aSource))
			return aAppend;
		else
			return aSource + aSplit + aAppend;
	}

	public static final String addStringLn(String aSource, String aAppend) {
		return addString(aSource, aAppend, C_sNextLine);
	}

	public static final <T extends INameValue> void splitNameValueNameValueList(String aText,
			NamedList<T> aNameValueList) {

		ExceptionUtils.assertFormat(aNameValueList != null, "splitNameValueTNameValueList() aNameValueList is null!");

		if (isBlank(aText))
			return;
		String[] ftemp;
		ftemp = aText.split(C_sNextLine);
		ResultTwoValue<String, String> fNameValue;
		T fItem;
		for (int i = 0; i < ftemp.length; i++) {
			fNameValue = splitNameValue(ftemp[i]);
			fItem = aNameValueList.getAndCreate(fNameValue.Value1);
			fItem.setItemValue(fNameValue.Value2);
		}
	}


	public static final HashMap<String, String> splitNameValueHash(String aText, HashMap<String, String> aHashMap) {
		HashMap<String, String> result;
		if (aHashMap == null)

			result = new HashMap<String, String>();
		else
			result = aHashMap;
		if (isBlank(aText))
			return result;
		String[] ftemp;
		ftemp = aText.split(C_sNextLine);
		ResultTwoValue<String, String> fNameValue;
		for (int i = 0; i < ftemp.length; i++) {
			fNameValue = splitNameValue(ftemp[i]);
			result.put(fNameValue.Value1, fNameValue.Value2);
		}
		return result;
	}

	public static final boolean splitValue(String aSource, String aTag, ResultTwoValue<String, String> aResult) {
		boolean result;
		result = false;
		if (aSource == null)
			return false;
		if (aTag == null)
			return false;
		int fIndex;
		fIndex = aSource.indexOf(aTag);
		result = fIndex >= IConst.C_iZero;
		if (!result)
			return false;
		aResult.Value1 = aSource.substring(IConst.C_iZero, fIndex);
		aResult.Value2 = aSource.substring(fIndex + aTag.length(), aSource.length());
		return true;
	}
	
	

	public static final ResultTwoValue<String, String> splitNameValue(String aSource) {
		ResultTwoValue<String, String> result = new ResultTwoValue<String, String>();
		if (!splitValue(aSource, "=", result))
			return null;
		else
			return result;
	}
	
	public static final INameValue splitNameValue_NameValue(String aSource) {
		NameValue result = new NameValue();
		if (!splitValue(aSource, "=", result))
			return null;
		else
			return result;
	}
	
	public static final INameValue splitEMailValue(String aSource){
		NameValue result = new NameValue();
		if (!splitValue(aSource, "@", result))
			return null;
		else
			return result;
	}

	

	public static final String[] getStringList(String aValue) {
		if (aValue == null)
			return new String[IConst.C_iZero];
		return aValue.split("\r\n");
	}

	private final static String C_sCommentFlage = "//";

	public static final String deleteStringComment(String aValue) {
		if (aValue == null)
			return null;
		int fIndex;
		fIndex = aValue.indexOf(C_sCommentFlage);
		boolean fflage;
		fflage = fIndex >= IConst.C_iZero;
		if (!fflage)
			return aValue;
		return aValue.substring(IConst.C_iZero, fIndex);
	}

	public static boolean isBlankList(@SuppressWarnings("rawtypes") List aList) {
		if (aList == null)
			return true;
		if (aList.size() <= C_iZero)
			return true;
		return false;
	}

	public static <T> boolean isBlankArray(T[] aArray) {
		if (aArray == null)
			return true;
		if (aArray.length <= C_iZero)
			return true;
		return false;
	}

	public final static String[] list2Array(List<String> aList) {
		if (isBlankList(aList))
			return null;
		String[] result;
		result = new String[aList.size()];
		result = aList.toArray(result);
		return result;
	}

	public final static String extractExt(String aFilename) {
		if (aFilename == null || aFilename.equals(""))
			return "";
		int findex = aFilename.lastIndexOf('.');
		if (findex < 0)
			return "";
		return aFilename.substring(findex + 1);
	}

	public final static String deleteExt(String aFilename) {
		if (aFilename == null || aFilename.equals(""))
			return "";
		int findex = aFilename.lastIndexOf('.');
		if (findex < 0)
			return aFilename;
		return aFilename.substring(0, findex);
	}
	
	public final static String changeExt(String aFilename,String aNewExt){
		String result;
		result=deleteExt(aFilename);
		if (StringUtils.isBlank(aFilename)) return result;
		return result+"."+aNewExt;
	}

	public final static String extractFileName(String aFilename, boolean aHasExt) {
		if (isBlank(aFilename))
			return "";

		aFilename = aFilename.trim();
		int fEndPos = aFilename.length();
		char fChar;
		for (int i = aFilename.length() - 1; i >= 0; i--) {
			fChar = aFilename.charAt(i);
			if (fChar == '\\' || fChar == '/' || fChar == ':')
				break;
			else
				fEndPos--;
		}
		String result;
		result = aFilename.substring(fEndPos, aFilename.length());
		if (!aHasExt)
			result = deleteExt(result);
		return result;
	}

	/**
	 * 将Java的字符串转换为xml格式的
	 * 
	 * @param aValue
	 * @return
	 */
	public final static String Java2XMLStr(String aValue) {
		if (aValue == null || aValue.length() <= 0)
			return aValue;
		StringBuilder result = new StringBuilder(aValue.length() + 50);
		char ftemp;
		int i = 0;
		while (i < aValue.length()) {
			ftemp = aValue.charAt(i);
			switch (ftemp) {
			case 34:// 双引号
				result.append("&quot;");
				break;
			case 38:// &
				result.append("&amp;");
				break;
			case 60:// <
				result.append("&lt;");
				break;
			case 62:// >
				result.append("&gt;");
				break;
			default:
				result.append(ftemp);
				break;
			}
			i++;
		}
		return result.toString();
	}

	/**
	 * 将Java的字符串转换为Javascript格式的
	 * 
	 * @param aValue
	 * @return
	 */
	public final static String Java2JavaScriptStr(String aValue) {
		if (aValue == null || aValue.length() <= 0)
			return aValue;
		StringBuilder result = new StringBuilder(aValue.length() + 10);
		char ftemp;
		for (int i = 0; i < aValue.length(); i++) {
			ftemp = aValue.charAt(i);
			switch (ftemp) {
			case 9:
				result.append(ftemp);
				break;
			case 10:
				result.append("\\n");
				break;
			case 13:
				result.append("\\r");
				break;
			case 34:
				result.append("\\\"");
				break;
			case 39:
				result.append("\\\'");
				break;
			case 92:
				result.append("\\\\");
				break;
			default:
				if (ftemp >= 0 && ftemp <= 9)
					result.append("\\u000" + ((int) ftemp));
				else if (ftemp >= 10 && ftemp <= 15)
					result.append("\\u000" + (char) (ftemp - 10 + 'A'));
				else if (ftemp > 15 && ftemp <= 25)
					result.append("\\u001" + (int) (ftemp - 16));
				else if (ftemp > 25 && ftemp <= 31)
					result.append("\\u001" + (char) (ftemp - 26 + 'A'));
				else
					result.append(ftemp);
				break;
			}
		}
		return result.toString();
	}

	/**
	 * 将Java的字符串转换为Html attribute格式的
	 * 
	 * @param aValue
	 * @return
	 */
	public final static String Java2HtmlAttStr(String aValue) {
		if (aValue == null || aValue.length() <= 0)
			return aValue;
		StringBuilder result = new StringBuilder(aValue.length() + 50);
		char ftemp;
		int i = 0;
		while (i < aValue.length()) {
			ftemp = aValue.charAt(i);
			switch (ftemp) {
			case 9:
				result.append(ftemp);
				break;
			case 10:
				result.append("\\n");
				break;
			case 13:
				result.append("\\r");
				break;
			case 34:
				result.append("\\\"");
				break;
			// case 39:
			// result.append("\\\'");
			// break;
			case 92:
				result.append("\\\\");
				break;
			default:
				result.append(ftemp);
				break;
			}
			i++;
		}
		return result.toString();
	}

	/**
	 * 将Java的字符串转换为html格式的
	 * 
	 * @param aValue
	 * @return
	 */
	public final static String Java2HtmlStr(String aValue) {
		if (aValue == null || aValue.length() <= 0)
			return aValue;
		StringBuilder result = new StringBuilder(aValue.length() + 50);
		char ftemp;
		int i = 0;
		while (i < aValue.length()) {
			ftemp = aValue.charAt(i);
			switch (ftemp) {
			case 32: // 空白
				result.append("&nbsp;");
				break;
			case 10:// 换行
				// 判断下一个字符是否为回车键
				if ((i + 1) < aValue.length()) {
					if (aValue.charAt(i + 1) == 13)
						i++;// 跳过判断，避免多产生空白行
				}
				result.append("<br>");
				break;
			case 13:// 回车
				// 判断下一个字符是否为换行键
				if ((i + 1) < aValue.length()) {
					if (aValue.charAt(i + 1) == 10)
						i++;// 跳过判断，避免多产生空白行
				}
				result.append("<br>");
				break;
			case 34:// 双引号
				result.append("&quot;");
				break;
			case 38:// &
				result.append("&amp;");
				break;
			case 60:// <
				result.append("&lt;");
				break;
			case 62:
				result.append("&gt;");
				break;
			case 160:
				result.append("&nbsp;");
				break;
			default:
				result.append(ftemp);
				break;
			}
			i++;
		}
		return result.toString();
	}

	public final static String Java2HtmlStr_UTF8(String aValue) {
		if (aValue == null || aValue.length() <= 0)
			return aValue;
		StringBuilder result = new StringBuilder(aValue.length() + 50);
		char ftemp;
		int i = 0;
		while (i < aValue.length()) {
			ftemp = aValue.charAt(i);
			switch (ftemp) {
			case 32: // 空白
				result.append(C_HtmlSpaceChar);
				break;
			case 10:// 换行
				// 判断下一个字符是否为回车键
				if ((i + 1) < aValue.length()) {
					if (aValue.charAt(i + 1) == 13)
						i++;// 跳过判断，避免多产生空白行
				}
				result.append("<br>");
				break;
			case 13:// 回车
				// 判断下一个字符是否为换行键
				if ((i + 1) < aValue.length()) {
					if (aValue.charAt(i + 1) == 10)
						i++;// 跳过判断，避免多产生空白行
				}
				result.append("<br>");
				break;
			case 34:// 双引号
				result.append("&quot;");
				break;
			case 38:// &
				result.append("&amp;");
				break;
			case 60:// <
				result.append("&lt;");
				break;
			case 62:
				result.append("&gt;");
				break;
			case 160:
				result.append("&nbsp;");
				break;
			default:
				result.append(ftemp);
				break;
			}
			i++;
		}
		return result.toString();
	}

	/**
	 * 将string转换成int
	 * 
	 * @param source
	 *            需要转换的string
	 * @param defaultValue
	 *            转换失败时的缺省值
	 * @return
	 */
	public final static int s2i(String source, int defaultValue) {
		try {
			return Integer.parseInt(source);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public final static String intToString(boolean aCondition, int aValue) {
		if (aCondition)
			return "" + aValue;
		else
			return C_sBlank;
	}

	private final static String C_sSpace_10 = "          ";

	public final static String space(int alen) {
		StringBuilder fbuffer = new StringBuilder(alen);
		int loop, mod;
		loop = alen / 10;
		mod = alen % 10;
		for (int i = 0; i < loop; i++) {
			fbuffer.append(C_sSpace_10);
		}
		switch (mod) {
		case 1:
			fbuffer.append(" ");
			break;
		case 2:
			fbuffer.append("  ");
			break;
		case 3:
			fbuffer.append("   ");
			break;
		case 4:
			fbuffer.append("    ");
			break;
		case 5:
			fbuffer.append("     ");
			break;
		case 6:
			fbuffer.append("      ");
			break;
		case 7:
			fbuffer.append("       ");
			break;
		case 8:
			fbuffer.append("        ");
			break;
		case 9:
			fbuffer.append("         ");
			break;
		}

		return fbuffer.toString();
	}

	public static void main(String[] args) {
		try {
			System.out.println(String.format("long='%s'",createTimeId()));
			System.out.println(String.format("long='%s'",createSessionId(30)));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public final static boolean sameString(String aS1, String aS2) {
		if (aS1 == null && aS2 == null)
			return true;
		if (aS1 == null || aS2 == null)
			return false;
		return aS1.equals(aS2);
	}

	public final static boolean sameText(String aS1, String aS2) {
		if (aS1 == null && aS2 == null)
			return true;
		if (aS1 == null || aS2 == null)
			return false;
		return aS1.trim().equalsIgnoreCase(aS2.trim());
	}

	public final static String Utf8Bytes2String(byte[] aBytes) {
		if (aBytes == null)
			return null;
		try {
			return new String(aBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public final static String GuidToFileName(String aGuid) {
		if (aGuid == null)
			return null;
		return aGuid.replaceAll("-", "_");
	}

	public final static String addExtName(String aFileName, String aExtName) {
		if (isBlank(aExtName))
			return aFileName;
		return String.format("%s.%s", aFileName, aExtName);
	}

	public final static String idToFileName(String aFileName, String aExtName) {
		return addExtName(GuidToFileName(aFileName), aExtName);
	}
	public final static String replaceToResourceName(String aName){
		if (aName==null) return null;
		return aName.replaceAll("\\.", "/");
	}

	private static String createTimeId(){
		return Long.toHexString(System.currentTimeMillis()).toUpperCase();
	}
	
	private static String createSessionId(int aLen){
		StringBuilder fb;
		fb=new StringBuilder();
		String ftemp;
		ftemp=createTimeId();
		int fTimeLen;
		fTimeLen=aLen-ftemp.length()-1;
		if (fTimeLen>0){
			fb.append(createRandomIDByLen(fTimeLen));
			fb.append("-");
		}
		if (fTimeLen<0){
			ftemp=ftemp.substring(0, aLen);
		}

		for (int i = ftemp.length()-1; i >=0 ; i--) {
			fb.append(ftemp.charAt(i));
		}
		return fb.toString();
		
	}
	
	private static String createRandomIDByLen(int aLen){
		StringBuilder fb;
		if (fRandom==null)
			fRandom=new Random(System.currentTimeMillis());
		fb=new StringBuilder();
        int fNow;
		char fchar;
		for (int i = 0; i < aLen; i++) {
			fNow=fRandom.nextInt(35);
			if (fNow<10)
				fNow=fN+fNow;
			else
				fNow=fA+fNow-10;
			fchar=(char)fNow;
			fb.append(fchar);
		}
		return fb.toString();
	}

	private static SimpleDateFormat fDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	public static String getFormatedDateStr(long aDate) {
		return fDateformat.format(new Date(aDate));
	}
	
	public static long getDataStrToLong(String aDate){
		try {
			return fDateformat.parse(aDate).getTime();
		} catch (ParseException ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public static String getFormatedDateStr(Date aDate) {
		return fDateformat.format(aDate);
	}

	public static String getNowDataStr() {
		return getFormatedDateStr(new Date());
	}

	

	public static String readFileString(String aFileName, String aCharSet) {
		File fReadFile;
		try {
			fReadFile = new File(aFileName);
			if (!fReadFile.exists())
				return null;
			InputStream fin;
			fin = new FileInputStream(fReadFile);
			try {
				return StreamUtils.readStreamText(fin, aCharSet, true);
			} finally {
				fin.close();
			}
		} catch (Exception ex) {
			throw ExceptionUtils.newException(ex);
		}
	}

	public final static String getPathStr(String aPath) {
		if (isBlank(aPath))
			return aPath;

		aPath = aPath.trim();
		int fEndPos = aPath.length();
		char fChar;
		for (int i = aPath.length() - 1; i >= 0; i--) {
			fChar = aPath.charAt(i);
			if (fChar == '\\' || fChar == '/') {
				fEndPos--;
			} else
				break;
		}
		String result;
		result = aPath.substring(0, fEndPos);
		if (result.endsWith(":"))
			result = result + File.separator;
		return result;
	}

	public final static String getPathStr_hasEnd(String aPath) {
		String result;
		result = getPathStr(aPath);
		if (!(result.endsWith("\\") || result.endsWith("/")))
			result = result + File.separator;
		return result;
	}
	
	public final static String getAddPathStr(String aPath,String aAdd){
		if (isBlank(aAdd)) return aPath;
		return getPathStr_hasEnd(aPath)+aAdd;
	}

	public final static  String getUrlMoudleName(String aUri, String aContentPath) {
		URI u;
		try {
			u = new URI(aUri);
			String fPath = u.getPath();

			if (aContentPath != null) {

				fPath = StringUtils.deleteHead(fPath, aContentPath);
				fPath = StringUtils.deleteHead(fPath, "/");
			}

			return fPath;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	public static final  String getCallMethodName(String aMethod, String aNameSpace){
		if (StringUtils.isBlank(aMethod)) return null;
		if (StringUtils.isBlank(aNameSpace)) return null;
		return aMethod.trim()+"@"+aNameSpace.trim();
	}
	public static final  String getCallMethodName_NameValue(INameValue aCallMethod){
		if (aCallMethod==null) return null;
		return getCallMethodName(aCallMethod.getItemName(),aCallMethod.getItemValue());
	}
	
}
