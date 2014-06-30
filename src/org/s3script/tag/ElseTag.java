package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;

/**
 * else标签
 * 标签格式为 <c:else>
 * 输出Javascript语句为  
 *    }
 *     else {
 * @author 宋牮
 *
 */
public class ElseTag extends AbsS3ScriptTag{
	public ElseTag(){
		fTagName="s3:else";
		fSingle=true;
		
	}
	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		return "}\r\nelse {";
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		return null;
	}

}
