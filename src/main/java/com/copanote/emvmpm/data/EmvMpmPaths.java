package com.copanote.emvmpm.data;

import java.util.Arrays;
import java.util.List;

public class EmvMpmPaths {
	
	public static List<String> parsePath(String mpmNodePath) {
		String[] sa = mpmNodePath.split("/");
		List<String> sl = Arrays.asList(sa);
		if (mpmNodePath.startsWith("/")) {
			if (sl.size() == 0 ) {
				return Arrays.asList("/");
			} else {
				sl.set(0, "/");
			}
		}
		return sl;
	}
	
	public static String getEmvMpmPath(String path) {
		return String.join("", parsePath(path));
	}
	

}
