package com.polishchuk;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.polishchuk.model.DataFormatModel;
import com.polishchuk.model.TypeReference;
import com.polishchuk.parser.Parser;
import com.polishchuk.parser.XmlDataFormatParser;

public class DataFormat<T> {
	
	private Parser<T> xmlDataFormatParser;
	private Parser<T> binaryDataFormatParser;
	
	public DataFormat() throws ParserConfigurationException {
		this.xmlDataFormatParser =  new XmlDataFormatParser<>();
//		this.binaryDataFormatParser = new BinaryDataFormatParser<>();
	}

//	public T parseXml(String string) throws ParserConfigurationException, SAXException, IOException, ParseException{
//		return parse(string, null, xmlDataFormatParser);
//		
//	};
//
//	public T parseBin(String string) throws ParserConfigurationException, SAXException, IOException, ParseException {
//		return parse(string, null, binaryDataFormatParser);
//	}
	
	public T parse(String path
			, TypeReference<T> typeReference
			, Parser<T> parser) throws ParserConfigurationException, SAXException, IOException, ParseException {
		
        DataFormatModel model = DataFormatModel.from(typeReference);
        System.out.println(model.showModel());
        
		return parser.parse(path, model);
	}


}
