package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;

/**
 * Foreach循环的标签 标签格式为 <s3:foreach item="var fItem" array="fList" >
 * 输出Javascript语句为 for (var fItem in fList) {
 * 
 * @author 宋牮
 * 
 */
public class ForeachTag extends AbsS3ScriptTag {

	public final static String C_sAttname_Item = "item";
	public final static String C_sAttname_Array = "array";

	public ForeachTag() {
		fSingle = false;
		fTagName = "s3:foreach";
	}

	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		return String.format(" for (%s in %s) {", aData.getAttValue(C_sAttname_Item),
				aData.getAttValue(C_sAttname_Array));
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		return " } //end foreach";
	}
	
	

}
