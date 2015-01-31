package com.guntzergames.medievalwipeout.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class Packet {

	private List<CollectionElement> elementsNotInCollection;
	private List<CollectionElement> elementsAlreadyInCollection;
	
	public List<CollectionElement> getElementsNotInCollection() {
		return elementsNotInCollection;
	}

	public void setElementsNotInCollection(List<CollectionElement> elementsNotInCollection) {
		this.elementsNotInCollection = elementsNotInCollection;
	}

	public List<CollectionElement> getElementsAlreadyInCollection() {
		return elementsAlreadyInCollection;
	}

	public void setElementsAlreadyInCollection(List<CollectionElement> elementsAlreadyInCollection) {
		this.elementsAlreadyInCollection = elementsAlreadyInCollection;
	}
	
	@JsonIgnore
	public int getSize() {
		return (elementsNotInCollection == null ? 0 : elementsNotInCollection.size())
				+ (elementsAlreadyInCollection == null ? 0 : elementsAlreadyInCollection.size());
	}

	public String toJson() {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		mapper.enable(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, this);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = new String(out.toByteArray());
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	
	}

	public static Packet fromJson(String json) {

		ObjectMapper mapper = new ObjectMapper();
		Packet packet = null;
		try {
			packet = mapper.readValue(json, Packet.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packet;

	}
	
}
