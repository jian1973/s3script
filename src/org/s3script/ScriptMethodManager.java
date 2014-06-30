package org.s3script;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.songjian.utils.ExceptionUtils;
import org.songjian.utils.FileUtils;
import org.songjian.utils.HashMapUtils;
import org.songjian.utils.StreamUtils;
import org.songjian.utils.StringConvertUtils;
import org.songjian.utils.StringUtils;

public class ScriptMethodManager implements IS3ScriptParserContext {

	private HashMap<String, IS3ScriptTemplate> fMethodHash;

	private HashMap<String, String> fNamespaceFileBaseHash;
	public final static String C_sFileExt_S3Js = "s3js";
	public final static String C_sFileName_NameSpace = "NamespacePath.txt";

	private String fErrorLogPath;

	public ScriptMethodManager() {
		fMethodHash = new HashMap<String, IS3ScriptTemplate>();
		fNamespaceFileBaseHash = new HashMap<String, String>();
	}
	public String getErrorLogPath() {
		return fErrorLogPath;
	}

	public void setErrorLogPath(String aErrorLogPath) {
		this.fErrorLogPath = aErrorLogPath;
	}

	
	public final synchronized IS3ScriptTemplate[] getAll(){
		IS3ScriptTemplate[] result;
		result=new IS3ScriptTemplate[fMethodHash.size()];
		result=fMethodHash.values().toArray(result);
		return result;
	}

	@Override
	public synchronized IS3ScriptTemplate findTemplate(String aMethod, String aNameSpace) {

		String ftemp;
		ftemp = StringUtils.getCallMethodName(aMethod, aNameSpace);
		if (ftemp == null)
			return null;
		return findTemplateByCallName(ftemp);
	}

	public synchronized void addTemplate(IS3ScriptTemplate aTemplate) {
		String ftemp;
		if (aTemplate == null)
			return;
		ftemp = StringUtils.getCallMethodName(aTemplate.getFuncName(), aTemplate.getNamespace());
		ExceptionUtils.assertFormat(ftemp != null, "Can't  add template key is null,fileName='%s'", aTemplate.getFileName(), aTemplate.getFuncName());
		ExceptionUtils.assertFormat(!fMethodHash.containsKey(ftemp), "Can't  add template all ready found it,name='%s.%s',fileName='%s'",
				aTemplate.getNamespace(), aTemplate.getFuncName(), aTemplate.getFileName());
		fMethodHash.put(ftemp, aTemplate);

	}

	public IS3ScriptTemplate newTemplate() {
		return new S3ScriptTemplate();
	}

	public interface IOnLoadError{
		public void outException(String aTemplateFileName,String aLastErrorMsg, Exception aEx) throws IOException ;
	}
	
	private class prvOnLoadError implements IOnLoadError{

		@Override
		public void outException(String aTemplateFileName, String aLastErrorMsg, Exception aEx) throws IOException {
			File fFile;
			fFile=FileUtils.getNowDataFile(fErrorLogPath,".error");
			PrintWriter fWrite;
			
			fWrite=new PrintWriter(StreamUtils.createFileWriter(fFile, StringConvertUtils.getBytesCharset()));
			try{
				fWrite.print("template file ="+aTemplateFileName+" ");
				fWrite.println(aLastErrorMsg);
				aEx.printStackTrace(fWrite);
				fWrite.flush();
			}finally{
				fWrite.close();
			}
			System.err.println(String.format("load template error name=%s %s ,%s",aTemplateFileName,aLastErrorMsg,aEx.getMessage()));
			
		}
		
	}
	
	
	private IOnLoadError fOnError=new prvOnLoadError();

	public synchronized void loadFromPath(String aPath,IOnLoadError aOnError) throws IOException {
		final String fNowPath;
		fNowPath = aPath;
		final IOnLoadError fThisOnError;
		if (aOnError==null)
			fThisOnError=fOnError;
		else
			fThisOnError=aOnError;
		FileUtils.IEnumFileAction fAction = new FileUtils.IEnumFileAction() {

			@Override
			public boolean onEnumFile(File aFile) throws IOException {
				if (aFile == null)
					return true;
				String fFileName, fFileExt;
				String fPathName;
				fFileName = aFile.getName();
				fFileExt = FileUtils.getExt(fFileName);
				if (!fFileExt.equals(C_sFileExt_S3Js))
					return true;
				IS3ScriptTemplate fTemplet;
				fTemplet = newTemplate();
				fPathName = aFile.getPath();
				fFileName = StringUtils.deleteHead(fPathName, fNowPath);
				fTemplet.init(fPathName);
				try {
					try {
						fTemplet.setFileName(fFileName);
						fTemplet.parseCallMethod(ScriptMethodManager.this);
						addTemplate(fTemplet);

					} catch (Exception ex) {
						fThisOnError.outException(fFileName,fTemplet.getLastErrorMsg(), ex);
					}
				} finally {
					fTemplet.closeit();
				}
				return true;
			}
		};
		FileUtils.enumPathFiles(aPath, true, fAction);
		fNamespaceFileBaseHash.clear();
		String fNamespacePathFile;
		fNamespacePathFile = StringUtils.getPathStr_hasEnd(fNowPath) + C_sFileName_NameSpace;
		HashMapUtils.loadHashFromFile(fNamespaceFileBaseHash, fNamespacePathFile, StringConvertUtils.getBytesCharset());
	}

	@Override
	public IS3ScriptTemplate findTemplateByCallName(String aCallName) {
		return fMethodHash.get(aCallName);
	}

	@Override
	public String getNamespaceFileName(String aNameSpace) {
		return fNamespaceFileBaseHash.get(aNameSpace);
	}

	public String findNamespaceByFileName(String aFileName) {
		Set<Entry<String, String>> fSet = fNamespaceFileBaseHash.entrySet();
		String fkey;
		String fvalue;
		for (Entry<String, String> fItem : fSet) {
			fkey = fItem.getKey().toString();
			fvalue = fItem.getValue().toString();
			if (fvalue.equals(aFileName))
				return fkey;
		}
		return null;
	}

	public List<IS3ScriptTemplate> getNamespaceFiles(String aSpaceName) {
		List<IS3ScriptTemplate> result = new ArrayList<IS3ScriptTemplate>();
		for (IS3ScriptTemplate fTemplate : fMethodHash.values()) {
			if (fTemplate == null)
				continue;
			if (fTemplate.getNamespace().equals(aSpaceName))
				result.add(fTemplate);
		}
		return result;

	}

	@Override
	public boolean isSecurityMode() {
		return true;
	}

}
