package com.boterop.xpascend.events;

import java.util.Set;

import com.boterop.xpascend.XPAscend;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.ai.attributes.Attributes;

@Mod.EventBusSubscriber(modid = XPAscend.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XPEventHandler {
    @SubscribeEvent
    public static void onXPChange(PlayerXpEvent.XpChange event) {
        update(event.getEntity());
    }

    @SubscribeEvent
    public static void onLogged(PlayerEvent.PlayerLoggedInEvent event) {
        update(event.getEntity());
    }

    private static void update(Player player) {
        int playerExp = player.experienceLevel;
        float difficulty = 0.4f;
        int amount = (int) Math.floor(playerExp * difficulty);
        amount = amount < 0 ? 0 : amount;

        addAttribute(player, Attributes.ATTACK_DAMAGE, "AttackDamage", amount);
        addAttribute(player, Attributes.MAX_HEALTH, "MaxHealth", amount);
    }
    
    private static void addAttribute(Player player, Attribute attribute, String modifier, int value) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        Set<AttributeModifier> modifiers = attributeInstance.getModifiers();

        if (modifiers.size() >= 1) {
            attributeInstance.removeModifiers();
        }

        AttributeModifier amount = new AttributeModifier(modifier, value, AttributeModifier.Operation.ADDITION);
        attributeInstance.addPermanentModifier(amount);
    }
}
