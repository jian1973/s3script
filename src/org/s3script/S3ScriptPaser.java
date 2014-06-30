package org.s3script;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.regex.*;

import org.s3script.IS3ScriptTag.ITagData;
import org.s3script.tag.*;
import org.songjian.utils.ExceptionUtils;
import org.songjian.utils.INameValue;
import org.songjian.utils.StringUtils;
import static org.songjian.utils.IConst.*;

/**
 * 脚本解析器,将HTML转换为Javascript的字符串脚本
 * 
 * @author Jian
 * 
 */
public class S3ScriptPaser implements IS3ScriptPaserCallback {
	public final static int C_iState_Text = 0;
	public final static int C_iState_Flage = 1;
	public final static int C_iState_Expression = 2;
	public final static int C_iState_Script = 3;
	public final static int C_iState_TagHead = 4;
	public final static int C_iState_TagEnd = 5;
	public final static int C_iState_TagHead_AttValue1 = 6;
	public final static int C_iState_TagHead_AttValue2 = 7;
	public final static int C_iState_Comment = 8;

	public final static String C_sFunctionHead = "%s = function(%s){";
	public final static String C_sFunctionEnd = "return %s.join(\"\");\r\n};";
	public final static String C_sNewArray = "var %s=new Array();";
	public final static String C_sAddLineLn = "%s.push(\"%s\\r\\n\");";
	public final static String C_sAddLine = "%s.push(\"%s\");";
	public final static String C_sAddResultPushLine = "%s.push(%s);";
	public final static String C_sResultData = "%s=%s.join(\"\");";
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

	private HashMap<String, INameValue> fCallmethod;

	private IS3ScriptParserContext fFinder;
	private Properties fPrperties;
	
	private int fLineNo;
	

	public HashMap<String, INameValue> getCallmethod() {
		return fCallmethod;
	}

	public S3ScriptPaser() {
		fDHtmlTagHash = new HashMap<String, IS3ScriptTag>();
		fTagStack = new Stack<ITagData>();
		fCallmethod = new HashMap<String, INameValue>();
		fPrperties = new Properties();
		regTag(new IfTag());
		regTag(new ElseTag());
		regTag(new ForTag());
		regTag(new ForeachTag());
		regTag(new CallTag());
		regTag(new JsTag());
		regTag(new SwitchTag());
		regTag(new CaseTag());
		regTag(new DefaultTag());
		fLineNo=C_iZero;

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

	public final int getLineNo(){
		return fLineNo;
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
		if (aStr == "")
			return;
		if (skipPushBlank)
			if (StringUtils.isBlank(aStr))
				return;
		if (fTrim)
			aStr = aStr.trim();

		if (fSkipPushLn || aSkip) {
			if (aStr.equals(""))
				return;
			fWriter.println(String.format(C_sAddLine, fResultValuename, aStr));
		} else

			fWriter.println(String.format(C_sAddLineLn, fResultValuename, aStr));
	}

	private void putToOutText(String aStr) {
		putToOutText_SkipLn(aStr, false);
	};

	private void putToOutScript(String aStr) {
		if (skipPushBlank)
			if (StringUtils.isBlank(aStr))
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

	private int convertText_Tag(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1, p2;
		ITagData fTagData;
		p1 = aCharArray[aIndex];

		p2 = getArrayCharS(aCharArray, aIndex + 1);
		// 检查为内嵌Javascript脚本
		if (p2 == '%') {
			putToOutText_SkipLn(aFb.toString(), true);
			aFb.clear();
			state = C_iState_Script;
			aIndex++;
			return aIndex;
		}

		if (p2 == '!') {
			if (getArrayCharS(aCharArray, aIndex + 2) == '-' && getArrayCharS(aCharArray, aIndex + 3) == '-') {
				putToOutText_SkipLn(aFb.toString(), true);
				aFb.clear();
				state = C_iState_Comment;
				aIndex += 3;
				return aIndex;
			}

		}

		// 检查为注册标签的头
		fTagData = isTagHead(aCharArray, aIndex + 1);
		if (fTagData != null) {
			putToOutText_SkipLn(aFb.toString(), true);
			aFb.clear();

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
		case 34:
		case 39:
			aFb.append("\\");
			aFb.append(p1);
			break;
		case 9:
			aFb.append("\\t");
			break;
		case 10:
			aFb.append("\\n");
			break;
		case 11:
			aFb.append("\\v");
			break;
		case 12:
			aFb.append("\\f");
			break;
		case 13:
			aFb.append("\\r");
			break;
		case '$':
			state = C_iState_Flage;
			break;
		case '<':
			aIndex = convertText_Tag(aCharArray, aIndex, aFb, aBuffer);
			break;

		default:
			aFb.append(p1);
			break;
		}
		return aIndex;
	}

	public int ConvertFlage(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1;
		p1 = aCharArray[aIndex];
		switch (p1) {
		case '{':
			aBuffer.clear();
			state = C_iState_Expression;
			break;
		default:
			aFb.append('$');
			aFb.append(p1);
			state = C_iState_Text;
			break;
		}
		return aIndex;
	}

	private int ConvertScript(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		char p1, p2;
		p1 = aCharArray[aIndex];
		switch (p1) {
		case '%':
			p2 = getArrayCharS(aCharArray, aIndex + 1);
			if (p2 == '>') {
				putToOutScript(aFb.toString());
				aFb.clear();
				state = C_iState_Text;
				aIndex++;
			} else
				aFb.append(p1);
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

	public final static String C_sHead_H = "h:";
	public final static String C_sHead_V = "v:";

	private final void endOfExpression(String aExpression, S3StringBuilder aFb) {
		if (StringUtils.isBlank(aExpression))
			return;
		// 标准模式
		if (!fFinder.isSecurityMode()) {
			aFb.append("\"+(" + aExpression + ")+\"");
			return;
		}

		// 安全模式
		if (aExpression.startsWith(C_sHead_V)) {
			aExpression = StringUtils.deleteHead(aExpression, C_sHead_V);
			aFb.append(String.format("\"+s3utils.c2v(%s)+\"", aExpression));
			return;

		} else if (aExpression.startsWith(C_sHead_H)) {
			aExpression = StringUtils.deleteHead(aExpression, C_sHead_H);
			aFb.append(String.format("\"+s3utils.c2h(%s)+\"", aExpression));
			return;
		}
		ExceptionUtils.raiseExceptionFormat("In security mode mast start with %s or %s.but expression is '%s'", C_sHead_H, C_sHead_V, aExpression);

	}

	private void ConvertExpression(char[] aCharArray, int aIndex, S3StringBuilder aFb, S3StringBuilder aBuffer) {
		String ftemp;
		char p1;
		p1 = aCharArray[aIndex];
		switch (p1) {
		case '}':
			ftemp = aBuffer.toString();
			endOfExpression(ftemp, aFb);
			aBuffer.clear();
			state = C_iState_Text;
			break;

		default:
			aBuffer.append(p1);
			break;
		}
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
			if (fTagData.getOwnerTag().isResultPush()) {
				ftemp = fTagData.getOwnerTag().exportHead(fTagData, this);

				putToOutScript(String.format(C_sAddResultPushLine, fResultValuename, ftemp));
				// aFb.append();
			} else {
				ftemp = aFb.toString();
				if (StringUtils.isNoBlank(ftemp))
					putToOutText(aFb.toString());
				aFb.clear();
				ftemp = fTagData.getOwnerTag().exportHead(fTagData, this);
				putToOutScript(ftemp);
			}
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

			if (fTagData.getOwnerTag().isResultPush()) {
				ftemp = fTagData.getOwnerTag().exportEnd(fTagData, this);
				aFb.append(String.format(C_sAddResultPushLine, fResultValuename, ftemp));
			} else {
				ftemp = aFb.toString();
				if (StringUtils.isNoBlank(ftemp))
					putToOutText(aFb.toString());
				aFb.clear();
				ftemp = fTagData.getOwnerTag().exportEnd(fTagData, this);
				putToOutScript(ftemp);
			}
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
			case C_iState_Flage:
				i = ConvertFlage(fTemp, i, fb, buffer);
				break;
			case C_iState_Expression:
				ConvertExpression(fTemp, i, fb, buffer);
				break;
			case C_iState_Script:
				isDirctOutPut = true;
				i = ConvertScript(fTemp, i, fb, buffer);
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
		if (state != C_iState_Text) {
			if (state == C_iState_Flage)
				fb.append('$');
			else if (state == C_iState_Expression) {
				fb.append("${");
				fb.append(buffer.toString());

			}
		}

		return fb.toString();
	}

	private void createReaderWriter(Reader aReader, Writer aWriter) {
		if (fReader == null)
			fReader = new BufferedReader(aReader);
		if (fWriter == null)
			fWriter = new PrintWriter(aWriter);

	}

	private final void ConvertBody(Reader aReader, Writer aWriter) throws IOException {
		String fLine;
		String fConverted;
		state = C_iState_Text;
		skipPushBlank = false;
		fLineNo=C_iZero;
		putToOutScript(String.format(C_sNewArray, fResultValuename));
		do {
			fLine = fReader.readLine();
			fLineNo++;
			fConverted = ConvertLine(fLine);

			if (fConverted != null) {
				if (isDirctOutPut) {
					if (state != C_iState_Comment)
						putToOutScript(fConverted);
				} else
					putToOutText(fConverted);
			}
		} while (fLine != null);
		fWriter.flush();
		boolean flage;
		flage = fTagStack.size() == C_iZero;
		if (!flage)
			ExceptionUtils.assertFormat(flage, "TagStack is Not null,tagName '%s'", fTagStack.pop().getOwnerTag().getTagName());

	}

	public void doConvert(Reader aReader, Writer aWriter) throws IOException {
		createReaderWriter(aReader, aWriter);
		ConvertBody(aReader, aWriter);
	}

	private String getFunctionParamters(String[] aParamters) {
		if (aParamters == null || aParamters.length <= 0)
			return "";
		String result = "";
		for (String fParamName : aParamters) {
			if (fParamName == null || fParamName.trim() == "")
				continue;
			if (!result.equalsIgnoreCase(""))
				result += ",";
			result += fParamName;
		}
		return result;
	}

	public void doConvertFunction(String aFunctionName, String[] aParamters, Reader aReader, Writer aWriter, IS3ScriptParserContext aFinder) throws IOException {
		fFinder = aFinder;
		createReaderWriter(aReader, aWriter);
		try {
			fCallmethod.clear();
			putToOutScript(String.format(C_sFunctionHead, aFunctionName, getFunctionParamters(aParamters)));
			doConvert(aReader, aWriter);

			putToOutScript(String.format(C_sFunctionEnd, fResultValuename));
			fWriter.flush();
		} finally {
		}

	}

	public static void main(String[] args) {
		FileReader fReader;
		FileWriter fWriter;
		S3ScriptPaser fPaser;
		try {
			fReader = new FileReader("C:\\forbackup\\test.html");
			fWriter = new FileWriter("C:\\forbackup\\test.js");
			try {
				fPaser = new S3ScriptPaser();

				fPaser.doConvertFunction("nju.getFlowInfo", new String[] { "aIsNull", "aId", "aName" }, fReader, fWriter, null);
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

	@Override
	public void pushCallMethod(INameValue aCallMethod) {
		String fKey;
		fKey = StringUtils.getCallMethodName_NameValue(aCallMethod);
		if (fKey == null)
			return;
		fCallmethod.put(fKey, aCallMethod);
	}

	public final String getCallImport() {
		StringBuilder fb;
		fb = new StringBuilder();
		for (INameValue fItem : fCallmethod.values()) {
			if (fItem == null)
				continue;
			fb.append(StringUtils.getCallMethodName_NameValue(fItem));
			fb.append("=");
			IS3ScriptTemplate ftemplet;
			ftemplet = fFinder.findTemplate(fItem.getItemName(), fItem.getItemValue());
			if (ftemplet != null)
				fb.append(StringUtils.getCallMethodName(ftemplet.getName(), ftemplet.getPackage()));

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
		result = new INameValue[fCallmethod.size()];
		result = fCallmethod.values().toArray(result);
		return result;
	}

	@Override
	public void pushDomTag(IS3DomTagCall aTagCall) {
		ExceptionUtils.raiseException("no support method 'pushDomTag()'");
		
	}

	@Override
	public List<IS3DomTagCall> getDomTagList() {
		ExceptionUtils.raiseException("no support method 'getdomTagList()'");
		return null;
	}

}
