package org.s3script;


public interface IS3ScriptTag {
	public interface ITagData {

		public IS3ScriptTag getOwnerTag();

		public String getAttValue(String aAttName);

		public void setAttData(String aAttData);
		
		public String[] getAttNames();

	}

	/**
	 * 新建标签数据对象,用于存储标签属性
	 * @return
	 */
	public ITagData newTagData();

	/**
	 * 标签名称
	 * @return
	 */
	public String getTagName();

	/**
	 * 单行标签,没有结束标签
	 * 
	 * @return
	 */
	public boolean isSingle();

	/**
	 * 返回的数据是不是脚本,而是Html字符串
	 * @return
	 */
	public boolean isResultPush();
	
	

	public void setSingle(boolean aSingle);

	/**
	 * 输出标签头信息
	 * @param aData 标签的属性
	 * @return
	 */
	public String exportHead(ITagData aData,IS3ScriptPaserCallback aCallback);

	/**
	 * 输出标签尾部信息
	 * @param aData
	 * @return
	 */
	public String exportEnd(ITagData aData,IS3ScriptPaserCallback aCallback);
}
