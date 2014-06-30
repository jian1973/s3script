package org.songjian.utils;

import java.io.*;
import java.util.Properties;
import static org.songjian.utils.IConst.*;

public class StreamUtils {
	private final static String C_sCharSet_UTF8 = "UTF-8";

	public static String readResourceText_UTF8(String aResourceName) {
		return readResourceText(aResourceName, C_sCharSet_UTF8, true);
	}

	public static Properties readResourceProperties_UTF8(String aResourceName) {
		Properties fResult;
		String fText;
		fText = readResourceText_UTF8(aResourceName);
		if (fText == null)
			return null;
		StringReader fReader = new StringReader(fText);
		fResult = new Properties();
		try {
			fResult.load(fReader);
		} catch (IOException e) {
			return null;
		}
		return fResult;
	}

	public static byte[] readBinaryStream(InputStream aStream) throws IOException {
		ByteArrayOutputStream ftemp;
		try {
			byte[] fBuffer = new byte[C_iBuffer];
			ftemp = new ByteArrayOutputStream(C_iBuffer);
			int flen;
			do {
				flen = aStream.read(fBuffer, 0, fBuffer.length);
				if (flen > 0)
					ftemp.write(fBuffer, 0, flen);
			} while (flen >= 0);
			return ftemp.toByteArray();
		} finally {
			aStream.close();
		}
	}

	private static StreamUtils fObj;

	private static void initObj() {
		if (fObj == null)
			fObj = new StreamUtils();
	}

	/**
	 * 读取Class路径中的文件流,读取出错返回null,错误信息,从fLastErrorMsg中获取
	 * 
	 * @param aResourceName
	 *            类名,如com/seeyon/airappserver/common/temp.properties
	 * @return
	 */
	public static byte[] readResourceData(String aResourceName) {
		ExceptionUtils.clearLastError();
		String fErrorPos = null;
		initObj();
		try {
			InputStream fin = null;
			try {
				fErrorPos = "C4C1A5D8-384E-4FA7-AF1F-9F17BF212092";
				fin = fObj.getClass().getClassLoader().getResourceAsStream(aResourceName);
				if (fin == null) {
					return null;
				}
				byte[] ftemp = new byte[C_iBuffer];
				ByteArrayOutputStream fbyteout = new ByteArrayOutputStream(C_iBuffer);
				int flen = 0;
				while (flen >= 0) {
					flen = fin.read(ftemp);
					if (flen > 0) {
						fbyteout.write(ftemp, 0, flen);
					}
				}
				ftemp = fbyteout.toByteArray();
				return ftemp;
			} finally {
				fErrorPos = "CAA102FB-55FE-4272-ABD2-1481DD04BF2C";
				if (fin != null)
					fin.close();

			}
		} catch (Exception ex) {
			ExceptionUtils.setLastErrorMsg(ex.getMessage(), fErrorPos);
			return null;
		}
	}

	public static InputStream readResourceStream(String aResourceName) {
		ExceptionUtils.clearLastError();
		initObj();
		return fObj.getClass().getClassLoader().getResourceAsStream(aResourceName);

	}

	public static Reader getResourceReader(String aResourceName, String aCharSet) throws UnsupportedEncodingException {
		Reader result;
		ExceptionUtils.clearLastError();
		initObj();
		InputStream fin;
		fin = fObj.getClass().getClassLoader().getResourceAsStream(aResourceName);
		if (fin == null)
			return null;
		result = new InputStreamReader(fin, aCharSet);
		return result;
	}

	public static String readStreamText(InputStream aStream, String aCharSet, boolean aBOM_SkipUTF8) {
		InputStreamReader freader;
		ExceptionUtils.clearLastError();
		String fErrorPos = null;
		initObj();
		InputStream fin;
		fin = aStream;
		if (fin == null)
			return null;
		try {
			fErrorPos = "305BF691-87C4-45A0-B890-8CCF0CBBB538";
			freader = new InputStreamReader(fin, aCharSet);
			StringBuilder result = new StringBuilder();
			boolean aReadBOM;
			aReadBOM = aBOM_SkipUTF8;
			try {
				char[] fBuffer = new char[C_iBuffer];
				int flen;

				do {
					flen = freader.read(fBuffer);

					if ((flen > 0) && aReadBOM) {
						if (result.length() == 0) {
							// 去掉UTF-8的BOM头
							if (fBuffer[0] == 0xFEFF) {
								result.append(fBuffer, 1, flen - 1);
								continue;
							}
						}

						result.append(fBuffer, 0, flen);
					}

				} while (flen >= 0);
			} finally {
				fErrorPos = "F0BE0AC7-46D7-4339-A0C1-BE401688A41F";
				freader.close();
			}
			return result.toString();
		} catch (Exception ex) {
			ExceptionUtils.setLastErrorMsg(ex.getMessage(), fErrorPos);
			return null;
		}

	}

	public static String readResourceText(String aResourceName, String aCharSet, boolean aBOM_SkipUTF8) {
		InputStreamReader freader;
		ExceptionUtils.clearLastError();
		String fErrorPos = null;
		initObj();
		InputStream fin;
		fin = fObj.getClass().getClassLoader().getResourceAsStream(aResourceName);
		if (fin == null)
			return null;
		try {
			fErrorPos = "305BF691-87C4-45A0-B890-8CCF0CBBB538";
			freader = new InputStreamReader(fin, aCharSet);
			StringBuilder result = new StringBuilder();
			boolean aReadBOM;
			aReadBOM = aBOM_SkipUTF8;
			try {
				char[] fBuffer = new char[C_iBuffer];
				int flen;

				do {
					flen = freader.read(fBuffer);

					if ((flen > 0) && aReadBOM) {
						if (result.length() == 0) {
							// 去掉UTF-8的BOM头
							if (fBuffer[0] == 0xFEFF) {
								result.append(fBuffer, 1, flen - 1);
								continue;
							}
						}

						result.append(fBuffer, 0, flen);
					}

				} while (flen >= 0);
			} finally {
				fErrorPos = "F0BE0AC7-46D7-4339-A0C1-BE401688A41F";
				freader.close();
			}
			return result.toString();
		} catch (Exception ex) {
			ExceptionUtils.setLastErrorMsg(ex.getMessage(), fErrorPos);
			return null;
		}

	}

	public static String getPackageResource(@SuppressWarnings("rawtypes") Class aClass, String aResourceName) {
		String fResourceName;
		fResourceName = aClass.getPackage().getName().replace('.', '/') + "/resource/%s.properties";

		fResourceName = String.format(fResourceName, aResourceName);
		String result;
		result = readResourceText(fResourceName, StringConvertUtils.getJavaStringCharset(), true);
		ExceptionUtils.checkNull(result, "SQL:" + aResourceName);
		return result;
	}

	public static void writeByteArrayToFile(byte[] aData, String aFileName) throws IOException {
		FileOutputStream fOut;
		fOut = new FileOutputStream(aFileName);
		try {
			fOut.write(aData);
			fOut.flush();
		} finally {
			fOut.close();
		}
	}
	
	public final static void writeFromReader(Reader aReader,Writer aWriter) throws IOException{
		char[] fBuffer;
		int fLen;
		fBuffer=new char[C_iBuffer];
		do{
			fLen=aReader.read(fBuffer);
			if (fLen>C_iZero)
				aWriter.write(fBuffer, C_iZero, fLen);
				
		}while (fLen<C_iZero);
	}

	public static void writeStringToFile(String aFileName, String aData, String aCharset) throws IOException {
		FileOutputStream fOut;
		fOut = new FileOutputStream(aFileName);
		Writer fWriter = new OutputStreamWriter(fOut, aCharset);
		try {
			fWriter.write(aData);
			fWriter.flush();
		} finally {
			fWriter.close();
			fOut.close();
		}

	}

	public static Writer createFileWriter(File aFile, String aCharset) throws IOException {
		FileOutputStream fOut;
		fOut = new FileOutputStream(aFile);
		Writer result;
		if (aCharset != null)
			result = new OutputStreamWriter(fOut, aCharset);
		else
			result = new OutputStreamWriter(fOut);
		return result;

	}

	public static Writer createFileWriter(String aFileName, String aCharset) throws IOException {
		FileOutputStream fOut;
		fOut = new FileOutputStream(aFileName);
		Writer result;
		if (aCharset != null)
			result = new OutputStreamWriter(fOut, aCharset);
		else
			result = new OutputStreamWriter(fOut);
		return result;

	}

	public static byte[] readBinaryData(String aFileName) throws IOException {
		ByteArrayOutputStream ftemp;
		FileInputStream fStream;
		fStream = new FileInputStream(aFileName);
		try {
			byte[] fBuffer = new byte[C_iBuffer];
			ftemp = new ByteArrayOutputStream(C_iBuffer);
			int flen;
			do {
				flen = fStream.read(fBuffer, 0, fBuffer.length);
				if (flen > 0)
					ftemp.write(fBuffer, 0, flen);
			} while (flen >= 0);
			return ftemp.toByteArray();
		} finally {
			fStream.close();
		}
	}
	

	public static BufferedReader createFileReader(String aFileName, String aCharset) throws IOException {
		BufferedReader result;
		FileInputStream fStream = new FileInputStream(aFileName);
		result = new BufferedReader(new InputStreamReader(fStream, aCharset));
		return result;
	}
	

}
