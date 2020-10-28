package com.terrance.Lesson4XML;

import org.w3c.dom.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;


public class Lesson4XML {

    private static File file = new File("JobResult_UCSDExt.xml");

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {

        System.out.println("Results of XML Parsing using DOM Parser:");
        printDomResults();

        System.out.println("Results of XML Parsing using SAX Parser:");
        printSaxResults();

        System.out.println("Results of XML Parsing using XPath:");
        printXPathResults();

        System.out.println("All Done!");

    }
    public static void printXPathResults() throws ParserConfigurationException, XPathExpressionException, IOException, SAXException {
        XPathFactory xpfactory = XPathFactory.newInstance();
        XPath path = xpfactory.newXPath();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);

        System.out.println("serial: " + path.evaluate("/jobresult/serial", doc));
        System.out.println("visible-string: " + path.evaluate("/jobresult/data/visible-string", doc));
        System.out.println("unsigned: " + path.evaluate("/jobresult/data/structure/unsigned", doc));
    }

    public static void printSaxResults() throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        DefaultHandler handler = new DefaultHandler() {
            String text = "";
            boolean flag = false;
            @Override
            public void startElement(String namespaceURI, String lname, String qname,
                                     Attributes attrs){

                if (qname.equalsIgnoreCase("serial") || qname.equalsIgnoreCase("visible-string")
                        ||qname.equalsIgnoreCase("unsigned")){
                    text = qname;
                    flag = true;
                }

            }
            @Override
            public void characters(char[] data, int start, int length) throws SAXException {
                if (flag) {
                    super.characters(data, start, length);
                    String value = new String(data, start, length);
                    System.out.println(text + " : " + value);
                    flag = false;
                    text = "";
                }
            }
        };
        saxParser.parse(file, handler);
    }

    public static void printDomResults() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        //File file = new File("JobResult_UCSDExt.xml");
        Document doc = builder.parse(file);
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child instanceof Element) { //get serial text

                Element childElement = (Element) child;
                Text textNode = (Text) childElement.getFirstChild();
                String text = textNode.getData().trim();
                NodeList grandChildren = childElement.getChildNodes();

                if (childElement.getTagName().equals("serial"))
                    System.out.println(childElement.getTagName() + ": " + text);
                else if (childElement.getTagName().equals("data") && childElement.hasChildNodes())
                    for (int j = 0; j < grandChildren.getLength(); j++) {
                        Node gc = grandChildren.item(j);
                        if (gc instanceof Element) {
                            Element gcElement = (Element) gc;
                            Text textNodeGc = (Text) gcElement.getFirstChild();
                            String textGc = textNodeGc.getData().trim();
                            if (gcElement.getTagName().equals("visible-string"))
                                System.out.println(gcElement.getTagName() + ": " + textGc);
                            else if(gcElement.getTagName().equals("structure") && gcElement.hasChildNodes()){
                                NodeList ggchildren = gcElement.getChildNodes();
                                for (int k = 0; k < 1; k++){
                                    Node gcc = ggchildren.item(j);
                                    if (gcc instanceof Element){
                                        Element gccElement = (Element) gcc;
                                        Text textNodeGcc = (Text) gccElement.getFirstChild();
                                        String textGcc = textNodeGcc.getData().trim();
                                        if (gccElement.getTagName().equals("unsigned"))
                                            System.out.println(gccElement.getTagName() + ": " + textGcc);
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }


}
