package com.copanote.emvmpm.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
		
		List<String> result = new ArrayList<>();
		result.addAll(sl);
		
		return result.stream().filter(s-> !s.isEmpty()).collect(Collectors.toList());
	}
	
	public static String getEmvMpmPath(String path) {
		
		String joined = String.join("/", parsePath(path));
		if (joined.startsWith("//")) {
			joined = joined.replaceFirst("//", "/");
		}
		
		return joined;
	}
	

}
