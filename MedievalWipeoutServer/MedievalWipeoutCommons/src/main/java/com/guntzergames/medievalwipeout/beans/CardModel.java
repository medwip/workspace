package com.guntzergames.medievalwipeout.beans;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CARD_MODEL")
public class CardModel {
	
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

	@Override
	public String toString() {
		return String.format("%s: Attack = %s, Life Points = %s", name, attack, lifePoints);
	}
	
}
