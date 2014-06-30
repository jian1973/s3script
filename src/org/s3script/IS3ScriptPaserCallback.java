package org.s3script;

import java.util.List;

import org.songjian.utils.INameValue;

public interface IS3ScriptPaserCallback {
	public void setSkipPushBlank(boolean aValue);
	public void pushCallMethod(INameValue aCallMethod);
	public void pushDomTag(IS3DomTagCall aTagCall);
	public IS3ScriptParserContext getDHtmlFinder();
	public String getProperty(String aName);
	public INameValue[] getCallMethodArray();
	public List<IS3DomTagCall> getDomTagList();
}
