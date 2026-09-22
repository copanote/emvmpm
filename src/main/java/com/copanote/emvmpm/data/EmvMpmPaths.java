package com.copanote.emvmpm.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * data tree({@link EmvMpmNode})와 definition tree({@code DataObjectDef})가 공유하는 canonical
 * path 파싱/포맷팅 유틸리티.
 *
 * <p>canonical path는 "/"로 구분되며 root를 나타내는 "/"로 시작한다 (예: "/62/50/00").
 */
public class EmvMpmPaths {
    private static final String ROOT_ID = "/";

    /** 인스턴스화를 막는 private 생성자. 이 클래스의 모든 멤버는 static이다. */
    private EmvMpmPaths() {}

    /**
     * root 노드를 나타내는 ID를 반환한다.
     *
     * @return root ID ("/")
     */
    public static String getRootId() {
        return ROOT_ID;
    }

    private static final String DELIMITER = "/";

    /**
     * canonical path의 구분자를 반환한다.
     *
     * @return 구분자 ("/")
     */
    public static String getDelimiter() {
        return DELIMITER;
    }

    /**
     * canonical path 문자열을 세그먼트 목록으로 분리한다. 빈 세그먼트는 제거하고, "/"로 시작하면 첫
     * 세그먼트를 root ID로 치환한다.
     *
     * @param mpmNodePath 파싱할 canonical path 문자열
     * @return 세그먼트 목록 (첫 요소는 root path인 경우 "/")
     */
    public static List<String> parsePath(String mpmNodePath) {
        String[] sa = mpmNodePath.split(DELIMITER);

        List<String> sl = Arrays.asList(sa);
        if (mpmNodePath.startsWith(ROOT_ID)) {
            if (sl.isEmpty()) {
                return Arrays.asList(ROOT_ID);
            } else {
                sl.set(0, ROOT_ID);
            }
        }

        List<String> result = new ArrayList<>(sl);

        return result.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    /**
     * 임의의 경로 문자열을 정규화된 canonical path 형태로 되돌린다. 중복 슬래시, 끝의 슬래시 등을
     * 정리한다.
     *
     * @param path 정규화할 경로 문자열
     * @return 정규화된 canonical path
     */
    public static String getEmvMpmPath(String path) {

        String joined = String.join(DELIMITER, parsePath(path));
        if (joined.startsWith("//")) {
            joined = joined.replaceFirst("//", "/");
        }

        return joined;
    }
}
