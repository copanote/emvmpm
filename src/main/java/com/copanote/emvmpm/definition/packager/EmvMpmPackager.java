package com.copanote.emvmpm.definition.packager;

import com.copanote.emvmpm.EmvMpmException;
import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML({@code <mpmpackager>} 루트, 중첩된 {@code <dataobject id maxlength type>} 엘리먼트)로부터
 * {@link EmvMpmDefinition}을 생성하는 불변(immutable) packager.
 *
 * <p>{@link String} 경로, {@link File}, {@link InputStream}, 또는 프로그래밍 방식의
 * {@link DataObjectDef}[]/{@link List} 중 하나의 소스를 골라 {@code of(...)} 정적 팩토리 메서드로
 * 인스턴스를 생성한 뒤, {@link #create()}로 {@link EmvMpmDefinition}을 얻는다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * EmvMpmPackager packager = EmvMpmPackager.of("emvmpm_bc.xml");
 * EmvMpmDefinition definition = packager.create();
 * }</pre>
 *
 * <p><b>스레드 안전성:</b> 이 클래스는 불변이며 생성 후 상태가 바뀌지 않으므로 스레드 안전하다.
 */
public final class EmvMpmPackager {

    private final List<DataObjectDef> fields;

    private EmvMpmPackager(List<DataObjectDef> fields) {
        this.fields = Collections.unmodifiableList(new ArrayList<>(fields));
    }

    /**
     * 지금까지 설정된 필드 정의로 {@link EmvMpmDefinition}을 생성한다.
     *
     * @return 생성된 definition
     */
    public EmvMpmDefinition create() {
        return EmvMpmDefinition.of(this.fields);
    }

    /**
     * 필드 정의 배열로 packager를 생성한다.
     *
     * @param fields 최상위 필드 정의 배열
     * @return 생성된 packager
     */
    public static EmvMpmPackager of(DataObjectDef[] fields) {
        return new EmvMpmPackager(Arrays.asList(fields));
    }

    /**
     * 필드 정의 목록으로 packager를 생성한다.
     *
     * @param fields 최상위 필드 정의 목록
     * @return 생성된 packager
     */
    public static EmvMpmPackager of(List<DataObjectDef> fields) {
        return new EmvMpmPackager(fields);
    }

    /**
     * emvmpm 정의 XML 파일 경로로 packager를 생성한다.
     *
     * @param path emvmpm 정의 XML 파일 경로
     * @return 생성된 packager
     * @throws EmvMpmException XML 파서를 구성할 수 없거나, 파일을 읽을 수 없거나, XML 파싱에 실패한 경우
     */
    public static EmvMpmPackager of(String path) {
        try {
            return new EmvMpmPackager(configure(parse(path)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new EmvMpmException("Failed to load EMV MPM definition XML from path \"" + path + "\"", e);
        }
    }

    /**
     * emvmpm 정의 XML 파일로 packager를 생성한다.
     *
     * @param file emvmpm 정의 XML 파일
     * @return 생성된 packager
     * @throws EmvMpmException XML 파서를 구성할 수 없거나, 파일을 읽을 수 없거나, XML 파싱에 실패한 경우
     */
    public static EmvMpmPackager of(File file) {
        try {
            return new EmvMpmPackager(configure(parse(file)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new EmvMpmException("Failed to load EMV MPM definition XML from file \"" + file + "\"", e);
        }
    }

    /**
     * emvmpm 정의 XML을 담은 입력 스트림으로 packager를 생성한다.
     *
     * @param inputStream emvmpm 정의 XML 입력 스트림
     * @return 생성된 packager
     * @throws EmvMpmException XML 파서를 구성할 수 없거나, 스트림을 읽을 수 없거나, XML 파싱에 실패한 경우
     */
    public static EmvMpmPackager of(InputStream inputStream) {
        try {
            return new EmvMpmPackager(configure(parse(inputStream)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new EmvMpmException("Failed to load EMV MPM definition XML from input stream", e);
        }
    }

    private static Document parse(String path) throws ParserConfigurationException, IOException, SAXException {
        return newSecureDocumentBuilder().parse(path);
    }

    private static Document parse(File file) throws ParserConfigurationException, IOException, SAXException {
        return newSecureDocumentBuilder().parse(file);
    }

    private static Document parse(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException {
        return newSecureDocumentBuilder().parse(inputStream);
    }

    /**
     * XXE(XML External Entity) 공격을 막도록 DOCTYPE 선언과 외부 엔티티/외부 DTD 접근을 비활성화한
     * {@link DocumentBuilder}를 생성한다.
     *
     * @return XXE 방지 설정이 적용된 DocumentBuilder
     * @throws ParserConfigurationException 요청한 보안 설정을 파서가 지원하지 않는 경우
     */
    private static DocumentBuilder newSecureDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder();
    }

    private static List<DataObjectDef> configure(Document doc) {
        NodeList rootNodeList = doc.getElementsByTagName("mpmpackager");
        if (rootNodeList.getLength() < 1) {
            throw new EmvMpmException("There is no <mpmpackager> root element");
        }

        return configure(rootNodeList.item(0));
    }

    private static List<DataObjectDef> configure(Node mpmpackager) {
        List<DataObjectDef> result = new ArrayList<>();
        NodeList children = mpmpackager.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node element = children.item(i);
            if (element.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            NamedNodeMap attributes = element.getAttributes();

            String id = requireAttribute(element, attributes, "id");
            String name = requireAttribute(element, attributes, "name");
            String maxlength = requireAttribute(element, attributes, "maxlength");
            String type = requireAttribute(element, attributes, "type");

            int ml;
            try {
                ml = Integer.parseInt(maxlength);
            } catch (NumberFormatException e) {
                throw new EmvMpmException(
                        "Invalid \"maxlength\" attribute value \"" + maxlength + "\" on <dataobject id=\"" + id
                                + "\"> element",
                        e);
            }

            boolean isTemplate = DataObjectDef.Type.TEMPLATE.toString().equalsIgnoreCase(type);
            boolean isPrimitive = DataObjectDef.Type.PRIMITIVE.toString().equalsIgnoreCase(type);
            if (!isTemplate && !isPrimitive) {
                throw new EmvMpmException("Invalid \"type\" attribute value \"" + type + "\" on <dataobject id=\"" + id
                        + "\"> element (expected \"primitive\" or \"template\")");
            }

            if (isTemplate) {
                List<DataObjectDef> childDefs = configure(element);
                result.add(new DataObjectDef(id, name, ml, DataObjectDef.Type.TEMPLATE, childDefs));
            } else {
                result.add(new DataObjectDef(id, name, ml, DataObjectDef.Type.PRIMITIVE));
            }
        }
        return result;
    }

    /**
     * {@code <dataobject>} 엘리먼트에서 필수 속성 값을 읽는다.
     *
     * @param element 속성을 읽을 엘리먼트(오류 메시지 컨텍스트용)
     * @param attributes {@code element}의 속성 맵
     * @param attrName 읽을 속성 이름
     * @return 속성 값
     * @throws EmvMpmException 속성이 존재하지 않는 경우
     */
    private static String requireAttribute(Node element, NamedNodeMap attributes, String attrName) {
        Node attr = attributes.getNamedItem(attrName);
        if (attr == null) {
            throw new EmvMpmException(
                    "Missing required \"" + attrName + "\" attribute on <" + element.getNodeName() + "> element");
        }
        return attr.getNodeValue();
    }

    @Override
    public String toString() {
        return "EmvMpmPackager [fields=" + fields + "]";
    }
}
