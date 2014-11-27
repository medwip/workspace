package com.guntzergames.medievalwipeout.enums;

public enum CardModel {

	GOBLIN_PIRAT(1, "card_goblin_pirate", "Gobin Pirate", 10, 60, 20, 0),
	GNOME_ENGINEER(1, "card_gnome_engineer", "Gnome Engineer", 10, 60, 5, 0),
	TEMPLAR(2, "card_templar", "Templar", 10, 60, 5, 1),
	ASTRAL_SPIDER(3, "card_astral_spider", "Astral Spider", 40, 150, 100, 0);
	
	private CardModel(int id, String drawableResourceName, String name, int attack, int lifePoints, int goldCost, int faithCost) {
		this.id = id;
		this.drawableResourceName = drawableResourceName;
		this.name = name;
		this.attack = attack;
		this.lifePoints = lifePoints;
		this.goldCost = goldCost;
		this.faithCost = faithCost;
	}
	
	private int id;
	private String drawableResourceName;
	private String name;
	private int attack;
	private int lifePoints;
	private int goldCost;
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
