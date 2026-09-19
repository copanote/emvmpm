package com.copanote.emvmpm.definition.packager;

import com.copanote.emvmpm.definition.DataObjectDef;
import com.copanote.emvmpm.definition.EmvMpmDefinition;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
 * {@link EmvMpmDefinition}을 생성하는 packager.
 *
 * <p>{@link String} 경로, {@link File}, {@link InputStream}, 또는 프로그래밍 방식의
 * {@link DataObjectDef}[]/{@link List}를 입력으로 받는다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * EmvMpmPackager packager = new EmvMpmPackager();
 * packager.setEmvMpmPackager("emvmpm_bc.xml");
 * EmvMpmDefinition definition = packager.create();
 * }</pre>
 */
public class EmvMpmPackager {

    private List<DataObjectDef> FIELDS = new ArrayList<DataObjectDef>();

    /** 필드 정의가 비어 있는 packager를 생성한다. {@code setEmvMpmPackager(...)} 계열 메서드로 필드를 채운 뒤 {@link #create()}를 호출한다. */
    public EmvMpmPackager() {}

    /**
     * 지금까지 설정된 필드 정의로 {@link EmvMpmDefinition}을 생성한다.
     *
     * @return 생성된 definition
     */
    public EmvMpmDefinition create() {

        return EmvMpmDefinition.of(this.FIELDS);
    }

    /**
     * 필드 정의 배열로 packager를 구성한다.
     *
     * @param fields 최상위 필드 정의 배열
     */
    public void setEmvMpmPackager(DataObjectDef[] fields) {
        FIELDS = Arrays.asList(fields);
    }

    /**
     * 필드 정의 목록을 기존 필드에 추가한다.
     *
     * @param fields 추가할 최상위 필드 정의 목록
     */
    public void setEmvMpmPackager(List<DataObjectDef> fields) {
        FIELDS.addAll(fields);
    }

    /**
     * emvmpm 정의 XML 파일 경로로 packager를 구성한다.
     *
     * @param path emvmpm 정의 XML 파일 경로
     * @throws ParserConfigurationException XML 파서를 구성할 수 없는 경우
     * @throws SAXException XML 파싱에 실패한 경우
     * @throws IOException 파일을 읽을 수 없는 경우
     */
    public void setEmvMpmPackager(String path) throws ParserConfigurationException, SAXException, IOException {
        configure(parse(path));
    }

    /**
     * emvmpm 정의 XML 파일로 packager를 구성한다.
     *
     * @param file emvmpm 정의 XML 파일
     * @throws IOException 파일을 읽을 수 없는 경우
     * @throws SAXException XML 파싱에 실패한 경우
     * @throws ParserConfigurationException XML 파서를 구성할 수 없는 경우
     */
    public void setEmvMpmPackager(File file) throws IOException, SAXException, ParserConfigurationException {
        configure(parse(file));
    }

    /**
     * emvmpm 정의 XML을 담은 입력 스트림으로 packager를 구성한다.
     *
     * @param inputStream emvmpm 정의 XML 입력 스트림
     * @throws ParserConfigurationException XML 파서를 구성할 수 없는 경우
     * @throws IOException 스트림을 읽을 수 없는 경우
     * @throws SAXException XML 파싱에 실패한 경우
     */
    public void setEmvMpmPackager(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException {
        configure(parse(inputStream));
    }

    private Document parse(String path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(path);
    }

    private Document parse(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    private Document parse(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }

    private void configure(Document doc) {
        NodeList rootNodeList = doc.getElementsByTagName("mpmpackager");
        if (rootNodeList.getLength() < 1) {
            throw new IllegalArgumentException("There is no mpmpackager element");
        }

        Node mpmpackager = rootNodeList.item(0);
        FIELDS = configure(mpmpackager);
    }

    private List<DataObjectDef> configure(Node mpmpackager) {
        List<DataObjectDef> result = new ArrayList<DataObjectDef>();
        NodeList dataObject = mpmpackager.getChildNodes();

        for (int i = 0; i < dataObject.getLength(); i++) {
            Node element = dataObject.item(i);
            if (element.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            NamedNodeMap node = element.getAttributes();

            String id = node.getNamedItem("id").getNodeValue();
            String name = node.getNamedItem("name").getNodeValue();
            String maxlength = node.getNamedItem("maxlength").getNodeValue();
            int ml = Integer.parseInt(maxlength);
            String type = node.getNamedItem("type").getNodeValue();

            if (DataObjectDef.Type.TEMPLATE.toString().equalsIgnoreCase(type)) {
                List<DataObjectDef> children = configure(element);
                result.add(new DataObjectDef(id, name, ml, DataObjectDef.Type.TEMPLATE, children));
            } else {
                result.add(new DataObjectDef(id, name, ml, DataObjectDef.Type.PRIMITIVE));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "EmvMpmPackager [FIELDS=" + FIELDS + "]";
    }
}
