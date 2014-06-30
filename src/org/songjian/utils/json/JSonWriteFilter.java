package org.songjian.utils.json;

import static com.alibaba.fastjson.parser.CharTypes.replaceChars;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.alibaba.fastjson.parser.CharTypes;
import com.alibaba.fastjson.util.Base64;
import com.alibaba.fastjson.util.IOUtils;

public class JSonWriteFilter extends Writer {

	private Writer fWirter;
	private char fSeperator = ':';

	public JSonWriteFilter(Writer aWriter) {

		fWirter = aWriter;
	}

	public JSonWriteFilter(OutputStream aStream, String aCharsetName) throws UnsupportedEncodingException {
		fWirter = new OutputStreamWriter(aStream, aCharsetName);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		fWirter.write(cbuf, off, len);

	}

	@Override
	public void flush() throws IOException {
		fWirter.flush();
	}

	@Override
	public void close() throws IOException {
		fWirter.close();
	}

	public char[] expandCapacity(int aSize) {
		if (aSize <= 0)
			return null;
		return new char[aSize];
	}

	public void writeInt(int i) throws IOException {
		if (i == Integer.MIN_VALUE) {
			write("-2147483648");
			return;
		}

		int size = (i < 0) ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);
		char[] buf;
		buf = expandCapacity(size);

		IOUtils.getChars(i, size, buf);
		write(buf);

	}

	public void writeNull() throws IOException {
		write("null");
	}

	public void writeShortArray(short[] array) throws IOException {
		if (array == null) {
			writeNull();
			return;
		}
		writeArrayHead();

		for (int i = 0; i < array.length; ++i) {
			if (i != 0) {
				write(',');
			}
			writeInt(array[i]);
		}
		writeArrayEnd();
	}

	public final void println() throws IOException {
		fWirter.write("\r\n");
	}

	public void writeByteArray(byte[] bytes) throws IOException {
		int bytesLen = bytes.length;
		if (bytesLen == 0) {
			write("\"\"");
			return;
		}

		final char[] CA = Base64.CA;

		int eLen = (bytesLen / 3) * 3; // Length of even 24-bits.
		// char[] chars = new char[charsLen];
		write('\"');

		// Encode even 24-bits
		for (int s = 0; s < eLen;) {
			// Copy next three bytes into lower 24 bits of int, paying attension
			// to sign.
			int i = (bytes[s++] & 0xff) << 16 | (bytes[s++] & 0xff) << 8 | (bytes[s++] & 0xff);
			write(CA[(i >>> 18) & 0x3f]);
			write(CA[(i >>> 12) & 0x3f]);
			write(CA[(i >>> 6) & 0x3f]);
			write(CA[i & 0x3f]);
		}

		// Pad and encode last bits if source isn't even 24 bits.
		int left = bytesLen - eLen; // 0 - 2.
		if (left > 0) {
			// Prepare the int
			int i = ((bytes[eLen] & 0xff) << 10) | (left == 2 ? ((bytes[bytesLen - 1] & 0xff) << 2) : 0);

			// Set last four chars
			write(CA[i >> 12]);
			write(CA[(i >>> 6) & 0x3f]);
			write(left == 2 ? CA[i & 0x3f] : '=');
			write('=');
		}
		write('\"');
	}

	private void doWriteStringWithQuote(String text, char aQuote) throws IOException {
		if (text == null) {
			writeNull();
			return;
		}

		int len = text.length();

		int start = 0;
		int end = start + len;

		write(aQuote);

		char[] buf = text.toCharArray();

		for (int i = start; i < end; ++i) {
			char ch = buf[i];

			if (ch == '"' || ch == '/' || ch == '\\') {
				write('\\');
				write(replaceChars[(int) ch]);
				continue;
			}

			if (ch == '\b' || ch == '\f' || ch == '\n' || ch == '\r' || ch == '\t') {
				continue;
			}

			if (ch < 32) {
				write('\\');
				write('u');
				write('0');
				write('0');
				write(CharTypes.ASCII_CHARS[ch * 2]);
				write(CharTypes.ASCII_CHARS[ch * 2 + 1]);
				continue;
			}

			if (ch >= 127 && ch <= 255) {
				write('\\');
				write('u');
				write(CharTypes.digits[(ch >>> 12) & 15]);
				write(CharTypes.digits[(ch >>> 8) & 15]);
				write(CharTypes.digits[(ch >>> 4) & 15]);
				write(CharTypes.digits[ch & 15]);
				continue;
			}
			write(ch);

		}

		write(aQuote);
		return;

	}

	private void writeStringWithDoubleQuote(String text) throws IOException {
		doWriteStringWithQuote(text, '\"');
	}

	@SuppressWarnings("unused")
	private void writeStringWithSingleQuote(String text) throws IOException {
		doWriteStringWithQuote(text, '\'');
	}

	private void writeBoolean(boolean aBoolean) throws IOException {
		if (aBoolean)
			fWirter.write("true");
		else
			fWirter.write("false");
	}

	public final void writeStringValue(String value) throws IOException {
		writeStringWithDoubleQuote(value);
	}
	public final void writeFieldName(String name) throws IOException {
		writeStringWithDoubleQuote(name);
		write(fSeperator);
	}

	public final void writeFieldValue(String name, String value) throws IOException {
		writeStringWithDoubleQuote(name);
		write(fSeperator);
		writeStringWithDoubleQuote(value);
	}

	public final void writeFieldValue(String name, Integer value) throws IOException {
		writeStringWithDoubleQuote(name);
		write(fSeperator);
		if (value == null)
			writeNull();
		else
			writeInt(value);
	}
	
	public final void writeFieldValue(String name, Boolean value) throws IOException {
		writeStringWithDoubleQuote(name);
		write(fSeperator);
		if (value == null)
			writeNull();
		else
			writeBoolean(value);
	}
	
    public final void writeObjHead() throws IOException {
    	write('{');
    }

    public final void writeObjEnd() throws IOException {
    	write('}');
    }
    
    public final void writeArrayHead()throws IOException {
    	write('[');
    }
    public final void writeArraySeperator()throws IOException {
    	write(',');
    }
    public final void writePropertySeperator()throws IOException {
    	write(',');
    }
    public final void writeArrayEnd()throws IOException {
    	write(']');
    }
    
    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JSonWriteFilter fFiler;
			fFiler = new JSonWriteFilter(System.out, "UTF-8");
			fFiler.writeInt(100000);
			short[] fShortArray;
			fFiler.println();
			fShortArray = new short[] { 22, 33, 44, 55 };
			// fShortArray=null;
			fFiler.writeShortArray(fShortArray);
			fFiler.println();
			fFiler.writeByteArray("1234567".getBytes());
			fFiler.println();
			fFiler.write("{");
			fFiler.writeFieldValue("测试信\"息1212", "");
			fFiler.write("}");
			fFiler.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
