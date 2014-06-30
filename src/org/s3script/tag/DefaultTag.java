package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;

/**
 * switch标签
 * 标签格式为 <s3:default >
 * 输出Javascript语句为  default:
 *                  break;
 * @author 宋牮
 *
 */
public class DefaultTag extends AbsS3ScriptTag{
	public DefaultTag(){
		fSingle=false;
		fTagName="s3:default";
	}

	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		aCallback.setSkipPushBlank(false);
		return " default:";
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		aCallback.setSkipPushBlank(true);
		return " break;";
	}


}
