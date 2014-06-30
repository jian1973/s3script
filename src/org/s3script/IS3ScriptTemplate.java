package org.s3script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public interface IS3ScriptTemplate {

	public final static String C_sHead_Template = "[template]";
	public final static String C_sHead_Import = "[import]";
	public final static String C_sHead_Test = "[test]";
	public final static String C_sHead_Body = "[body]";
	public final static int C_iState_None = 0;
	public final static int C_iState_Template = 1;
	public final static int C_iState_Import = 2;
	public final static int C_iState_Test = 3;
	public final static int C_iState_Body = 4;
	public static final String C_sHeadValue_Id = "id";
	public static final String C_sHeadValue_Name = "name";
	public static final String C_sHeadValue_FileName = "fileName";
	public final static String C_sHeadValue_Funcname = "funcname";
	public final static String C_sHeadValue_Funcparam = "funcparam";
	public final static String C_sHeadValue_Skippushln = "skippushln";
	public final static String C_sHeadValue_Trim = "trim";
	public final static String C_sHeadValue_Namespace = "namespace";
	public final static String C_sHeadValue_Package = "package";
	public final static String C_sHeadValue_Test = "test";
	public static final String C_sHeadValue_Import = "import";

	public String getNamespace();

	public String getPackage();

	public void setPackage(String aPackage);

	public void setNamespace(String aNamespace);

	public String getTestParam();

	public void setTestParam(String aTestParam);

	public String getImport();

	public void setImport(String aImport);

	public boolean getSkipPushLn();

	public void setSkipPushLn(boolean aSkipPushLn);

	public boolean getTrim();

	public void setTrim(boolean aTrim);

	public String getFuncName();

	public String getParamters();

	public void setParamters(String aParamters);

	public BufferedReader getBodyReader();

	public void setBodyReader(BufferedReader aBodyReader);

	public String getId();

	public void setId(String aId);

	public String getName();

	public void setName(String aName);

	public String getFileName();

	public void setFileName(String aFileName);

	public void init(String aFileName) throws IOException;

	public void closeit() throws IOException;

	public void doConvertTemplate(Writer aFileWriter, IS3ScriptParserContext aFinder) throws IOException;

	public void parseCallMethod(IS3ScriptParserContext aFinder) throws IOException;

	public String getExportFileName(IS3ScriptParserContext aFinder);

	public String getLastErrorMsg();


}