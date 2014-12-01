package com.guntzergames.medievalwipeout.beans;

import java.util.List;

import javax.persistence.Basic;
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

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
	private List<DeckTemplate> deckTemplates;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
	private List<CollectionElement> collectionElements;

	@Transient
	private boolean botAccount = false;

	public String getFacebookUserId() {
		return facebookUserId;
	}

	public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
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

	@Override
	public String toString() {
		return String.format("%s %s %s", id, facebookUserId, botAccount);
	}

}
