package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;

/**
 * switch标签
 * 标签格式为 <s3:case value="3">
 * 输出Javascript语句为  case 3:
 *                  break;
 * @author 宋牮
 *
 */
public class CaseTag extends AbsS3ScriptTag {
	public final static String C_sAttname_Value = "value";
	public CaseTag() {
		fSingle=false;
		fTagName="s3:case";
	}

	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		aCallback.setSkipPushBlank(false);
		return String.format("case %s:", aData.getAttValue(C_sAttname_Value));
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		aCallback.setSkipPushBlank(true);
		return " break;";
	}

}
