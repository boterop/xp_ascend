package com.boterop.xpascend.utils;

import net.minecraft.world.entity.player.Player;

public class Difficulty {
    private static final float EASY = 0.4f;
    private static final float MEDIUM = 0.3f;
    private static final float HARD = 0.2f;
    
	public static float multiplier(Player player) {
        int levelDifficulty = player.level().getDifficulty().ordinal();
        return switch (levelDifficulty) {
	        case 1 -> EASY;
	        case 2 -> MEDIUM;
	        case 3 -> HARD;
	        default -> 0f;
		};
	}
}
