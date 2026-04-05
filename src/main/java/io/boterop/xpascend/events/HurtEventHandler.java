package io.boterop.xpascend.events;

import io.boterop.xpascend.utils.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class HurtEventHandler {
  static final float LIMIT = 0.9f;
	
    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        int playerExp = player.experienceLevel;

        float reductionFactor = (float) playerExp * Difficulty.getDifficulty(player) / 15;

        reductionFactor = Math.min(reductionFactor, LIMIT);

        float originalDamage = event.getAmount();
        float reducedDamage = originalDamage / (1 + reductionFactor);

        event.setAmount(reducedDamage);
    }
}
