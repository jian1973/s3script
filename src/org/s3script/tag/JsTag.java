package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3ScriptPaserCallback;
/**
 * js标签 标签格式为 <s3:js script="aa=2;bbb=3;">
 * 输出Javascript语句为 aa=2;bbb=3;
 * 
 * @author 宋牮
 * 
 */
public class JsTag extends AbsS3ScriptTag {
	public  final  static String C_sAttname_Script="script";

	public JsTag(){
		fSingle=true;
		fTagName="s3:js";
	}
	@Override
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback){
		return aData.getAttValue(C_sAttname_Script);
	}

	@Override
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback){
		return null;
	}

}
