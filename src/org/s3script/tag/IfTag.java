package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;

/**
 * if标签
 * 标签格式为 <s3:if test="fValue==true">
 * 输出Javascript语句为  if (fValue==true) 
 * @author 宋牮
 *
 */
public class IfTag extends AbsS3ScriptTag {
	public final static String C_sAttname_Test = "test";
	public IfTag(){
		fTagName="s3:if";
		fSingle=false;
	}

	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		return String.format(" if (%s){",aData.getAttValue(C_sAttname_Test));
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		return " } //end if";
	}

}
