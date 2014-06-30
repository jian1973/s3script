package org.s3script;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.songjian.utils.INameValue;
import org.songjian.utils.NullWriter;
import org.songjian.utils.StringUtils;
import org.songjian.utils.json.IJSonIEObject;

import com.alibaba.fastjson.JSONObject;

public class S3ScriptTemplate implements IJSonIEObject, IS3ScriptTemplate {

	private FileReader fFileReader;

	private BufferedReader fBodyReader;

	private String fId;
	private String fName;
	private String fFileName;
	private String fFuncName;
	private String fParamters;
	private boolean fSkipPushLn;
	private boolean fTrim;

	private String fNamespace;
	private String fTestParam;
	private String fImport;
	private String fPackage;

	public String getExportFileName(IS3ScriptParserContext aFinder) {

		return aFinder.getNamespaceFileName(fNamespace);
	}

	@Override
	public String getNamespace() {
		return fNamespace;
	}

	@Override
	public String getPackage() {
		return fPackage;
	}

	@Override
	public void setPackage(String aPackage) {
		this.fPackage = aPackage;
	}

	@Override
	public void setNamespace(String aNamespace) {
		this.fNamespace = aNamespace;
	}

	@Override
	public String getTestParam() {
		return fTestParam;
	}

	@Override
	public void setTestParam(String aTestParam) {
		this.fTestParam = aTestParam;
	}

	@Override
	public String getImport() {
		return fImport;
	}

	@Override
	public void setImport(String aImport) {
		this.fImport = aImport;
	}

	@Override
	public boolean getSkipPushLn() {
		return fSkipPushLn;
	}

	@Override
	public void setSkipPushLn(boolean aSkipPushLn) {
		this.fSkipPushLn = aSkipPushLn;
	}

	@Override
	public boolean getTrim() {
		return fTrim;
	}

	@Override
	public void setTrim(boolean aTrim) {
		this.fTrim = aTrim;
	}

	@Override
	public String getFuncName() {
		return fFuncName;
	}

	@Override
	public String getParamters() {
		return fParamters;
	}

	@Override
	public void setParamters(String aParamters) {
		if (StringUtils.isNoBlank(aParamters))
			S3ScriptPaser.checkString(aParamters, C_sRegex_funcParam, C_sHeadValue_Funcparam);
		this.fParamters = aParamters;
	}

	@Override
	public BufferedReader getBodyReader() {
		return fBodyReader;
	}

	@Override
	public void setBodyReader(BufferedReader aBodyReader) {
		this.fBodyReader = aBodyReader;
	}

	private final void setFuncName(String aFuncName) {
		S3ScriptPaser.checkString(aFuncName, C_sRegex_funcName, C_sHeadValue_Funcname);
		this.fFuncName = aFuncName;
	}

	@Override
	public String getId() {
		return fId;
	}

	@Override
	public void setId(String aId) {
		this.fId = aId;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public void setName(String aName) {
		this.fName = aName;
	}

	@Override
	public String getFileName() {
		return fFileName;
	}

	@Override
	public void setFileName(String aFileName) {
		this.fFileName = aFileName;
	}

	private final void readTemplateParam(INameValue aNameValue) {
		if (aNameValue == null)
			return;
		if (aNameValue.getItemName().equalsIgnoreCase(C_sHeadValue_Id))
			fId = aNameValue.getItemValue().trim();
		else if (aNameValue.getItemName().equalsIgnoreCase(C_sHeadValue_Name))
			fName = aNameValue.getItemValue().trim();
		else if (aNameValue.getItemName().equalsIgnoreCase(C_sHeadValue_Funcname))
			setFuncName(aNameValue.getItemValue());
		else if (aNameValue.getItemName().equalsIgnoreCase(C_sHeadValue_Funcparam))
			setParamters(aNameValue.getItemValue());
		else if (aNameValue.getItemName().equalsIgnoreCase(C_sHeadValue_Skippushln))
			fSkipPushLn = aNameValue.getItemValue().trim().equalsIgnoreCase("true");
		else if (aNameValue.getItemName().equalsIgnoreCase(C_sHeadValue_Trim))
			fTrim = aNameValue.getItemValue().trim().equalsIgnoreCase("true");
		else if (aNameValue.getItemName().equalsIgnoreCase(C_sHeadValue_Namespace))
			fNamespace = aNameValue.getItemValue().trim();
		else if (aNameValue.getItemName().equalsIgnoreCase(C_sHeadValue_Package))
			fPackage = aNameValue.getItemValue().trim();
	}

	private final void readHead() throws IOException {
		String fLine;
		INameValue fNameValue;
		clear();
		StringBuilder fbImport, fbTestParam;
		int fState;
		fState = C_iState_None;
		fbImport = new StringBuilder();
		fbTestParam = new StringBuilder();
		do {
			fLine = fBodyReader.readLine();
			if (fLine == null)
				break;

			if (fLine.equalsIgnoreCase(C_sHead_Template)) {
				fState = C_iState_Template;
				continue;
			} else if (fLine.equalsIgnoreCase(C_sHead_Import)) {
				fState = C_iState_Import;
				continue;
			} else if (fLine.equalsIgnoreCase(C_sHead_Test)) {
				fState = C_iState_Test;
				continue;
			} else if (fLine.equalsIgnoreCase(C_sHead_Body)) {
				fState = C_iState_Body;
				break;
			}

			switch (fState) {
			case C_iState_Template:
				fNameValue = StringUtils.splitNameValue_NameValue(fLine);
				readTemplateParam(fNameValue);
				break;
			case C_iState_Import:
				if (StringUtils.isNoBlank(fLine))
					fbImport.append(fLine + "\r\n");
				break;
			case C_iState_Test:
				if (StringUtils.isNoBlank(fLine))
					fbTestParam.append(fLine + "\r\n");
				break;
			case C_iState_Body:
			case C_iState_None:
			default:
				break;
			}

		} while (fLine != null);

		if (StringUtils.isBlank(fFuncName)) {
			throw new RuntimeException("template function Name is Null!");
		}
		fImport = fbImport.toString();
		fTestParam = fbTestParam.toString();
	}

	@Override
	public final void init(String aFileName) throws IOException {
		fFileReader = new FileReader(aFileName);
		try {
			fBodyReader = new BufferedReader(fFileReader);
			readHead();
		} catch (Exception ex) {
			if (fFileReader != null)
				fFileReader.close();
			fFileReader = null;
			throw ex;
		}
	}

	/**
	 * 函数名的正则表达式,允许带.
	 */
	private final static String C_sRegex_funcName = "^[a-zA-Z_]([\\.]{0,1}[a-zA-Z0-9_]{1,}){0,}$";
	/**
	 * 参数的正则表达式,允许用,分割
	 */
	private final static String C_sRegex_funcParam = "^[a-zA-Z_]([\\,]{0,1}[a-zA-Z0-9_]{1,}){0,}$";

	// "^[a-zA-Z_][a-zA-Z0-9_]*$";

	@Override
	public void closeit() throws IOException {
		if (fFileReader != null) {
			fFileReader.close();
			fFileReader = null;
		}
		fBodyReader = null;
	}

	private String[] getParamterArray() {
		if (fParamters == null)
			return null;
		else
			return fParamters.split(",");
	}

	@Override
	public void doConvertTemplate(Writer aFileWriter, IS3ScriptParserContext aFinder) throws IOException {
		runConvert(aFileWriter, aFinder);
	}

	private S3ScriptPaser runConvert(Writer aFileWriter, IS3ScriptParserContext aFinder) throws IOException {
		S3ScriptPaser fPaser;
		String fFuncName_Convert;
		fLastErrorMsg = null;
		if (StringUtils.isBlank(fNamespace))
			fFuncName_Convert = fFuncName;
		else
			fFuncName_Convert = fNamespace.trim() + "." + fFuncName.trim();

		fPaser = new S3ScriptPaser();
		fPaser.setReader(fBodyReader);
		fPaser.setSkipPushLn(fSkipPushLn);
		fPaser.setTrim(fTrim);
		try {
			fPaser.doConvertFunction(fFuncName_Convert, getParamterArray(), null, aFileWriter, aFinder);
		} catch (Exception ex) {
			fLastErrorMsg = String.format("function name=%s,line no=%d", fFuncName_Convert, fPaser.getLineNo());
			throw ex;
		}
		return fPaser;

	}

	public String fLastErrorMsg;

	public final String getLastErrorMsg() {
		return fLastErrorMsg;
	}


	public static void test1() throws IOException {
		IS3ScriptTemplate fTemplate;
		fTemplate = new S3ScriptTemplate();
		FileWriter fWriter;
		fWriter = new FileWriter("C:\\forbackup\\test.js");
		try {

			fTemplate.init("C:\\forbackup\\test.htmls");
			fTemplate.doConvertTemplate(fWriter, null);
			fWriter.flush();
			System.out.println("ok");
		} finally {
			fTemplate.closeit();
			fWriter.close();
		}

	}

	public static void main(String[] args) {
		try {
			test1();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void importFromJSon(JSONObject aJSON) {
		fId = aJSON.getString(C_sHeadValue_Id);
		fName = aJSON.getString(C_sHeadValue_Name);
		fFileName = aJSON.getString(C_sHeadValue_FileName);
		fFuncName = aJSON.getString(C_sHeadValue_Funcname);
		fParamters = aJSON.getString(C_sHeadValue_Funcparam);
		fSkipPushLn = aJSON.getBooleanValue(C_sHeadValue_Skippushln);
		fTrim = aJSON.getBooleanValue(C_sHeadValue_Trim);
		fNamespace = aJSON.getString(C_sHeadValue_Namespace);
		fTestParam = aJSON.getString(C_sHeadValue_Test);
		fImport = aJSON.getString(C_sHeadValue_Import);
		fPackage = aJSON.getString(C_sHeadValue_Package);

	}

	public JSONObject exportToJSon(JSONObject aJSONObject) {
		JSONObject result;
		if (aJSONObject == null)
			result = new JSONObject();
		else
			result = aJSONObject;

		result.put(C_sHeadValue_Id, fId);
		result.put(C_sHeadValue_Name, fName);
		result.put(C_sHeadValue_FileName, fFileName);
		result.put(C_sHeadValue_Funcname, fFuncName);
		result.put(C_sHeadValue_Funcparam, fParamters);
		result.put(C_sHeadValue_Skippushln, fSkipPushLn);
		result.put(C_sHeadValue_Trim, fTrim);
		result.put(C_sHeadValue_Namespace, fNamespace);
		result.put(C_sHeadValue_Test, fTestParam);
		result.put(C_sHeadValue_Import, fImport);
		result.put(C_sHeadValue_Package, fPackage);

		return result;
	}

	public void clear() {
		fId = null;
		fName = null;
		fFileName = null;
		fFuncName = null;
		fParamters = null;
		fSkipPushLn = false;
		fTrim = false;
		fNamespace = null;
		fTestParam = null;
		fImport = null;
		fPackage = null;
	}

	@Override
	public void parseCallMethod(IS3ScriptParserContext aFinder) throws IOException {
		NullWriter fWriter;
		fWriter = new NullWriter();
		S3ScriptPaser fPaser = runConvert(fWriter, aFinder);
		fImport = fPaser.getCallImport();

	}

}
