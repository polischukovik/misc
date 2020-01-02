package com.polishchuk.entity;

import java.time.LocalDate;

import com.polishchuk.anotation.DataFormatElement;

public class Car {
		private String barandName;
		private LocalDate date;
		private Integer price;
		
		public String getBarandName() {
			return barandName;
		}
		
		@DataFormatElement
		public void setBarandName(String barandName) {
			this.barandName = barandName;
		}
		
		public LocalDate getDate() {
			return date;
		}
		
		@DataFormatElement
		public void setDate(LocalDate date) {
			this.date = date;
		}
		
		public Integer getPrice() {
			return price;
		}
		
		@DataFormatElement
		public void setPrice(Integer price) {
			this.price = price;
		}
		
		@Override
		public String toString() {
			return "Car [barandName=" + barandName + ", date=" + date + ", price=" + price + "]";
		}
	}