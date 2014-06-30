package org.s3script;

import java.util.HashMap;

import org.songjian.utils.*;


public abstract class AbsS3ScriptTag implements IS3ScriptTag {

	protected String fTagName;
	protected boolean fSingle;
	protected boolean fResultPush;

	public final String getTagName() {
		return fTagName;
		
	}

	public boolean isSingle() {
		return fSingle;
	}

	public void setSingle(boolean aSingle) {
		this.fSingle = aSingle;
	}
	
	public boolean isResultPush(){
		return fResultPush;
	}
	
	public class TTagData implements ITagData{
		protected IS3ScriptTag fOwnerTag;
		protected HashMap<String, String> fAttMap;

		public TTagData(IS3ScriptTag aOwnerTag){
			fOwnerTag=aOwnerTag;
			fAttMap=new HashMap<String, String>();
		}
		@Override
		public IS3ScriptTag getOwnerTag() {
			return fOwnerTag;
		}
		@Override
		public String getAttValue(String aAttName) {
			return fAttMap.get(aAttName.trim().toUpperCase());
		}
		
		@Override
		public void setAttData(String aAttData) {
			if (aAttData==null) return ;
			if (aAttData.trim().equals("")) return ;
			INameValue fNameValue;
			aAttData=aAttData.trim();
			fNameValue=StringUtils.splitNameValue_NameValue(aAttData);
			if (fNameValue==null)
				fAttMap.put(aAttData.toUpperCase(), "");
			else 
				fAttMap.put(fNameValue.getItemName().trim().toUpperCase(), fNameValue.getItemValue().trim());
		}
		@Override
		public String[] getAttNames() {
			String[] result;
			result=new String[fAttMap.size()];
			fAttMap.keySet().toArray(result);
			return result;
		}
	}
	public ITagData newTagData(){
		return new TTagData(this);
	}


}
