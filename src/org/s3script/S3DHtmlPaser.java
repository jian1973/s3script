package org.s3script;

import static org.songjian.utils.IConst.C_iZero;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.regex.Pattern;

import org.s3script.IS3ScriptTag.ITagData;
import org.s3script.tag.CallTag;
import org.s3script.tag.DomTag;
import org.s3script.tag.ImportTag;
import org.s3script.tag.InitFuncTag;
import org.s3script.web.WebInstance;
import org.songjian.utils.ExceptionUtils;
import org.songjian.utils.IConst;
import org.songjian.utils.INameValue;
import org.songjian.utils.NullWriter;
import org.songjian.utils.StreamUtils;
import org.songjian.utils.StringUtils;

public class S3DHtmlPaser implements IS3ScriptPaserCallback {
	public final static int C_iState_Text = 0;
	public final static int C_iState_TagHead = 4;
	public final static int C_iState_TagEnd = 5;
	public final static int C_iState_TagHead_AttValue1 = 6;
	public final static int C_iState_TagHead_AttValue2 = 7;
	public final static int C_iState_Comment = 8;

	public final static String C_sIF_Head = "?";
	public final static String C_sIF_Codition = ":";
	public final static String C_sIF_Split = ",";


	private String fResultValuename = "result";
	private int state;
	private boolean isDirctOutPut;

	private boolean skipPushBlank;

	private BufferedReader fReader;
	private PrintWriter fWriter;

	private boolean fDoTrim;

	private HashMap<String, IS3ScriptTag> fDHtmlTagHash;

	private Stack<ITagData> fTagStack;

	private boolean fSkipPushLn;

	private boolean fTrim;

	private IS3ScriptParserContext fFinder;

	private Properties fPrperties;

	private HashMap<String, INameValue> fCallMethod;
	
	private List<IS3DomTagCall> fDomTagCallList;

	public HashMap<String, INameValue> getCallmethod() {
		return fCallMethod;
	}

	public S3DHtmlPaser() {
		fDHtmlTagHash = new HashMap<String, IS3ScriptTag>();
		fTagStack = new Stack<ITagData>();
		fCallMethod = new HashMap<String, INameValue>();
		fPrperties = new Properties();
		fDomTagCallList=new ArrayList<IS3DomTagCall>();
		regTag(new ImportTag());
		regTag(new CallTag());
		regTag(new InitFuncTag());
		regTag(new DomTag());
	}

	public boolean getSkipPushLn() {
		return fSkipPushLn;
	}

	public void setSkipPushLn(boolean aSkipPushLn) {
		this.fSkipPushLn = aSkipPushLn;
	}

	public void setSkipPushBlank(boolean aValue) {
		skipPushBlank = aValue;
	}

	public boolean getTrim() {
		return fTrim;
	}

	public void setTrim(boolean aTrim) {
		this.fTrim = aTrim;
	}

	private final void regTag(IS3ScriptTag aTag) {
		if (aTag == null)
			return;
		fDHtmlTagHash.put(aTag.getTagName().toUpperCase(), aTag);
	}

	private final IS3ScriptTag getTag(String aName) {
		return fDHtmlTagHash.get(aName);
	}

	public BufferedReader getReader() {
		return fReader;
	}

	public void setReader(BufferedReader aReader) {
		this.fReader = aReader;
	}

	public PrintWriter getWriter() {
		return fWriter;
	}

	public void setWriter(PrintWriter aWriter) {
		this.fWriter = aWriter;
	}

	public boolean getDoTrim() {
		return fDoTrim;
	}

	public void setDoTrim(boolean aDoTrim) {
		this.fDoTrim = aDoTrim;
	}

	public String getResultValuename() {

		return fResultValuename;
	}

	public void setResultValuename(String aResultValuename) {
		this.fResultValuename = aResultValuename;
	}

	private void putToOutText_SkipLn(String aStr, boolean aSkip) {
		// if (StringUtils.isBlank(aStr)){
		// if (aStr==null) return ;
		// }

		if (skipPushBlank)
			if (StringUtils.isBlank(aStr))
				return;
		if (fTrim)
			aStr = aStr.trim();

		if (fSkipPushLn || aSkip)
			fWriter.print(aStr);
		else
			fWriter.println(aStr);

	}

	private void putToOutText(String aStr) {
		putToOutText_SkipLn(aStr, false);
	};

	private void putToOutScript(String aStr, boolean aSkipBlank) {
		if (skipPushBlank)
			if (StringUtils.isBlank(aStr))
				return;

		if (aSkipBlank && aStr.equals(IConst.C_sBlank))
			return;

		fWriter.print(aStr);
	}

	private void putToOutScriptLn(String aStr, boolean aSkipBlank) {
		if (skipPushBlank)
			if (StringUtils.isBlank(aStr))
				return;

		if (aSkipBlank && aStr.equals(IConst.C_sBlank))
			return;

		fWriter.println(aStr);
	}

	private final boolean isNameChar(char aChar) {
		if (aChar >= 'a' && aChar <= 'z')
			return true;
		if (aChar >= 'A' && aChar <= 'Z')
			return true;
		if (aChar >= '0' && aChar <= '9')
			return true;
		switch (aChar) {
		case '/':
		case ':':
			return true;
		}
		return false;

	}

	private final String readTag(char[] aCharArray, int aIndex) {
		int fLen;
		fLen = aCharArray.length;
		StringBuilder fb;
		fb = new StringBuilder();
		char p1;
		for (int i = aIndex; i < fLen; i++) {
			p1 = aCharArray[i];
			if (!isNameChar(p1))
				break;
			fb.append(p1);
		}
		return fb.toString().toUpperCase();
	}

	private ITagData isTagHead(char[] aCharArray, int aIndex) {
		String fTagName;
		fTagName = readTag(aCharArray, aIndex);
		IS3ScriptTag fTag = getTag(fTagName);
		if (fTag == null)
			return null;
		return fTag.newTagData();
	}

	private boolean isSameTagName(String aTagName1, String aTagName2) {
		if (aTagName1 == null && aTagName2 == null)
			return true;
		if (aTagName1 == null || aTagName2 == null)
			return true;
		return aTagName1.equalsIgnoreCase(aTagName2);
	}

	private ITagData isTagEnd(char[] aCharArray, int aIndex) {
		String fTagName;
		fTagName = readTag(aCharArray, aIndex);
		if (!fTagName.startsWith("/"))
			return null;
		fTagName = StringUtils.deleteHead(fTagName, "/");
		IS3ScriptTag fTag = getTag(fTagName);
		if (fTag == null)
			return null;
		ITagData result;
		result = getCurrentTagData();
		ExceptionUtils.assertFormat(result != null, "Tag stack is null,end tag Name is '%s'", fTagName);
		ExceptionUtils.assertFormat(isSameTagName(fTagName, result.getOwnerTag().getTagName()), "Tag stack is error,current tag is '%s' end tag is '%s'",
				result.getOwnerTag().getTagName(), fTagName);
		ExceptionUtils.assertFormat(!result.getOwnerTag().isSingle(), "is a Single tage not has end,tagname is '%s'", fTagName);
		return result;
	}

	private final int getTagHeadLen(ITagData aTagData) {
		if (aTagData == null)
			return C_iZero;

		return aTagData.getOwnerTag().getTagName().length();
	}

	private final void putToTagDataStack(ITagData aTagData) {
		if (aTagData == null)
			return;
		fTagStack.push(aTagData);
	}

	private final void popTagDataStack() {
		if (fTagStack.size() <= 0)
			return;
		fTagStack.pop();
	}

	private final ITagData getCurrentTagData() {
		if (fTagStack.size() <= 0)
			return null;
		return fTagStack.peek();
	}

	/**
	 * 安全的获取字符数组中的char
	 * 
	 * @param aCharArray
	 * @param aIndex
	 * @return
	 */
	private final char getArrayCharS(char[] aCharArray, int aIndex) {
		if (aIndex >= aCharArray.length || aIndex < 0)
			return C_iZero;
		return aCharArray[aIndex];
	}

	private void printInnerText(S3StringBuilder aFb) {
		String ftemp;
		ftemp = aFb.toString();
		aFb.clear();
		if (ftemp.equals(IConst.C_sBlank))
			return;
		putToOutText_SkipLn(ftemp, true);

	}

	private int convertText_Tag(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1;
		ITagData fTagData;
		p1 = aCharArray[aIndex];

		// 检查为内嵌Javascript脚本
		// char p2;
		// p2 = getArrayCharS(aCharArray, aIndex + 1);
		// if (p2 == '!') {
		// if (getArrayCharS(aCharArray, aIndex + 2) == '-' &&
		// getArrayCharS(aCharArray, aIndex + 3) == '-') {
		// putToOutText_SkipLn(aFb.toString(), true);
		// aFb.clear();
		// state = C_iState_Comment;
		// aIndex += 3;
		// return aIndex;
		// }
		//
		// }

		// 检查为注册标签的头
		fTagData = isTagHead(aCharArray, aIndex + 1);
		if (fTagData != null) {
			printInnerText(aFb);

			putToTagDataStack(fTagData);
			state = C_iState_TagHead;
			aIndex += getTagHeadLen(fTagData);
			return aIndex;
		}
		// 检查为注册标签的尾
		fTagData = isTagEnd(aCharArray, aIndex + 1);
		if (fTagData != null) {
			putToOutText_SkipLn(aFb.toString(), true);
			aFb.clear();
			state = C_iState_TagEnd;
			aIndex += getTagHeadLen(fTagData) + 1;// 多加一个/的长度
			return aIndex;
		}
		// 其它HTML标记
		aFb.append(p1);
		return aIndex;

	}

	private int ConvertText(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1;
		p1 = aCharArray[aIndex];
		switch (p1) {
		case '<':
			aIndex = convertText_Tag(aCharArray, aIndex, aFb, aBuffer);
			break;

		default:
			aFb.append(p1);
			break;
		}
		return aIndex;
	}

	public static final void checkString(String aStr, String aRegex, String aName) {
		if (!Pattern.matches(aRegex, aStr))
			ExceptionUtils.raiseException("error " + aName + " '" + aStr + "'");

	}

	private void putTagAttrib(S3StringBuilder aBuffer) {
		ITagData fTagData;
		fTagData = getCurrentTagData();
		fTagData.setAttData(aBuffer.toString());
		ExceptionUtils.assertFormat(fTagData != null, "currentTagData is Null");
		aBuffer.clear();
	}

	private int ConvertTagHead(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1;
		p1 = aCharArray[aIndex];
		ITagData fTagData;
		String ftemp;
		switch (p1) {
		case '>':
			fTagData = getCurrentTagData();
			putTagAttrib(aBuffer);
			ftemp = aFb.toString();
			if (StringUtils.isNoBlank(ftemp))
				putToOutText(aFb.toString());
			aFb.clear();
			ftemp = fTagData.getOwnerTag().exportHead(fTagData, this);
			putToOutScript(ftemp, true);
			if (fTagData.getOwnerTag().isSingle())
				popTagDataStack();
			state = C_iState_Text;
			break;
		case '"':
			state = C_iState_TagHead_AttValue1;
			break;
		case '\'':
			state = C_iState_TagHead_AttValue2;
			break;
		case ' ':
			putTagAttrib(aBuffer);
			break;
		default:
			aBuffer.append(p1);
			break;
		}
		// 本行最后一个字符,添加Attrib
		if (state != C_iState_Text && aCharArray.length == aIndex + 1)
			putTagAttrib(aBuffer);
		return aIndex;
	}

	private int ConvertTagHead_AttValue1(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1, p2;
		p1 = aCharArray[aIndex];

		switch (p1) {
		case '"':
			state = C_iState_TagHead;
			break;
		case '\\':
			p2 = getArrayCharS(aCharArray, aIndex + 1);
			switch (p2) {
			case '\\':
				aBuffer.append("\\");
				break;
			case 't':
				aBuffer.append("\t");
				break;
			case 'n':
				aBuffer.append("\n");
				break;
			case 'v':
				aBuffer.append((char) 11);
				break;
			case 'f':
				aBuffer.append((char) 12);
				break;
			case 'r':
				aBuffer.append("\r");
				break;
			case '"':
				aBuffer.append("\"");
				break;

			default:
				ExceptionUtils.raiseException("unkown char " + p1 + p2);
				break;
			}
			aIndex++;
			break;
		default:
			aBuffer.append(p1);
			break;
		}
		// 本行最后一个字符,添加Attrib
		if (aCharArray.length == aIndex + 1)
			putTagAttrib(aBuffer);

		return aIndex;
	}

	private int ConvertTagHead_AttValue2(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1, p2;
		p1 = aCharArray[aIndex];
		switch (p1) {
		case '\'':
			state = C_iState_TagHead;
			break;
		case '\\':
			p2 = getArrayCharS(aCharArray, aIndex + 1);
			switch (p2) {
			case '\\':
				aBuffer.append("\\");
				break;
			case 't':
				aBuffer.append("\t");
				break;
			case 'n':
				aBuffer.append("\n");
				break;
			case 'v':
				aBuffer.append((char) 11);
				break;
			case 'f':
				aBuffer.append((char) 12);
				break;
			case 'r':
				aBuffer.append("\r");
				break;
			case '\'':
				aBuffer.append("\'");
				break;

			default:
				ExceptionUtils.raiseException("unkown char " + p1 + p2);
				break;
			}
			aIndex++;
			break;

		default:
			aBuffer.append(p1);
			break;
		}
		// 本行最后一个字符,添加Attrib
		if (aCharArray.length == aIndex + 1)
			putTagAttrib(aBuffer);
		return aIndex;
	}

	private int ConvertComment(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1, p2, p3;
		p1 = aCharArray[aIndex];
		p2 = getArrayCharS(aCharArray, aIndex + 1);
		p3 = getArrayCharS(aCharArray, aIndex + 2);
		if (p1 == '-' && p2 == '-' && p3 == '>') {
			aBuffer.clear();
			aFb.clear();
			aIndex += 2;
			state = C_iState_Text;

		}
		return aIndex;

	}

	private int ConvertTagEnd(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1;
		p1 = aCharArray[aIndex];
		ITagData fTagData;
		String ftemp;
		switch (p1) {
		case '>':
			fTagData = getCurrentTagData();
			ExceptionUtils.assertFormat(fTagData != null, "currentTagData is Null");
			aBuffer.clear();

			ftemp = aFb.toString();
			if (StringUtils.isNoBlank(ftemp))
				putToOutText(aFb.toString());
			aFb.clear();
			ftemp = fTagData.getOwnerTag().exportEnd(fTagData, this);
			putToOutScriptLn(ftemp, true);
			popTagDataStack();
			state = C_iState_Text;
			break;

		default:
			aBuffer.append(p1);
			break;
		}

		return aIndex;
	}

	private final boolean isSkipLast(char[] aTemp, S3StringBuilder aFb, int aIndex) {
		return aTemp.length == (aIndex + 1) && aFb.length() <= 0;
	}

	public String ConvertLine(String aLine) {
		if (aLine == null)
			return null;
		S3StringBuilder fb;
		S3StringBuilder buffer;
		char[] fTemp;
		if (fDoTrim)
			fTemp = aLine.trim().toCharArray();
		else
			fTemp = aLine.toCharArray();
		if (fTemp.length <= 0)
			return "";
		isDirctOutPut = false;
		int fLen;
		fLen = fTemp.length;
		fb = new S3StringBuilder();
		buffer = new S3StringBuilder();
		int i = C_iZero;

		while (i < fLen) {
			switch (state) {
			case C_iState_Text:
				isDirctOutPut = false;
				i = ConvertText(fTemp, i, fb, buffer);
				break;
			case C_iState_TagHead:
				isDirctOutPut = true;
				i = ConvertTagHead(fTemp, i, fb, buffer);
				if (isSkipLast(fTemp, fb, i))
					return null;
				break;
			case C_iState_TagEnd:
				isDirctOutPut = true;
				i = ConvertTagEnd(fTemp, i, fb, buffer);
				if (isSkipLast(fTemp, fb, i))
					return null;
				break;
			case C_iState_TagHead_AttValue1:
				isDirctOutPut = true;
				i = ConvertTagHead_AttValue1(fTemp, i, fb, buffer);
				break;
			case C_iState_TagHead_AttValue2:
				isDirctOutPut = true;
				i = ConvertTagHead_AttValue2(fTemp, i, fb, buffer);
				break;
			case C_iState_Comment:
				isDirctOutPut = true;
				i = ConvertComment(fTemp, i, fb, buffer);
				break;
			default:
				break;
			}
			i++;
		}

		return fb.toString();
	}

	private void createReaderWriter(Reader aReader, Writer aWriter) {
		if (fReader == null)
			fReader = new BufferedReader(aReader);
		if (fWriter == null)
			fWriter = new PrintWriter(aWriter);

	}

	private final boolean notPrintBlankLine(String aLine, String aResult) {
		// 返回的不为空,需要打印
		if (aResult != null && aResult != "")
			return true;
		// 返回空,本身也是空行,需要打印
		if (aLine == null || aLine.equals(""))
			return true;
		return false;
	}

	private final void ConvertBody(Reader aReader, Writer aWriter) throws IOException {
		String fLine;
		String fConverted;
		state = C_iState_Text;
		skipPushBlank = false;
		do {
			fLine = fReader.readLine();
			fConverted = ConvertLine(fLine);

			if (fConverted != null) {
				if (isDirctOutPut) {
					if (state != C_iState_Comment)
						putToOutScriptLn(fConverted, true);
				} else {
					if (notPrintBlankLine(fLine, fConverted))
						putToOutText(fConverted);
				}
			}
		} while (fLine != null);
		fWriter.flush();
		boolean flage;
		flage = fTagStack.size() == C_iZero;
		if (!flage)
			ExceptionUtils.assertFormat(flage, "Tagstack is Not null,tagName '%s'", fTagStack.pop().getOwnerTag().getTagName());

	}

	public void doConvert(Reader aReader, Writer aWriter) throws IOException {
		createReaderWriter(aReader, aWriter);
		ConvertBody(aReader, aWriter);
	}

	
	public void doConvertHtml(Reader aReader, Writer aWriter, IS3ScriptParserContext aFinder,  boolean aExport) throws IOException {
		fFinder = aFinder;
		fPrperties.setProperty("EXPORT", Boolean.toString(aExport));
		StringReader fTempReader;
		Writer fTempWriter;
		fTempWriter=new StringWriter();
		StreamUtils.writeFromReader(aReader,fTempWriter);
		String ftemp;
		ftemp=fTempWriter.toString();
		fTempReader=new StringReader(ftemp);
		fTempWriter=new NullWriter();
		fCallMethod.clear();
		doConvert(fTempReader, fTempWriter);

		fReader=null;
		fWriter=null;
		fTempReader=new StringReader(ftemp);
		doConvert(fTempReader, aWriter);
		aWriter.flush();


	}

	public static void main(String[] args) {
		FileReader fReader;
		FileWriter fWriter;
		S3DHtmlPaser fPaser;
		try {
			fReader = new FileReader("C:\\forbackup\\test.htm");
			fWriter = new FileWriter("C:\\forbackup\\out.html");
			try {
				WebInstance.init("C:\\myCode\\Self\\workspace\\s3dhtmlscript\\WebContent\\");
				fPaser = new S3DHtmlPaser();

				fPaser.doConvertHtml(fReader, fWriter, WebInstance.ScriptMethodManagerInstance(), false);
				fWriter.flush();
				System.out.println("ok");
			} finally {
				fReader.close();
				fWriter.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static final String getCallMethodName(String aMethod, String aNameSpace) {
		if (StringUtils.isBlank(aMethod))
			return null;
		if (StringUtils.isBlank(aNameSpace))
			return null;
		return aMethod.trim() + "@" + aNameSpace.trim();
	}

	public static final String getCallMethodName_NameValue(INameValue aCallMethod) {
		if (aCallMethod == null)
			return null;
		return getCallMethodName(aCallMethod.getItemName(), aCallMethod.getItemValue());
	}

	@Override
	public void pushCallMethod(INameValue aCallMethod) {
		String fKey;
		fKey = getCallMethodName_NameValue(aCallMethod);
		if (fKey == null)
			return;
		fCallMethod.put(fKey, aCallMethod);
	}

	public final String getCallImport() {
		StringBuilder fb;
		fb = new StringBuilder();

		for (INameValue fItem : fCallMethod.values()) {
			if (fItem == null)
				continue;
			fb.append(getCallMethodName_NameValue(fItem));
			fb.append("=");
			IS3ScriptTemplate ftemplet;
			ftemplet = fFinder.findTemplate(fItem.getItemName(), fItem.getItemValue());
			if (ftemplet != null)
				fb.append(getCallMethodName(ftemplet.getName(), ftemplet.getPackage()));

			fb.append("\r\n");
		}
		return fb.toString();

	}

	@Override
	public IS3ScriptParserContext getDHtmlFinder() {
		return fFinder;
	}

	@Override
	public String getProperty(String aName) {
		return fPrperties.getProperty(aName.toUpperCase());
	}

	@Override
	public INameValue[] getCallMethodArray() {
		INameValue[] result;
		result = new INameValue[fCallMethod.size()];
		result = fCallMethod.values().toArray(result);
		return result;
	}

	@Override
	public void pushDomTag(IS3DomTagCall aTagCall) {
		pushCallMethod(aTagCall.getCallName());
		fDomTagCallList.add(aTagCall);
	}

	@Override
	public List<IS3DomTagCall> getDomTagList() {
		return fDomTagCallList;
	}

}
