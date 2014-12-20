package com.guntzergames.medievalwipeout.beans;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class CardModelList {

	private List<CardModel> cardModels;

	public CardModelList() {
		
	}
	
	public CardModelList(List<CardModel> cardModels) {
		setCardModels(cardModels);
	}

	public List<CardModel> getCardModels() {
		return cardModels;
	}
	
	public void setCardModels(List<CardModel> cardModels) {
		this.cardModels = cardModels;
	}
	
	public static CardModelList fromJson(String json) {
		
		ObjectMapper mapper = new ObjectMapper();
		CardModelList cardModelList = null;
    	try {
    		cardModelList = mapper.readValue(json, CardModelList.class);
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
    	return cardModelList;
		
	}
	
}
