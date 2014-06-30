package org.s3script;

public class S3StringBuilder  implements Appendable, CharSequence {
	private StringBuilder fb;
	
	public S3StringBuilder(){
		fb=new StringBuilder();
	}
	public int length() {
		
		return fb.length();
	}

	
	public int capacity() {
		
		return fb.capacity();
	}

	
	public void ensureCapacity(int minimumCapacity) {
		
		fb.ensureCapacity(minimumCapacity);
	}

	
	public void trimToSize() {
		
		fb.trimToSize();
	}

	
	public void setLength(int newLength) {
		
		fb.setLength(newLength);
	}

	
	public char charAt(int index) {
		
		return fb.charAt(index);
	}

	
	public int codePointAt(int index) {
		
		return fb.codePointAt(index);
	}

	
	public int codePointBefore(int index) {
		
		return fb.codePointBefore(index);
	}

	
	public int codePointCount(int beginIndex, int endIndex) {
		
		return fb.codePointCount(beginIndex, endIndex);
	}

	
	public int offsetByCodePoints(int index, int codePointOffset) {
		
		return fb.offsetByCodePoints(index, codePointOffset);
	}

	
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		
		fb.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	
	public void setCharAt(int index, char ch) {
		
		fb.setCharAt(index, ch);
	}

	
	public S3StringBuilder append(Object obj) {
		fb.append(obj);
		return this;
	}

	
	public S3StringBuilder append(String str) {
		
		fb.append(str);
		return this;
	}

	
	public S3StringBuilder append(StringBuffer sb) {
		
		fb.append(sb);
		return this;
	}

	
	public S3StringBuilder append(CharSequence s) {
		
		fb.append(s);
		return this;
	}

	
	public S3StringBuilder append(CharSequence s, int start, int end) {
		
		fb.append(s, start, end);
		return this;
	}

	
	public S3StringBuilder append(char[] str) {
		
		fb.append(str);
		return this;
	}

	
	public S3StringBuilder append(char[] str, int offset, int len) {
		
		fb.append(str, offset, len);
		return this;
	}

	
	public S3StringBuilder append(boolean b) {
		
		fb.append(b);
		return this;
	}

	
	public S3StringBuilder append(char c) {
		
		fb.append(c);
		return this;
	}

	
	public S3StringBuilder append(int i) {
		
		fb.append(i);
		return this;
	}

	
	public S3StringBuilder append(long l) {
		
		fb.append(l);
		return this;
	}

	
	public S3StringBuilder append(float f) {
		
		fb.append(f);
		return this;
	}

	
	public S3StringBuilder append(double d) {
		
		fb.append(d);
		return this;
	}

	
	public S3StringBuilder delete(int start, int end) {
		
		fb.delete(start, end);
		return this;
	}

	
	public S3StringBuilder appendCodePoint(int codePoint) {
		
		fb.appendCodePoint(codePoint);
		return this;
	}

	
	public S3StringBuilder deleteCharAt(int index) {
		
		fb.deleteCharAt(index);
		return this;
	}

	
	public S3StringBuilder replace(int start, int end, String str) {
		
		fb.replace(start, end, str);
		return this;
	}

	
	public String substring(int start) {
		
		return fb.substring(start);
	}

	
	public CharSequence subSequence(int start, int end) {
		
		return fb.subSequence(start, end);
	}

	
	public String substring(int start, int end) {
		
		return fb.substring(start, end);
	}

	
	public S3StringBuilder insert(int index, char[] str, int offset, int len) {
		
		fb.insert(index, str, offset, len);
		return this;
	}

	
	public S3StringBuilder insert(int offset, Object obj) {
		
		fb.insert(offset, obj);
		return this;
	}

	
	public S3StringBuilder insert(int offset, String str) {
		
		fb.insert(offset, str);
		return this;
	}

	
	public S3StringBuilder insert(int offset, char[] str) {
		
		fb.insert(offset, str);
		return this;
	}

	
	public S3StringBuilder insert(int dstOffset, CharSequence s) {
		
		fb.insert(dstOffset, s);
		return this;
	}

	
	public S3StringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
		
		fb.insert(dstOffset, s, start, end);
		return this;
	}

	
	public S3StringBuilder insert(int offset, boolean b) {
		
		fb.insert(offset, b);
		return this;
	}

	
	public S3StringBuilder insert(int offset, char c) {
		
		fb.insert(offset, c);
		return this;
	}

	
	public S3StringBuilder insert(int offset, int i) {
		
		fb.insert(offset, i);
		return this;
	}

	
	public S3StringBuilder insert(int offset, long l) {
		
		fb.insert(offset, l);
		return this;
	}

	
	public S3StringBuilder insert(int offset, float f) {
		
		fb.insert(offset, f);
		return this;
	}

	
	public S3StringBuilder insert(int offset, double d) {
		
		fb.insert(offset, d);
		return this;
	}

	
	public int indexOf(String str) {
		
		return fb.indexOf(str);
	}

	
	public int indexOf(String str, int fromIndex) {
		
		return fb.indexOf(str, fromIndex);
	}

	
	public int lastIndexOf(String str) {
		
		return fb.lastIndexOf(str);
	}

	
	public int lastIndexOf(String str, int fromIndex) {
		
		return fb.lastIndexOf(str, fromIndex);
	}

	
	public S3StringBuilder reverse() {
		
		fb.reverse();
		return this;
	}
	
	public void clear(){
		fb=new StringBuilder();
	}

	
	public String toString() {
		
		return fb.toString();
	}

	public void appendLn(String aStr){
		append(aStr);
		append("\r\n");
	}
	
	public void appendFormat(String aFormat,Object... args){
		append(String.format(aFormat, args));
	}

	public void appendFormatLn(String aFormat,Object... args){
		appendLn(String.format(aFormat, args));
	}

}
