package com.polishchuk.entity;

import java.util.ArrayList;
import java.util.List;

import com.polishchuk.anotation.DataFormatElement;

public class Data {
	
	private List<Car> cars;
	private String description;
	private Car car;

	public Data() {
		this.cars = new ArrayList<>();
	}

	public List<Car> getDocument() {
		return cars;
	}

	@DataFormatElement
	public void setCar(Car car) {
		this.car = car;
	}

	@DataFormatElement(inlineCollection = true)
	public void setCars(List<Car> document) {
		this.cars = document;
	}

	@DataFormatElement
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Document [document=" + cars + "]";
	}
	

}
