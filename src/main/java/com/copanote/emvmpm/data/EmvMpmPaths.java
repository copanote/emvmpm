package com.copanote.emvmpm.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmvMpmPaths {
	private static final String ROOT_ID = "/";
	public static String getRootId() {
		return ROOT_ID;
	}
	
	private static final String DELIMITER = "/";
	public static  String getDelimiter() {
		return DELIMITER;
	}
	
	public static List<String> parsePath(String mpmNodePath) {
		String[] sa = mpmNodePath.split(DELIMITER);
		
		List<String> sl = Arrays.asList(sa);
		if (mpmNodePath.startsWith(ROOT_ID)) {
			if (sl.size() == 0 ) {
				return Arrays.asList(ROOT_ID);
			} else {
				sl.set(0, ROOT_ID);
			}
		}
		
		List<String> result = new ArrayList<>();
		result.addAll(sl);
		
		return result.stream().filter(s-> !s.isEmpty()).collect(Collectors.toList());
	}
	
	public static String getEmvMpmPath(String path) {
		
		String joined = String.join(DELIMITER, parsePath(path));
		if (joined.startsWith("//")) {
			joined = joined.replaceFirst("//", "/");
		}
		
		return joined;
	}
	

}
