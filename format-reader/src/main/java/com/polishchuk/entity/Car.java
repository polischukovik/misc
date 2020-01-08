package com.polishchuk.entity;

import java.time.LocalDate;

import com.polishchuk.anotation.DataFormatDateAdaptor;
import com.polishchuk.anotation.DataFormatElement;

public class Car {
		private String brandName;
		private LocalDate date;
		private Integer price;
		
		public String getBarandName() {
			return brandName;
		}
		
		@DataFormatElement("BrandName")
		public void setBrandName(String brandName) {
			this.brandName = brandName;
		}
		
		public LocalDate getDate() {
			return date;
		}
		
		@DataFormatElement("Date")
		@DataFormatDateAdaptor("dd.mm.yyyy")
		public void setDate(LocalDate date) {
			this.date = date;
		}
		
		public Integer getPrice() {
			return price;
		}
		
		@DataFormatElement("Price")
		public void setPrice(Integer price) {
			this.price = price;
		}
		
		@Override
		public String toString() {
			return "Car [brandName=" + brandName + ", date=" + date + ", price=" + price + "]";
		}
	}