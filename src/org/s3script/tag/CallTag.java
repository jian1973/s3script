package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;
import org.songjian.utils.IConst;
import org.songjian.utils.INameValue;
import org.songjian.utils.StringUtils;

/**
 * include标签 标签格式为 <s3:call function="test@s3jstest" parameters="aId,aName">
 * 输出Javascript语句为 result.push(s3jstest.test(aId,aName));
 * 
 * @author 宋牮
 * 
 */
public class CallTag extends AbsS3ScriptTag {

	public final static String C_sAttname_Function = "function";
	public final static String C_sAttname_Parameters = "parameters";
	
	
	public CallTag() {
		fSingle = true;
		fResultPush = true;
		fTagName = "s3:call";
	}

	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		String fCallFunc;
		INameValue fNameValue;
		fNameValue=StringUtils.splitEMailValue(aData.getAttValue(C_sAttname_Function));
		if (fNameValue==null)
			fCallFunc=aData.getAttValue(C_sAttname_Function);
		else if (StringUtils.isBlank(fNameValue.getItemValue()))
			fCallFunc=fNameValue.getItemName();
		else
			fCallFunc=fNameValue.getItemValue()+"."+fNameValue.getItemName();
			
		aCallback.pushCallMethod(fNameValue);
		return String
				.format("%s(%s)", fCallFunc, aData.getAttValue(C_sAttname_Parameters));
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		return IConst.C_sBlank;
	}

}
