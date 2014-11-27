package com.guntzergames.medievalwipeout.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name = "DECK_TEMPLATE")
@NamedQueries({ @NamedQuery(name = DeckTemplate.NQ_FIND_BY_ACCOUNT, query = "SELECT d FROM DeckTemplate d WHERE d.account = :account") })
public class DeckTemplate {

	public static final String NQ_FIND_BY_ACCOUNT = "NQ_FIND_DECK_TEMPLATES_BY_ACCOUNT";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private long id;

	@JsonIgnore
	@ManyToOne(targetEntity = Account.class)
	@JoinColumn(name = "ACCOUNT_KEY")
	private Account account;

	@Column(name = "DECK_LIBEL")
	private String deckLibel;

	@OneToMany(mappedBy = "deckTemplate", fetch = FetchType.EAGER)
	private List<DeckTemplateElement> cards;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDeckLibel() {
		return deckLibel;
	}

	public void setDeckLibel(String deckLibel) {
		this.deckLibel = deckLibel;
	}

	public DeckTemplate() {

	}

	public List<DeckTemplateElement> getCards() {
		return cards;
	}

	public void setCards(List<DeckTemplateElement> cards) {
		this.cards = cards;
	}

	public List<PlayerDeckCard> toPlayerDeckCards() {

		List<PlayerDeckCard> playerDeckCards = new ArrayList<PlayerDeckCard>();

		for ( DeckTemplateElement element : cards ) {
			playerDeckCards.addAll(element.toPlayerDeckCards());
		}

		return playerDeckCards;

	}

	public PlayerDeck toDeck() {
		PlayerDeck deck = new PlayerDeck();
		deck.setCards(toPlayerDeckCards());
		Collections.shuffle(deck.getCards());
		return deck;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
