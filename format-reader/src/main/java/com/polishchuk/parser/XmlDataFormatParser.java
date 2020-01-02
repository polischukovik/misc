package com.polishchuk.parser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.polishchuk.ParseException;
import com.polishchuk.entity.Car;
import com.polishchuk.entity.Data;
import com.polishchuk.model.DataFormatModel;

public class XmlDataFormatParser<T> implements Parser<T> {
	
	private DocumentBuilder builder;
	private String rootElementName = "root";

	public XmlDataFormatParser() {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// should not happen
			e.printStackTrace();
		}
	}
	
	public XmlDataFormatParser(String rootElementName) {
		this.rootElementName = rootElementName;
		
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// should not happen
			e.printStackTrace();
		}
	}
	
	public T parse(String path, DataFormatModel model) throws ParseException {
		Document doc;
		try {
			doc = builder.parse(new File("src/main/resources/" + path));
		} catch (SAXException | IOException e) {
			throw new ParseException(e);
		}
        Node root = doc.getDocumentElement();
        root.normalize();

        System.out.println("root node: " + root.getNodeName());
        // for XML there would be always root node
        model.name = root.getNodeName();
        
        return parse(root, model);
	}
	
	private <V> V parse(Node node, DataFormatModel model) throws ParseException {  
		if(nodeExists(node, model.name)) {
			return null;
		}
		
		V data;
		
		try {
			data = model.createInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		NodeList nodeList = node.getChildNodes();	
		
		if(model.isSimpleType()) {
			final Node subNode = findNode(model.name, nodeList);
			if(node == null) return null;
			
    		final String nodeContent = getTextContent(node);
    		System.out.println(String.format("+ found entry: <%s> value: %s", model.name, nodeContent));
    		
    		try {
				V template = model.<V>createInstance();
				data = castValue(template, nodeContent);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new ParseException("Failed to initialize instance", e);
			}
    		
    	}
		
        System.out.println("Iterating over model " + model);                
        for(DataFormatModel subModel : model.getEntityModel()) {
        	System.out.println(String.format("model entity %s %s", subModel, subModel.isInlineCollection ? "inline" : ""));

    		final Node dubNode = findNode(subModel.name, nodeList);
    		
    		if(node != null) {
    			
    			//description
    	        V obj = null;
    	        
    			if(subModel.isCollection()) {
            		
    				System.out.println(String.format("+ found collection <%s>", subModel.name));
    			} else {
    				
    				System.out.println(String.format("+ found node <%s>", subModel.name));	
    			}     	
    		}
        }
        
        return null;        
	}

	private boolean nodeExists(Node node, String name) {
		return node.getNodeType() == Node.ELEMENT_NODE
				&& node.getNodeName().equals(name);
	}

	@SuppressWarnings("unchecked")
	private <V> V castValue(V obj, String nodeContent) {
		if(obj instanceof String) {
			return (V) nodeContent;
		} else if (obj instanceof Integer) {
			return (V) Integer.valueOf(nodeContent);
		} 
		return null;
	}

	private String getTextContent(Node node) {
		return node.hasChildNodes() ? node.getFirstChild().getTextContent() : "";
	}

	private Node findNode(String nodeName, NodeList nodeList) {
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			
			if(node.getNodeType() == Node.ELEMENT_NODE 
				&& node.getNodeName().equals(nodeName)) {
				
				return node;
			}
		}
		return null;
	}

	private void parseLevel(NodeList list, DataFormatModel[] dataFormatModels) {	            
		for(int i = 0; i < list.getLength(); i++) {
		  	Node elem = list.item(i);
		  	if(elem.getNodeType() == Node.ELEMENT_NODE) {
		  		System.out.println("  " + elem.getNodeName());	
				Car car = null;
				if("Car".equals(elem.getNodeName())) {
					 car = new Car();
				}
		  	}
		}
	}


}
