package com.djhs16.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class StreamUtils {
	private final static int bufferSize = 1024;
	private static byte[] byteBuffer = new byte[bufferSize];
	private static char[] charBuffer = new char[bufferSize];

	public static void copyStream(InputStream is, OutputStream os) {

		try {
			for (;;) {
				int count = is.read(byteBuffer, 0, bufferSize);
				if (count == -1) {
					break;
				}
				os.write(byteBuffer, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public synchronized static String readData(InputStreamReader rd) {
		try {
			StringBuffer sb = new StringBuffer();
			while (true) {
				int read = rd.read(charBuffer, 0, bufferSize);
				if (read == -1)
					break;
				sb.append(charBuffer, 0, read);
			}
			return sb.toString();
		} catch (IOException e) {
		} finally {
			try {
				rd.close();
			} catch (IOException e) {
			}
		}
		return "";
	}
}