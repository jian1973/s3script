package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;

/**
 * switch标签
 * 标签格式为 <s3:switch value="fValue" >
 * 输出Javascript语句为  switch (fValue) {
 * @author 宋牮
 *
 */
public class SwitchTag extends AbsS3ScriptTag {
	public final static String C_sAttname_Value = "value";
	public final static String C_sAttname_Ln = "ln";
	public SwitchTag() {
		fSingle = false;
		fTagName = "s3:switch";
	}

	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		aCallback.setSkipPushBlank(true);
		return String.format(" switch (%s)\r\n {", aData.getAttValue(C_sAttname_Value));
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		aCallback.setSkipPushBlank(false);
		return "} // end switch";
	}

}
