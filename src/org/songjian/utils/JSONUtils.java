package org.songjian.utils;


import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.songjian.utils.json.IJSonIEObject;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONUtils {

	public final static String[] getStringArray(JSONObject aJSON, String aKey) {
		String[] result;
		if (aJSON == null)
			return null;
		JSONArray ftempArray;
		ftempArray = aJSON.getJSONArray(aKey);
		if (ftempArray == null)
			return null;
		result = new String[ftempArray.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = ftempArray.getString(i);
		}
		return result;
	}
	
	public final static String getJSonString(IJSonIEObject aObj){
		JSONObject fObj;
		fObj=aObj.exportToJSon(null);
		if (fObj==null) return null;
		return fObj.toJSONString();
	}
	
	
	
	public final static String[] parseStringArray(String aJSonStr) {
		JSONArray ftemp;
		if (aJSonStr==null) return null;
		ftemp=parseJSonArray(aJSonStr);
		if (ftemp==null) return null;
		String[] result;
		result=new String[ftemp.size()];
		if (result.length<=0) return result;
		for (int i = 0; i < result.length; i++) {
			result[i]=ftemp.getString(i);
		}
		return result;
	}
	
	public final static JSONArray StringArrayToJSonArray(String[] aArray){
		JSONArray result;
		if (aArray==null) return null;
		result=new JSONArray(aArray.length);
		for (int i = 0; i < aArray.length; i++) {
			result.add(aArray[i]);
		}
		return result;
	}
	public final static String[] JSonArrayToStringArray(JSONArray aArray){
		String[] result;
		if (aArray==null) return null;
		result=new String[aArray.size()];
		for (int i = 0; i < result.length; i++) {
			result[i]=aArray.getString(i);
		}
		return result;
	}

	public final static JSONArray toPutJSonList(List<? extends IJSonIEObject> aList) {
		if (aList == null) {
			return null;
		}
		if (aList.size() <= 0) {
			return new JSONArray();
		}
		JSONArray ftempArray;
		JSONObject fitem;
		IJSonIEObject fitemObj;
		ftempArray = new JSONArray(aList.size());
		for (int i = 0; i < aList.size(); i++) {
			fitemObj = aList.get(i);
			fitem = null;
			if (fitemObj != null)
				fitem = fitemObj.exportToJSon(null);
			ftempArray.add(fitem);
		}
		return ftempArray;
	}

	public final static <T extends IJSonIEObject> JSONArray toPutJSonArray(T[] aArray) {
		if (aArray == null) {
			return null;
		}
		if (aArray.length <= 0) {
			return new JSONArray();
		}
		JSONArray ftempArray;
		JSONObject fitem;
		IJSonIEObject fitemObj;
		ftempArray = new JSONArray(aArray.length);
		for (int i = 0; i < aArray.length; i++) {
			fitemObj = aArray[i];
			fitem = null;
			if (fitemObj != null)
				fitem = fitemObj.exportToJSon(null);
			ftempArray.add(fitem);
		}
		return ftempArray;
	}

	public final static JSONObject toPutJSonObj(IJSonIEObject aObj) {
		if (aObj == null)
			return null;
		return aObj.exportToJSon(null);
	}

	public final static <T extends IJSonIEObject> T getImportJSonObj(JSONObject aJSon, Class<T> aClass) {
		if (aJSon == null)
			return null;
		T result;
		try {
			result = aClass.newInstance();
		} catch (InstantiationException e1) {
			throw new RuntimeException(e1);
		} catch (IllegalAccessException e2) {
			throw new RuntimeException(e2);
		}
		result.importFromJSon(aJSon);
		return result;
	}
	
	public final static <T extends IJSonIEObject> void copyProperties(IJSonIEObject aFrom,T aTo){
		JSONObject fObj;
		fObj=aFrom.exportToJSon(null);
		aTo.importFromJSon(fObj);
	}
	
	public final static JSONObject copyToNew(JSONObject  aObj){
		if (aObj==null) return null;
		String ftemp;
		ftemp=aObj.toJSONString();
		return parseJSon(ftemp);
	}
	
	public final static void copyProperties(JSONObject aSource,JSONObject  aDest){
		if (aSource==null) return ;
		if (aDest==null) return ;
		JSONObject ftemp;
		ftemp=copyToNew(aSource);
		for (String fKey : ftemp.keySet()) {
			aDest.put(fKey, ftemp.get(fKey));
		}
	}

	public final static JSONObject parseJSon(String aJSonStr) {
		return (JSONObject) JSONObject.parse(aJSonStr);
	}
	
	public final static  JSONObject parseJSonFile(String aFileName,String aCharSet) {
		String fJSonStr;
		fJSonStr=StringUtils.readFileString(aFileName, aCharSet);
		if (StringUtils.isBlank(fJSonStr)) return null;
		return parseJSon(fJSonStr);
	}

	public final static  JSONArray parseJSonFile_Array(String aFileName,String aCharSet) {
		String fJSonStr;
		fJSonStr=StringUtils.readFileString(aFileName, aCharSet);
		if (StringUtils.isBlank(fJSonStr)) return null;
		return parseJSonArray(fJSonStr);
	}
	
	public final static   <T extends IJSonIEObject>  T parseJSonFile_Class(String aFileName,Class<T> aClass,String aCharSet) {
		String fJSonStr;
		fJSonStr=StringUtils.readFileString(aFileName, aCharSet);
		if (StringUtils.isBlank(fJSonStr)) return null;
		JSONObject fObj;
		T result=null;
		try {
			fObj=parseJSon(fJSonStr);
			if (fObj==null) return null;
			result = aClass.newInstance();
			result.importFromJSon(fObj);
			return result;
		} catch (Exception ex) {
			throw ExceptionUtils.newException(ex);
		}
		
		
	}
	
	
	public final static  String[] parseJSonFile_StringArray(String aFileName,String aCharSet) {
		String fJSonStr;
		fJSonStr=StringUtils.readFileString(aFileName, aCharSet);
		if (StringUtils.isBlank(fJSonStr)) return null;
		
		return JSonArrayToStringArray(parseJSonArray(fJSonStr));
	}
	
	public final static boolean saveJSonFile(String aFileName,JSONObject aData,String aCharset) throws IOException {
		if (aData==null) return false;
		String fJSonStr;
		fJSonStr=aData.toJSONString();
		StreamUtils.writeStringToFile(aFileName, fJSonStr, aCharset);
		return true;
		
	}

	public final static boolean saveJSonFile_Array(String aFileName,JSONArray aData,String aCharset) throws IOException {
		if (aData==null) return false;
		String fJSonStr;
		fJSonStr=aData.toJSONString();
		StreamUtils.writeStringToFile(aFileName, fJSonStr, aCharset);
		return true;
		
	}

	public final static JSONArray parseJSonArray(String aJSonStr) {
		return (JSONArray) JSONObject.parse(aJSonStr);
	}

	public final static void parseJSonHash(String aJSonStr, Map<String, String> aMap, String aKey, String aValue) {
		JSONArray fJSonArray;
		JSONObject fitem;
		String fKey;
		String fValue;
		fJSonArray = JSONUtils.parseJSonArray(aJSonStr);
		for (int i = 0; i < fJSonArray.size(); i++) {
			fitem = fJSonArray.getJSONObject(i);
			fKey = fitem.getString(aKey);
			fValue = fitem.getString(aValue);
			aMap.put(fKey, fValue);
		}
	}

	public static <T extends IJSonIEObject> void parseJSonHashObj(String aJSonStr, Map<String, T> aMap, IJSonIEObject.INewFactory<T> aNewFactroy, String aKey) {
		if (StringUtils.isBlank(aJSonStr))
			return;
		JSONArray fJSonArray;
		JSONObject fitem;
		String fKey;
		T itemObj;
		fJSonArray = JSONUtils.parseJSonArray(aJSonStr);
		for (int i = 0; i < fJSonArray.size(); i++) {
			fitem = fJSonArray.getJSONObject(i);
			fKey = fitem.getString(aKey);
			itemObj = aNewFactroy.newObj();
			itemObj.importFromJSon(fitem);
			aMap.put(fKey, itemObj);
		}
	}
	public static <T extends IJSonIEObject> void parseJSonHashObj_JSonFactory(String aJSonStr, Map<String, T> aMap,IJSonIEObject.INewFactoryJSon<T> aNewFactoryJSon,String aKey) {
		if (StringUtils.isBlank(aJSonStr))
			return;
		JSONArray fJSonArray;
		JSONObject fitem;
		String fKey;
		T itemObj;
		fJSonArray = JSONUtils.parseJSonArray(aJSonStr);
		for (int i = 0; i < fJSonArray.size(); i++) {
			fitem = fJSonArray.getJSONObject(i);
			fKey = fitem.getString(aKey);
			itemObj = aNewFactoryJSon.newObj(fitem);
			itemObj.importFromJSon(fitem);
			aMap.put(fKey, itemObj);
		}
	}
	public static <T extends IJSonIEObject> void parseJSonHashObj_Class(String aJSonStr, Map<String, T> aMap, Class<T> aClass, String aKey) {
		JSONArray fJSonArray;
		JSONObject fitem;
		String fKey;
		T itemObj;
		if (StringUtils.isBlank(aJSonStr))
			return;
		fJSonArray = JSONUtils.parseJSonArray(aJSonStr);
		for (int i = 0; i < fJSonArray.size(); i++) {
			fitem = fJSonArray.getJSONObject(i);
			fKey = fitem.getString(aKey);
			try {
				itemObj = aClass.newInstance();
			} catch (Exception ex) {
				throw ExceptionUtils.newException(ex);
			}
			itemObj.importFromJSon(fitem);
			aMap.put(fKey, itemObj);
		}
	}
	
	
	
	public static <T extends IJSonIEObject> void parseJSonListObj_Class(String aJSonStr, List<T> aList, Class<T> aClass) {
		
		JSONArray fJSonArray;
		JSONObject fitem;
		T itemObj;
		if (StringUtils.isBlank(aJSonStr))
			return;
		fJSonArray = JSONUtils.parseJSonArray(aJSonStr);
		for (int i = 0; i < fJSonArray.size(); i++) {
			fitem = fJSonArray.getJSONObject(i);
			try {
				itemObj = aClass.newInstance();
			} catch (Exception ex) {
				throw ExceptionUtils.newException(ex);
			}
			itemObj.importFromJSon(fitem);
			aList.add(itemObj);
		}
	}

	public static <T extends IJSonIEObject> void parseJSonListObj(String aJSonStr, List<T> aList,IJSonIEObject.INewFactory<T> aNewFactroy) {
		
		JSONArray fJSonArray;
		JSONObject fitem;
		T itemObj;
		if (StringUtils.isBlank(aJSonStr))
			return;
		fJSonArray = JSONUtils.parseJSonArray(aJSonStr);
		for (int i = 0; i < fJSonArray.size(); i++) {
			fitem = fJSonArray.getJSONObject(i);
			try {
				itemObj = aNewFactroy.newObj();
			} catch (Exception ex) {
				throw ExceptionUtils.newException(ex);
			}
			itemObj.importFromJSon(fitem);
			aList.add(itemObj);
		}
	}

	public static <T extends IJSonIEObject> void parseJSonListObj_JSonFactory(String aJSonStr, List<T> aList,IJSonIEObject.INewFactoryJSon<T> aNewFactoryJSon) {
		
		JSONArray fJSonArray;
		JSONObject fitem;
		T itemObj;
		if (StringUtils.isBlank(aJSonStr))
			return;
		fJSonArray = JSONUtils.parseJSonArray(aJSonStr);
		for (int i = 0; i < fJSonArray.size(); i++) {
			fitem = fJSonArray.getJSONObject(i);
			try {
				itemObj = aNewFactoryJSon.newObj(fitem);
			} catch (Exception ex) {
				throw ExceptionUtils.newException(ex);
			}
			itemObj.importFromJSon(fitem);
			aList.add(itemObj);
		}
	}
	
	public static <G, T extends IJSonIEObject> int saveValue_List(Writer aWriter, List<T> aList) throws IOException {
		int fCount;
		fCount = 0;
		aWriter.write("[\r\n");
		fCount = saveValueBody_List(aWriter, aList, fCount);
		aWriter.write("]\r\n");
		return fCount;

	}

	public static <G, T extends IJSonIEObject> int saveValueBody_List(Writer aWriter, List<T> aList, int aCount) throws IOException {
		int fCount;
		fCount = aCount;
		for (T fItem : aList) {
			if (fCount != 0)
				aWriter.write(",\r\n");
			if (fItem == null)
				aWriter.write("null");
			else
				aWriter.write(fItem.exportToJSon(null).toJSONString());
			fCount++;

		}
		return fCount;
	}
	
	public static <G, T extends IJSonIEObject> int saveValue_HashMap(Writer aWriter, Map<G, T> aMap) throws IOException {
		int fCount;
		fCount = 0;
		aWriter.write("[\r\n");
		fCount = saveValueBody_HashMap(aWriter, aMap, fCount);
		aWriter.write("]\r\n");
		return fCount;

	}

	public static <G, T extends IJSonIEObject> int saveValueBody_HashMap(Writer aWriter, Map<G, T> aMap, int aCount) throws IOException {
		int fCount;
		fCount = aCount;
		for (T fItem : aMap.values()) {
			if (fCount != 0)
				aWriter.write(",\r\n");
			if (fItem == null)
				aWriter.write("null");
			else
				aWriter.write(fItem.exportToJSon(null).toJSONString());
			fCount++;

		}
		return fCount;
	}
	
	
	public static void main(String[] args){
		try {
			JSONArray f1;
			f1=new JSONArray();
			f1.add("111");
			f1.add("222");
			f1.add("333");
			String ftemp;
			ftemp=f1.toJSONString();
			System.out.println(ftemp);
			
			String[] j1;
			j1=parseStringArray(ftemp);
			for (int i = 0; i < j1.length; i++) {
				System.out.println(String.format("[%d] %s",i,j1[i]));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
