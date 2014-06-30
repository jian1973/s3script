package org.s3script.tag;

import org.s3script.AbsS3ScriptTag;
import org.s3script.IS3DomTagCall;
import org.s3script.IS3ScriptPaserCallback;
import org.songjian.utils.ExceptionUtils;
import org.songjian.utils.IConst;
import org.songjian.utils.INameValue;
import org.songjian.utils.StringUtils;

public class DomTag extends AbsS3ScriptTag {
	public final static String C_sAttname_id = "id";
	public final static String C_sAttname_Function = "function";
	public final static String C_sAttname_Parameters = "parameters";
	public final static String C_sAttname_Inner = "inner";

	public DomTag() {
		fSingle = true;
		fResultPush = true;
		fTagName = "s3:dom";
	}

	@Override
	public String exportHead(ITagData aData, IS3ScriptPaserCallback aCallback) {
		
		prvS3DomTagCall fCall;
		fCall=new prvS3DomTagCall();
		fCall.setId(aData.getAttValue(C_sAttname_id));
		fCall.setCallParam(aData.getAttValue(C_sAttname_Parameters));
		fCall.setInner(StringUtils.Str2bool(aData.getAttValue(C_sAttname_Inner)));
		INameValue fNameValue;
		fNameValue = StringUtils.splitEMailValue(aData.getAttValue(C_sAttname_Function));
		fCall.setCallName(fNameValue);
		fCall.check();

		aCallback.pushDomTag(fCall);
		if (!fCall.getInner())
			return String.format("<div id=\"%s\"></div>\r\n", fCall.getId());
		else
			return IConst.C_sBlank+"\r\n";
	}

	@Override
	public String exportEnd(ITagData aData, IS3ScriptPaserCallback aCallback) {

		return IConst.C_sBlank;
	}

	private class prvS3DomTagCall implements IS3DomTagCall {
		
		

		private String fId;
		private INameValue fCallName;
		private String fCallParam;
		private boolean fInner;
		
		public void check(){
			ExceptionUtils.checkNull(fId, C_sAttname_id);
			ExceptionUtils.checkNull(fCallName, C_sAttname_Function);
			ExceptionUtils.checkNull(fCallName.getItemName(), C_sAttname_Function+".methodname");
			ExceptionUtils.checkNull(fCallName.getItemValue(), C_sAttname_Function+".namespace");
			
			ExceptionUtils.checkNull(fCallParam, C_sAttname_Parameters);
		}

		public String getId() {
			return fId;
		}

		public void setId(String aId) {
			this.fId = aId;
		}

		public INameValue getCallName() {
			return fCallName;
		}

		public void setCallName(INameValue aCallName) {
			this.fCallName = aCallName;
		}

		public String getCallParam() {
			return fCallParam;
		}

		public void setCallParam(String aCallParam) {
			this.fCallParam = aCallParam;
		}

		public boolean getInner() {
			return fInner;
		}

		public void setInner(boolean aInner) {
			this.fInner = aInner;
		}

	}

}
