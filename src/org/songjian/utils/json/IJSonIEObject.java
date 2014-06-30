package org.songjian.utils.json;

import com.alibaba.fastjson.JSONObject;

public interface IJSonIEObject {
	public void importFromJSon(JSONObject aJSON);
	public JSONObject exportToJSon(JSONObject aJSONObject) ;
	public void clear() ;

	public interface INewFactory<T extends IJSonIEObject>{
		public T newObj();
	}

	public interface INewFactoryJSon<T extends IJSonIEObject>{
		public T newObj(JSONObject aData);
	}
}
