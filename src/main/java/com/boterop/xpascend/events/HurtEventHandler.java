package com.boterop.xpascend.events;

import com.boterop.xpascend.XPAscend;
import com.boterop.xpascend.utils.Difficulty;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = XPAscend.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HurtEventHandler {
	static final float LIMIT = 0.9f;
	
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        int playerExp = player.experienceLevel;

        float reductionFactor = (float) playerExp * Difficulty.multiplier(player) / 100f * 1.5f;

        reductionFactor = Math.min(reductionFactor, LIMIT);

        float originalDamage = event.getAmount();
        float reducedDamage = originalDamage * (1 - reductionFactor);

        event.setAmount(reducedDamage);
    }
}
