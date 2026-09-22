package com.copanote.emvmpm.definition;

import com.copanote.emvmpm.data.EmvMpmPaths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 변경 불가능하고 검색 가능한 {@link DataObjectDef} 컬렉션.
 *
 * <p>{@link #of(List)}로 생성하며, canonical path(예: "/26/00")로 {@link DataObjectDef}를
 * 조회할 수 있다.
 */
public class EmvMpmDefinition {
    // Data Source
    private final List<DataObjectDef> definitionList;

    // need Root Node?
    /**
     * 최상위 필드 정의 목록으로 definition을 생성한다. 전달받은 목록은 방어적으로 복사되므로, 호출자가
     * 이후 원본 목록을 수정하더라도 이미 생성된 definition은 영향을 받지 않는다.
     *
     * @param definitionList 최상위 필드 정의 목록
     */
    public EmvMpmDefinition(List<DataObjectDef> definitionList) {
        this.definitionList = Collections.unmodifiableList(new ArrayList<>(definitionList));
    }

    /**
     * 필드 정의 목록을 검증한 뒤 {@link EmvMpmDefinition}을 생성한다.
     *
     * @param definitionList 최상위 필드 정의 목록
     * @return 생성된 definition
     * @throws IllegalArgumentException definitionList가 null이거나 비어 있는 경우
     */
    public static EmvMpmDefinition of(List<DataObjectDef> definitionList) {
        if (definitionList == null || definitionList.isEmpty()) {
            throw new IllegalArgumentException("argument must not be null or empty");
        }

        return new EmvMpmDefinition(definitionList);
    }

    /**
     * canonical path로 필드 정의를 조회한다.
     *
     * @param canonicalId "/"로 구분된 canonical path (예: "/62/50/00")
     * @return 일치하는 필드 정의, 없으면 {@link Optional#empty()}
     */
    public Optional<DataObjectDef> find(String canonicalId) {
        return _find(definitionList, EmvMpmPaths.getEmvMpmPath(canonicalId));
    }

    private Optional<DataObjectDef> _find(List<DataObjectDef> defs, String canonicalId) {
        Optional<DataObjectDef> result;

        for (DataObjectDef dataObjectDef : defs) {
            if (dataObjectDef.getCanonicalId().equalsIgnoreCase(canonicalId)) {
                return Optional.of(dataObjectDef);
            } else {
                if (dataObjectDef.isTemplate()) {
                    result = _find(dataObjectDef.getChildren(), canonicalId);
                    if (result.isPresent()) {
                        return result;
                    }
                }
            }
        }

        return Optional.empty();
    }

    /**
     * 주어진 canonical path에 해당하는 필드가 template인지 판별한다.
     *
     * @param canonicalId "/"로 구분된 canonical path
     * @return 필드가 존재하고 template 타입이면 true
     */
    public boolean isTemplate(String canonicalId) {
        Optional<DataObjectDef> dod = find(canonicalId);
        return dod.filter(dataObjectDef -> DataObjectDef.Type.TEMPLATE == dataObjectDef.getType())
                .isPresent();
    }

    /**
     * 이 definition이 보유한 필드 정의 목록을 문자열로 출력한다.
     *
     * @return 필드 정의 목록의 문자열 표현
     */
    public String printDefinition() {
        return definitionList.toString();
    }

    @Override
    public String toString() {
        return "EmvMpmDefinition [definitionList=" + definitionList + "]";
    }
}
