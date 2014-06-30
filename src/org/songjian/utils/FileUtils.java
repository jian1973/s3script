package org.songjian.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.songjian.utils.IConst.*;

public class FileUtils {

	private static SimpleDateFormat fDateformat_File = new SimpleDateFormat("yyyyMMdd_HHmmss");

	public static String readTextFile(String aFileName) throws IOException {
		FileReader freader;
		freader = new FileReader(aFileName);
		StringBuilder result = new StringBuilder();
		try {
			char[] fBuffer = new char[8192];
			int flen;

			do {
				flen = freader.read(fBuffer);

				if (flen > 0) {
					if (result.length() == 0) {
						// 去掉UTF-8的BOM头,避免Json解析错误
						if (fBuffer[0] == 0xFEFF) {
							result.append(fBuffer, 1, flen - 1);
							continue;
						}
					}

					result.append(fBuffer, 0, flen);
				}

			} while (flen >= 0);
		} finally {
			freader.close();
		}
		return result.toString();
	}

	public interface IEnumFileAction {
		public boolean onEnumFile(File aFile) throws IOException;
	}

	private static  void doEnumPathFiles(File aPath, boolean aSubPath, IEnumFileAction aAction) throws IOException {
		if (!aPath.isDirectory())
			return;
		File[] fSubItem;
		if (!aPath.isDirectory()) return ;
		boolean fHasSubPath = false;
		fSubItem = aPath.listFiles();
		for (int i = 0; i < fSubItem.length; i++) {
			if (fSubItem[i] == null)
				continue;
			if (fSubItem[i].isDirectory())
				fHasSubPath = true;
			else
				if (!aAction.onEnumFile(fSubItem[i])) return ;
		}
		if (!aSubPath || !fHasSubPath) return ;
		for (int i = 0; i < fSubItem.length; i++) {
			if (fSubItem[i] == null)
				continue;
			if (fSubItem[i].isDirectory())
				doEnumPathFiles(fSubItem[i],aSubPath,aAction);
		}
	}

	public static void enumPathFiles(String aPath, Boolean aSubPath, IEnumFileAction aAction) throws IOException {
		File fPath;
		fPath = new File(aPath);
		doEnumPathFiles(fPath,aSubPath,aAction);

	}

	public static byte[] readBinaryFile(String aFileName) throws IOException {
		ByteArrayOutputStream ftemp;
		FileInputStream fin;
		fin = new FileInputStream(aFileName);
		try {
			byte[] fBuffer = new byte[8192];
			ftemp = new ByteArrayOutputStream(8192);
			int flen;
			do {
				flen = fin.read(fBuffer, 0, fBuffer.length);
				if (flen > 0)
					ftemp.write(fBuffer, 0, flen);
			} while (flen >= 0);
			return ftemp.toByteArray();
		} finally {
			fin.close();
		}
	}

	public static boolean hasExt(String aFileName) {
		if (aFileName == null)
			return false;
		String pExt = getExt(aFileName);
		return StringUtils.isNoBlank(pExt);
	}

	public static String changeFileExt(String aFileName, String aExt) {
		if (aFileName == null)
			return null;
		String result;
		String pExt;
		if (StringUtils.isBlank(aExt))
			pExt = C_sBlank;
		else
			pExt = "." + aExt;
		int pPos = aFileName.lastIndexOf('.');
		if (pPos < IConst.C_iZero)
			result = aFileName;
		else
			result = aFileName.substring(IConst.C_iZero, pPos);
		return String.format("%s%s", result, pExt);

	}

	public static String getExt(String aFileName) {
		if (aFileName == null)
			return null;
		int pPos = aFileName.lastIndexOf('.');
		if (pPos < IConst.C_iZero)
			return C_sBlank;
		String pExt;
		int pLen;
		pLen = aFileName.length();
		pExt = aFileName.substring(pPos + 1, pLen);
		return pExt;

	}

	public static void main(String[] agvs) {
		System.out.println(changeFileExt("test.html", "js"));
		System.out.println(changeFileExt("test.html", null));

		System.out.println(hasExt("test.html"));
		System.out.println(hasExt("test."));
		System.out.println(hasExt("test"));

	}

	public static File getNowDataFile(String aPath,String aExt){
		String fFileHead=fDateformat_File.format(new Date());
		String fFileName;
		File fFile;
		int i=C_iZero;
		fFile=new File(StringUtils.getPathStr_hasEnd(aPath));
		if (!fFile.exists())
			fFile.mkdirs();
		do {
			fFileName=StringUtils.getPathStr_hasEnd(aPath)+fFileHead;
			if (i==C_iZero)
				fFileName=fFileName+aExt;
			else
				fFileName=fFileName+i+aExt;
			fFile=new File(fFileName);
			i++;
			
		} while (fFile.exists());
		return fFile;
	}
}
