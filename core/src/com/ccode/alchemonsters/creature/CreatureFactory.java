package com.ccode.alchemonsters.creature;

import java.util.Random;

public class CreatureFactory {

	private static Random rand = new Random();
	
	//Stat gen values
	private static double STAT_GEN_MEAN = 16;
	private static double STAT_GEN_SD = 6;
	
	public static Creature generateRandomLevelOne(String baseID) {
		CreatureBase base = CreatureDictionary.getBase(baseID);
		CreatureNature nature = generateRandomCreatureNature();
		CreatureStats stats = generateRandomCreatureStats();
		
		return new Creature(base, nature, stats);
	}
	
	public static CreatureNature generateRandomCreatureNature() {
		StatType[] types = StatType.values();
		return new CreatureNature(types[rand.nextInt(types.length)], types[rand.nextInt(types.length)]);
	}
	
	/*
	 * Graph to adjust values for gen mean and SD:
	 * https://www.desmos.com/calculator/bluepthxcb
	 */
	public static byte generateRandomStatValue() {
		double dev = rand.nextGaussian();
		double value = STAT_GEN_MEAN + dev * STAT_GEN_SD;
		if(value > 31) value = 31;
		if(value < 0) value = 0;
		
		return (byte) Math.round(value);
	}
	
	public static CreatureStats generateRandomCreatureStats() {
		return new CreatureStats(
			generateRandomStatValue(),
			generateRandomStatValue(),
			generateRandomStatValue(),
			generateRandomStatValue(),
			generateRandomStatValue(),
			generateRandomStatValue(),
			generateRandomStatValue()
		);
	}
	
}
