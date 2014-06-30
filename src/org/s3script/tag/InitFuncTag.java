package org.s3script.tag;

import java.util.List;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3DomTagCall;
import org.s3script.IS3ScriptPaserCallback;
import org.s3script.S3StringBuilder;
import org.songjian.utils.IConst;
import org.songjian.utils.StringUtils;

public class InitFuncTag extends AbsS3ScriptTag {
	public final static String C_sAttname_Function = "function";
	public final static String C_sAttname_Parameters = "parameters";

	public InitFuncTag() {
		fSingle = false;
		fResultPush = true;
		fTagName = "s3:initfunc";
	}

	private final String makeInitFunctionBody(IS3ScriptPaserCallback aCallback){
		S3StringBuilder fb;
		List<IS3DomTagCall> fList;
		fb=new S3StringBuilder();
		fList=aCallback.getDomTagList();
		fb.appendLn("var fDomItem;");
		String fCallMethod;
		for (IS3DomTagCall fCall : fList) {
			
			fb.appendFormatLn("fDomItem=document.getElementById(\"%s\");",fCall.getId());
			fCallMethod=String.format("%s(%s)", fCall.getCallName().getItemValue()+"."+fCall.getCallName().getItemName(),
				StringUtils.getBlankStr(fCall.getCallParam()));
			if (fCall.getInner())
				  fb.appendFormatLn("fDomItem.innerHTML=%s;",fCallMethod);
			else
				  fb.appendFormatLn("fDomItem.outerHTML=%s;",fCallMethod);
		}
		return fb.toString();
	}
	@Override
	public String exportHead(ITagData aData, IS3ScriptPaserCallback aCallback) {
		String fInitFunction;
		fInitFunction= aData.getAttValue(C_sAttname_Function);
		StringBuilder fb;
		fb=new StringBuilder();
		fb.append("\r\n<script type=\"text/javascript\">\r\n");
		fb.append(String.format("function %s(%s){",fInitFunction,
				StringUtils.getBlankStr(aData.getAttValue(C_sAttname_Parameters))));
		fb.append(makeInitFunctionBody(aCallback));
		fb.append(String.format("\r\n}\r\n"));
		fb.append("</script>\r\n");
		return fb.toString();
	}

	@Override
	public String exportEnd(ITagData aData, IS3ScriptPaserCallback aCallback) {
		return IConst.C_sBlank;
	}

}
