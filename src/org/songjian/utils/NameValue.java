package org.songjian.utils;

public class NameValue extends ResultTwoValue<String, String> implements INameValue {

	
	public NameValue(){
		Value1=null;
		Value2=null;
	}
	
	

	@Override
	public String getItemValue() {
		return Value2;
	}

	@Override
	public void setItemValue(String aItemValue) {
		this.Value2 = aItemValue;
	}

	@Override
	public String getItemName() {
		return Value1;
	}

	@Override
	public void setItemName(String aItemName) {
		this.Value1 = aItemName;
	}
	
	
	@Override
	public String toString(){
		return String.format("%s=%s",Value1, Value2);
	}

}
