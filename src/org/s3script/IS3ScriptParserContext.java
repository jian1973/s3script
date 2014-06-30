package org.s3script;

public interface IS3ScriptParserContext {

	public  IS3ScriptTemplate  findTemplate(String aMethod,String aNameSpace);
	public  IS3ScriptTemplate  findTemplateByCallName(String aCallName);
	public String getNamespaceFileName(String aNameSpace);
	/**
	 * 是否为安全模式
	 * @return
	 */
	public boolean isSecurityMode();
}
