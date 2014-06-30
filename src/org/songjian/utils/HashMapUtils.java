package org.songjian.utils;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

public class HashMapUtils {

	public static HashMap<Integer, String> getFromIntegerProperties(Properties aProperties) {
		if (aProperties == null)
			return null;

		HashMap<Integer, String> result = new HashMap<Integer, String>();
		Iterator<Entry<Object, Object>> it = aProperties.entrySet().iterator();
		String fkey;
		String fvalue;
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			fkey = entry.getKey().toString();
			fvalue = entry.getValue().toString();
			result.put(Integer.parseInt(fkey), fvalue);
		}
		return result;
	}

	public static void loadHashFromFile(HashMap<String, String> aHashmap, String aFileName, String aCharset) throws IOException {
		File fFile;
		if (aHashmap == null)
			return;
		aHashmap.clear();
		fFile = new File(aFileName);
		if (!fFile.exists())
			return;
		BufferedReader fReader;
		fReader=StreamUtils.createFileReader(aFileName, aCharset);
		try{
			String fLine;
			INameValue fItem;
			do{
				fLine=fReader.readLine();
				if (fLine!=null){
					fItem=StringUtils.splitNameValue_NameValue(fLine);
					if (fItem==null) continue;
					aHashmap.put(fItem.getItemName(), fItem.getItemValue());
				}
			}while (fLine!=null);
		}finally{
			fReader.close();
		}
		
	}

}
