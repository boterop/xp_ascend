package io.boterop.xpascend.utils;

import net.minecraft.world.entity.player.Player;

public class Difficulty {
  public static float getDifficulty(Player player) {
    int levelDifficulty = player.level().getDifficulty().ordinal();
    return levelDifficulty * (-0.2f) + 0.8f;
  }
}
