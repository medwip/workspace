package com.guntzergames.medievalwipeout.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Entity
@Table(name = "ACCOUNT")
@NamedQueries({ @NamedQuery(name = Account.NQ_FIND_BY_FACEBOOK_USER_ID, query = "SELECT a FROM Account a WHERE a.facebookUserId = :facebookUserId") })
public class Account {

	public final static String NQ_FIND_BY_FACEBOOK_USER_ID = "NQ_FIND_ACCOUNT_BY_FACEBOOK_USER_ID";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private long id;

	@Column(name = "FACEBOOK_USER_ID")
	private String facebookUserId;
	
	@Column(name = "LEVEL")
	private int level;

	@Column(name = "BOT")
	private boolean bot;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
	private List<DeckTemplate> deckTemplates;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<CollectionElement> collectionElements;

	@Transient
	private boolean botAccount = false;

	public String getFacebookUserId() {
		return facebookUserId;
	}

	public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isBot() {
		return bot;
	}

	public void setBot(boolean bot) {
		this.bot = bot;
	}

	public List<DeckTemplate> getDeckTemplates() {
		return deckTemplates;
	}

	public List<CollectionElement> getCollectionElements() {
		return collectionElements;
	}

	public void setCollectionElements(List<CollectionElement> collectionElements) {
		this.collectionElements = collectionElements;
	}

	public void setDeckTemplates(List<DeckTemplate> deckTemplates) {
		this.deckTemplates = deckTemplates;
	}

	public boolean isBotAccount() {
		return botAccount;
	}

	public void setBotAccount(boolean botAccount) {
		this.botAccount = botAccount;
	}
	
	public static Account fromJson(String json) {
		
		ObjectMapper mapper = new ObjectMapper();
    	Account account = null;
    	try {
    		account = mapper.readValue(json, Account.class);
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
    	return account;
		
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

	@Override
	public String toString() {
		return String.format("%s %s %s", id, facebookUserId, botAccount);
	}

}
