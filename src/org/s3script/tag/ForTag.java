package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;

/**
 * For循环的标签 标签格式为 <s3:for start="var i=0" test="i<100" step="i++" >
 * 输出Javascript语句为 for (var i=0;i<100;i++) {
 * 
 * @author 宋牮
 * 
 */
public class ForTag extends AbsS3ScriptTag {
	public final static String C_sAttname_Test = "test";
	public final static String C_sAttname_Start = "start";
	public final static String C_sAttname_Step = "step";

	public ForTag() {
		fSingle = false;
		fTagName = "s3:for";
	}

	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		return String.format("for (%s;%s;%s){", aData.getAttValue(C_sAttname_Start),
				aData.getAttValue(C_sAttname_Test), aData.getAttValue(C_sAttname_Step));
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		return " } //end for";
	}

}
