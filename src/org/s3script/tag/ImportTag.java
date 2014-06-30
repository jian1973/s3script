package org.s3script.tag;

import java.util.HashMap;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptParserContext;
import org.s3script.IS3ScriptPaserCallback;
import org.s3script.IS3ScriptTemplate;
import org.songjian.utils.ExceptionUtils;
import org.songjian.utils.INameValue;
import org.songjian.utils.NameValue;
import org.songjian.utils.NamedList;
import org.songjian.utils.StringUtils;

public class ImportTag extends AbsS3ScriptTag {
	public final static String C_sAttname_Function = "function";

	public ImportTag() {
		fSingle = false;
		fTagName = "s3:import";
	}

	public static final void splitNameValueNameValueList_Tag(String aText, NamedList<INameValue> aNameValueList) {

		ExceptionUtils.assertFormat(aNameValueList != null, "splitNameValueTNameValueList() aNameValueList is null!");

		if (StringUtils.isBlank(aText))
			return;
		String[] ftemp;
		ftemp = aText.split(";");
		INameValue fItem;
		for (int i = 0; i < ftemp.length; i++) {
			fItem = StringUtils.splitEMailValue(ftemp[i]);
			ExceptionUtils.checkNull(fItem, "ftemp[i]");
			aNameValueList.addItem(fItem);
		}
	}

	private final String getSubTemplateName(String aLine) {
		INameValue fInfo;
		fInfo = StringUtils.splitNameValue_NameValue(aLine);
		if (fInfo == null)
			return null;
		return fInfo.getItemName();

	}

	private final void setCallTempletHash(HashMap<String, IS3ScriptTemplate> aHash, IS3ScriptTemplate aTemplet, IS3ScriptParserContext aFinder) {
		if (aTemplet == null)
			return;
		String fTemplateName;
		fTemplateName = StringUtils.getCallMethodName(aTemplet.getFuncName(), aTemplet.getNamespace());

		if (aHash.containsKey(fTemplateName))
			return;

		aHash.put(fTemplateName, aTemplet);

		String fImport;
		fImport = aTemplet.getImport();
		if (StringUtils.isBlank(fImport))
			return;
		String[] ftemp = fImport.split("\r\n");
		if (ftemp == null)
			return;
		for (String fItemStr : ftemp) {
			if (StringUtils.isBlank(fItemStr))
				continue;
			fItemStr = getSubTemplateName(fItemStr);
			if (StringUtils.isBlank(fItemStr))
				continue;
			setCallTempletHash(aHash, aFinder.findTemplateByCallName(fItemStr), aFinder);

		}
	}

	private void setToAttribImport(NamedList<INameValue> aList, String aAttrib) {
		if (StringUtils.isBlank(aAttrib))
			return;
		splitNameValueNameValueList_Tag(aAttrib, aList);
	}

	private final boolean isExport(IS3ScriptPaserCallback aCallback) {
		String ftemp;
		ftemp = aCallback.getProperty("EXPORT");
		if (ftemp != null && ftemp.equalsIgnoreCase("True"))
			return true;
		else
			return false;

	}

	private void parseAttrib(NamedList<INameValue> aList, ITagData aData) {
		String[] fAttNames;
		fAttNames = aData.getAttNames();
		for (String fAttName : fAttNames) {
			if (StringUtils.isBlank(fAttName))
				continue;
			if (fAttName.toUpperCase().startsWith(C_sAttname_Function.toUpperCase()))
				setToAttribImport(aList, aData.getAttValue(fAttName));
		}

	}

	private void parseCallMethod(NamedList<INameValue> aList, IS3ScriptPaserCallback aCallback) {
		INameValue[] fCallMethodArray;
		fCallMethodArray = aCallback.getCallMethodArray();
		for (INameValue fCallItem : fCallMethodArray) {
			if (fCallItem == null)
				continue;
			aList.addItem(fCallItem);
		}
	}

	private void loadTemplate(NamedList<INameValue> aList, HashMap<String, IS3ScriptTemplate> aImportHash, IS3ScriptParserContext aFinder) {
		IS3ScriptTemplate fTemplateItem;
		for (INameValue fNameValue : aList) {
			if (fNameValue == null)
				continue;
			fTemplateItem = aFinder.findTemplate(fNameValue.getItemName(), fNameValue.getItemValue());
			ExceptionUtils.checkNull_Notfound(fTemplateItem,  StringUtils.getCallMethodName_NameValue(fNameValue));
			setCallTempletHash(aImportHash, fTemplateItem, aFinder);
		}

	}

	private void makeOutputFileName(HashMap<String, IS3ScriptTemplate> aImportHash, boolean aExport, HashMap<String, String> aOutFileName,
			HashMap<String, String> aOutNameSpace, IS3ScriptParserContext aFinder) {
		String fTempNameSpace, fOutPutFileName;
		// 生成输出的文件和命名空间
		for (IS3ScriptTemplate fItem : aImportHash.values()) {
			if (fItem == null)
				continue;

			if (aExport) {
				fOutPutFileName = fItem.getExportFileName(aFinder);
				aOutFileName.put(fOutPutFileName, fOutPutFileName);
			} else
				aOutFileName.put(fItem.getFileName(), fItem.getFileName());
			fTempNameSpace = fItem.getNamespace();
			aOutNameSpace.put(fTempNameSpace, fTempNameSpace);
		}
	}

	private void makeOutputScript(StringBuilder aFb, boolean aExport, HashMap<String, String> aOutFileName, HashMap<String, String> aOutNameSpace) {
		if (aOutFileName.size() > 0 && !aExport) {
			aFb.append("<script  type=\"text/javascript\">\r\n");
			for (String fNameSpace : aOutNameSpace.keySet()) {
				if (StringUtils.isBlank(fNameSpace))
					continue;
				aFb.append(String.format("var %s={};\r\n", fNameSpace));
			}
			aFb.append("</script>\r\n");
		}
		for (String fFileName : aOutFileName.keySet()) {
			if (StringUtils.isBlank(fFileName))
				continue;
			aFb.append(String.format("<script src=\"%s\" type=\"text/javascript\"></script>\r\n", fFileName));
		}

	}

	@Override
	public String exportHead(ITagData aData, IS3ScriptPaserCallback aCallback) {
		NamedList<INameValue> fList;
		fList = new NamedList<INameValue>() {

			@Override
			public INameValue newItem() {
				return new NameValue();
			}
		};

		// 设置导出Script文件的类型,是源代码,还是发布
		boolean fExport = isExport(aCallback);

		// 初始化引用的模板HashMap
		HashMap<String, IS3ScriptTemplate> fImportHash;
		fImportHash = new HashMap<String, IS3ScriptTemplate>();

		// 初始化模板查找对象
		IS3ScriptParserContext fFinder;
		fFinder = aCallback.getDHtmlFinder();
		ExceptionUtils.checkNull(fFinder, "Callback.getDHtmlFinder()");

		// 将function开头属性中的,调用模板加入fList中
		parseAttrib(fList, aData);

		// 将解析出引用的标签
		parseCallMethod(fList, aCallback);

		// 加载对应的模板
		loadTemplate(fList, fImportHash, fFinder);

		HashMap<String, String> fOutFileName;
		HashMap<String, String> fOutNameSpace;

		fOutFileName = new HashMap<String, String>();
		fOutNameSpace = new HashMap<String, String>();

		// 生成要输出的引用文件名,和命名空间
		makeOutputFileName(fImportHash, fExport, fOutFileName, fOutNameSpace, fFinder);

		StringBuilder fb;
		fb = new StringBuilder();
		// 生成输出的Script脚本
		makeOutputScript(fb, fExport, fOutFileName, fOutNameSpace);
		return fb.toString();
	}

	@Override
	public String exportEnd(ITagData aData, IS3ScriptPaserCallback aCallback) {
		return "";
	}

}
