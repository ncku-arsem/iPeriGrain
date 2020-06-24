package edu.ncku.service;

public enum ColorMap {
	Gray(-1),
	AUTUMN(0),
	BONE(1),
	JET(2),
	WINTER(3),
	RAINBOW(4),
	OCEAN(5),
	SUMMER(6),
	SPRING(7),
	COOL(8),
	HSV(9),
	PINK(10),
	HOT(11);
	ColorMap(int color) {
		this.color = color;
	}
	private int color;
	
	public int getColor() {
		return color;
	}
}
