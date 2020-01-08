package com.polishchuk.parser;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.polishchuk.ParseException;
import com.polishchuk.model.DataFormatModel;

public interface Parser {
	
	public <T> T parse(String path, DataFormatModel model) throws ParserConfigurationException, SAXException, IOException, ParseException;

}
