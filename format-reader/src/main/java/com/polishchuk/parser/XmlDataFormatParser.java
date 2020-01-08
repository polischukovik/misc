package com.polishchuk.parser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.polishchuk.ParseException;
import com.polishchuk.entity.Car;
import com.polishchuk.model.DataFormatModel;

public class XmlDataFormatParser<T> implements Parser {
	
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
	
	private T parse(Node node, DataFormatModel model) throws ParseException {  
		T data;
		
		if(model.isSimpleType()) {
			String content = getTextContent(node);
			System.out.println(String.format("+ found entry: <%s> value: %s", model.name, content));
			
			try {
				data = castContent(model.createInstance(), content);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new ParseException("Unable to create instance for simple type " + model);
			}    		
    		return data;
    	}
		
		// if Class
		try {
			data = model.createInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new ParseException("Unable to initialize instance " + model , e);
		}
		
		NodeList nodeList = node.getChildNodes();			
        System.out.println("Iterating over model " + model);                
        for(DataFormatModel subModel : model.getEntityModel()) {
//        	System.out.println(String.format("model entity %s %s", subModel, subModel.isInlineCollection ? "inline" : ""));

    		final Node subNode = findNode(subModel.name, nodeList);
    		
    		if(subNode != null) {
    	        
    			if(subModel.isCollection()) {
            		
    				System.out.println(String.format("+ found collection <%s>", subModel.name));
    			} else {
//    				System.out.println(String.format("+ found node <%s>", subModel.name));
    				setField(data, model, subModel, parse(subNode, subModel));
    			}     	
    		}
        }
        
        return data;        
	}

	private <V,R> void setField(V instance, DataFormatModel parent, DataFormatModel field, R value) {
		String setterName = "set" + field.name;
		System.out.println(String.format("Setting value with setter %s", setterName));
		try {
			parent.setValue(instance, setterName, field, value);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new ParseException("Unable to set value" + field  + " for " + parent);
		}
	}

	private boolean nodeExists(Node node, String name) {
		return node.getNodeType() == Node.ELEMENT_NODE
				&& node.getNodeName().equals(name);
	}

	@SuppressWarnings("unchecked")
	private T castContent(T obj, String nodeContent) {
		if(obj instanceof String) {
			return (T) nodeContent;
		} else if (obj instanceof Integer) {
			return (T) Integer.valueOf(nodeContent);
		} 
		throw new ParseException("Unexpected simple type ");
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
