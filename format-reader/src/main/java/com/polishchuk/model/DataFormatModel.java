package com.polishchuk.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.polishchuk.ParseException;
import com.polishchuk.anotation.DataFormatDateAdaptor;
import com.polishchuk.anotation.DataFormatElement;

public class DataFormatModel {

	public String name;
	public Type type; 
	public List<DataFormatModel> components;
	public boolean isInlineCollection;
	public String datePattern;
	
	private DataFormatModel(String name, Type type, boolean isInlineCollection, String datePattern){
		this.name = name;
		this.type = type;
		this.components = new ArrayList<>(); 
		this.isInlineCollection = isInlineCollection;
		this.datePattern = datePattern;
	}
	
	private static List<Class<?>> simpleTypes = Arrays.asList(String.class, Integer.class);
	
	public static DataFormatModel from(TypeReference<?> typeReference) {
		return from(null, typeReference, false);
	}	
	
	private static DataFormatModel from(String name, TypeReference<?> typeRef, boolean isInlineCollection) throws ParseException {
		Type type = typeRef.getType();
		return DataFormatModel.from(name, type, isInlineCollection, null);
	}
	
	private static DataFormatModel from(String name, Type type, boolean isInlineCollection, String datePattern) throws ParseException {
		DataFormatModel node = new DataFormatModel(name, type, isInlineCollection, datePattern);
		
		if (simpleTypes.contains(type)){
			return node;
		} else if(type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			
			// Process collections
			if (Collection.class.isAssignableFrom((Class<?>) pt.getRawType())) {
				
				node.components.add(from(
						"_items"
						, pt.getActualTypeArguments()[0]
						, false
						, null
						));
				return node;
			} else {
				throw new ParseException("For parametrized types only Collections are allowed");
			}
		} else {
			Method[] methods = ((Class<?>) type).getDeclaredMethods();
			
			for(Method setter : methods) {
		    	if(setter.getName().startsWith("set") 
		    			&& setter.isAnnotationPresent(DataFormatElement.class)) {
		    		
		    		node.components.add(from(
		    				  getNameAnotationArgumentOrDefault(setter)
							, getArgType(setter)
							, getInlineCollectionAnotationArgument(setter)
							, getDatePatternAnotationArgiment(setter)
							));
		    	}
			}
		}
		
		return node;
	}

	private static String getDatePatternAnotationArgiment(Method setter) {
		DataFormatDateAdaptor[] anotation = setter.getAnnotationsByType(DataFormatDateAdaptor.class);
		if(anotation.length != 0){
			return anotation[0].value();
		} else {
			return "";
		}
	}

	private static boolean getInlineCollectionAnotationArgument(Method setter) {
		DataFormatElement[] anotation = setter.getAnnotationsByType(DataFormatElement.class);
		if(anotation.length != 0){
			return anotation[0].inlineCollection();
		} else {
			return false;
		}
	}

	private static String getNameAnotationArgumentOrDefault(Method setter) {
		DataFormatElement[] anotation = setter.getAnnotationsByType(DataFormatElement.class);
		if(anotation.length != 0 && !"".equals(anotation[0].value())){
			return anotation[0].value();
		} else {
			return setter.getName().replaceFirst("set", "").toLowerCase();
		}
	}

	private static Type getArgType(Method method) throws ParseException {
		Type[] methodArgs = method.getGenericParameterTypes();
		if(methodArgs.length != 1) 
			throw new ParseException("Inappropriate setter method " + method.getName() + ". One argument expeted");
		return methodArgs[0];			
	}

	public boolean isCollection() {
		if(type instanceof ParameterizedType) {
			return Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType());
		}
		return false;
	}
	
	public DataFormatModel[] getEntityModel() {
		if(this.isCollection()) {
        	assert this.components.size() == 0 : "Invalid model Colections should have one component '_list'";
        	return new DataFormatModel[] { this.components.get(0) };
        }

    	return this.components.toArray(new DataFormatModel[] {});
	}

	public boolean isSimpleType() {
		return simpleTypes.contains(type);
	}
	
	
	public String showModel() {
		return showModel(0);
	}

	private String showModel(int depth) {
		String indentation = "";
		for(int i = 0; i < depth; i++) {
			indentation += "\t";
		}
		
		StringBuilder sb = new StringBuilder();

		sb.append(indentation).append(name).append("/").append(type.getTypeName()).append(" ");
		if(components.size() == 0) {
			sb.append("{}\n");
			return sb.toString();
		} 

		sb.append("{\n");
		for(DataFormatModel node : components) {
				sb.append(node.showModel(depth + 1));
		}
		sb.append(indentation).append("}\n");	
		
		return sb.toString();
	}

	@Override
	public String toString() {
		return name + "/" + type.getTypeName();
	}

	@SuppressWarnings("unchecked")
	public <V> V createInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		switch (this.type.getTypeName()) {
			case "java.lang.String":
				return (V) "";
			case "java.lang.Integer":
				return (V) Integer.valueOf(0);
			default:
				break;
		}
		
		Constructor<V>[] ctors = (Constructor<V>[]) ((Class<?>) this.type).getDeclaredConstructors();
		Constructor<V> ctor = null;
		
		for (int i = 0; i < ctors.length; i++) {
		    ctor = ctors[i];
		    if (ctor.getGenericParameterTypes().length == 0)
			break;
		}
		
		ctor.setAccessible(true);
 	    V instance = ctor.newInstance();
		
		return instance;
	}

	public <V, R> void setValue(V instance, String setterName, DataFormatModel field, R value) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method setter = instance.getClass().getMethod(setterName, value.getClass());
		setter.invoke(instance, value);		
	}
	
}
