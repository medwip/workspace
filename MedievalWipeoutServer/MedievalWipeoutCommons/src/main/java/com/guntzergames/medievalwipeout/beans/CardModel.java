package com.guntzergames.medievalwipeout.beans;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "CARD_MODEL")
@NamedQueries(
{
	@NamedQuery(name = CardModel.NQ_FIND_ALL, query = "SELECT c FROM CardModel c"),
	@NamedQuery(name = CardModel.NQ_FIND_BY_REQUIRED_LEVEL, query = "SELECT c FROM CardModel c WHERE c.requiredLevel <= :requiredLevel"),
})
public class CardModel implements Comparable<CardModel> {

	public static final String NQ_FIND_ALL = "NQ_FIND_ALL_CARD_MODELS";
	public static final String NQ_FIND_BY_REQUIRED_LEVEL = "NQ_FIND_CARD_MODELS_BY_REQUIRED_LEVEL";

	public CardModel(int id, String drawableResourceName, String name, int attack, int lifePoints, int goldCost, int faithCost) {
		this.id = id;
		this.drawableResourceName = drawableResourceName;
		this.name = name;
		this.attack = attack;
		this.lifePoints = lifePoints;
		this.goldCost = goldCost;
		this.faithCost = faithCost;
	}

	public static CardModel DEFAULT_CARD = new CardModel(0, "", "DEFAULT", 0, 0, 0, 0);

	public CardModel() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private int id;

	@Column(name = "DRAWABLE_RESOURCE_NAME")
	private String drawableResourceName;

	@Column(name = "CARD_NAME")
	private String name;

	@Column(name = "ATTACK")
	private int attack;

	@Column(name = "LIFE_POINTS")
	private int lifePoints;

	@Column(name = "GOLD_COST")
	private int goldCost;

	@Column(name = "FAITH_COST")
	private int faithCost;

	@Column(name = "DEFENSOR")
	private boolean defensor;

	@Column(name = "ARCHER")
	private boolean archer;

	@Column(name = "REQUIRED_LEVEL")
	private int requiredLevel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDrawableResourceName() {
		return drawableResourceName;
	}

	public void setDrawableResourceName(String drawableResourceName) {
		this.drawableResourceName = drawableResourceName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getLifePoints() {
		return lifePoints;
	}

	public void setLifePoints(int lifePoints) {
		this.lifePoints = lifePoints;
	}

	public int getGoldCost() {
		return goldCost;
	}

	public void setGoldCost(int goldCost) {
		this.goldCost = goldCost;
	}

	public int getFaithCost() {
		return faithCost;
	}

	public void setFaithCost(int faithCost) {
		this.faithCost = faithCost;
	}

	public boolean isDefensor() {
		return defensor;
	}

	public void setDefensor(boolean defensor) {
		this.defensor = defensor;
	}

	public boolean isArcher() {
		return archer;
	}

	public void setArcher(boolean archer) {
		this.archer = archer;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}

	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}
	
	public int compareTo(CardModel o) {
		return (new Integer(id)).compareTo(new Integer(((CardModel)o).getId()));
	}

	@Override
	public boolean equals(Object obj) {
		return (new Integer(id)).equals(new Integer(((CardModel)obj).getId()));
	}

	@Override
	public String toString() {
		return String.format("[%s: Id=%s, Attack=%s, Life Points=%s]", name, id, attack, lifePoints);
	}

}
