package com.polishchuk;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.polishchuk.entity.Car;
import com.polishchuk.model.TypeReference;
import com.polishchuk.parser.XmlDataFormatParser;

public class App {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ParseException {

//		DataFormat<Data> parser = new DataFormat<>();
		DataFormat<Car> parser = new DataFormat<>();
		Car data = parser.parse("sample.xml"
				, new TypeReference<Car>() {}
				, new XmlDataFormatParser());
	
		System.out.println(data);
		
//		Data<DataFormatParser.Car> data1 = parser.parseBin("sample.bin");
//		Data<DataFormatParser.Car> data2 = parser.parse("sample.json", new JsonDataFormatParser<DataFormatParser.Car>());
	}

}

