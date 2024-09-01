package rzd.application.hierarchy;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final Map<String, String> mapAddress = new HashMap<>();
    private static final Map<String, String> mapHierarchy = new HashMap<>();

    public static void main(String[] args) {
        parseAddress("AS_ADDR_OBJ.XML");
        parseHierarchy("AS_ADM_HIERARCHY.XML");
        filteredAddresses("проезд").forEach(System.out::println);
    }

    public static void parseAddress(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList listNode = document.getElementsByTagName("OBJECT");

            for (int i = 0; i < listNode.getLength(); i++) {
                Node node = listNode.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String objectId = element.getAttribute("OBJECTID");
                    String name = element.getAttribute("NAME");
                    String typeName = element.getAttribute("TYPENAME");
                    String isActive = element.getAttribute("ISACTIVE");

                    if (isActive.equals("1")) {
                        mapAddress.put(objectId, typeName + " " + name);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void parseHierarchy(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList listNode = document.getElementsByTagName("ITEM");

            for (int i = 0; i < listNode.getLength(); i++) {
                Node node = listNode.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String objectId = element.getAttribute("OBJECTID");
                    String parentObjId = element.getAttribute("PARENTOBJID");
                    String isActive = element.getAttribute("ISACTIVE");

                    if (isActive.equals("1") && !parentObjId.equals("0")) {
                        mapHierarchy.put(objectId, parentObjId);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> filteredAddresses(String addressType) {
        List<String> listFilteredAddresses = new ArrayList<>();

        for (Map.Entry<String, String> entry : mapAddress.entrySet()) {
            String address = buildFullAddress(entry.getKey());
            if (address.contains(addressType)) {
                listFilteredAddresses.add(address);
            }
        }
        return listFilteredAddresses;
    }

    public static String buildFullAddress(String objectId) {
        StringBuilder address = new StringBuilder();
        String currentId = objectId;

        while (currentId != null) {
            String addressPart = mapAddress.get(currentId);
            if (addressPart != null) {
                if (address.length() > 0) {
                    address.insert(0, ", ");
                }
                address.insert(0, addressPart);
            }
            currentId = mapHierarchy.get(currentId);
        }
        return address.toString();
    }
}