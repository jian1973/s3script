package org.s3script;

import org.songjian.utils.INameValue;

public interface IS3DomTagCall {

	public String getId();

	public void setId(String aId);

	public INameValue getCallName();

	public void setCallName(INameValue aCallName);

	public String getCallParam();

	public void setCallParam(String aCallParam);

	public boolean getInner();

	public void setInner(boolean aInner);

}
