package org.songjian.utils;

import java.util.*;

public abstract class NamedList<T extends INamedItem> implements Iterable<T>{
	private List<T> fList;

	public NamedList() {
		fList = new ArrayList<T>();
	}

	public boolean checkSameName(String aName1, String aName2) {
		return aName1.equals(aName2);
	}

	public T getItem(String aName) {
		ExceptionUtils.assertFormat(aName != null, "getItem(String) aName is null!");
		for (T fItem : fList) {
			if (checkSameName(fItem.getItemName(), aName)) {
				return fItem;
			}
		}
		return null;
	}

	public T getItemByIndex(int aIndex) {
		ExceptionUtils.assertFormat(aIndex >= 0 && aIndex < fList.size(), "getItemByIndex(int) aIndex error,aIndex=%d",
				aIndex);
		return fList.get(aIndex);

	}

	public T delItem(String aName) {
		ExceptionUtils.assertFormat(aName != null, "getItem(String) aName is null!");
		T fItem;
		for (int i = 0; i < fList.size(); i++) {
			fItem = fList.get(i);
			if (checkSameName(aName, fItem.getItemName())) {
				fList.remove(i);
				return fItem;
			}
		}
		return null;
	}
	
	public boolean addItem(T aItem){
		return fList.add(aItem);
	}

	public abstract T newItem();

	public T getAndCreate(String aName) {
		T result;
		result = getItem(aName);
		if (result == null) {
			result = newItem();
			result.setItemName(aName);
			fList.add(result);
		}
		return result;
	}
	
	public void clear(){
		fList.clear();
	}
	
	public int getCount(){
		return fList.size();
	}
	
	public Iterator<T> iterator(){
		return fList.iterator();
	}
	
}
