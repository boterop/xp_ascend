package com.boterop.xpascend.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.boterop.xpascend.XPAscend;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

@Mod.EventBusSubscriber(modid = XPAscend.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XPEventHandler {
    private static final float EASY = 0.5f;
    private static final float MEDIUM = 0.25f;
    private static final float HARD = 0.125f;
    
    private static int level = 0;
    
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
        int levelDifficulty = player.level().getDifficulty().ordinal();
        float difficulty = 0f;
        switch (levelDifficulty) {
			case 1: // EASY
				difficulty = EASY;
				break;
			case 2: // NORMAL
				difficulty = MEDIUM;
				break;
			case 3: // HARD
				difficulty = HARD;
				break;
			default:
				difficulty = 0f; // PEACEFUL or unknown difficulty
				break;
		}
        
        int amount = (int) Math.floor(playerExp * difficulty);
        amount = amount < 0 ? 0 : amount;
        
        if (amount == level) return; // No change in level, skip updating attributes
        
		level = amount;
        player.displayClientMessage(Component.literal("level up: " + amount), true);
        
        Map<String, Attribute> attributes = new HashMap<>();
        attributes.put("Armor", Attributes.ARMOR);
        attributes.put("ArmorToughness", Attributes.ARMOR_TOUGHNESS);
        attributes.put("AttackDamage", Attributes.ATTACK_DAMAGE);
        attributes.put("AttackKnockback", Attributes.ATTACK_KNOCKBACK);
        attributes.put("AttackSpeed", Attributes.ATTACK_SPEED);
        attributes.put("KnockbackResistance", Attributes.KNOCKBACK_RESISTANCE);
        attributes.put("Luck", Attributes.LUCK);
        attributes.put("MaxAbsorption", Attributes.MAX_ABSORPTION);
        attributes.put("MaxHealth", Attributes.MAX_HEALTH);
        attributes.put("MovementSpeed", Attributes.MOVEMENT_SPEED);

        double value;
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
        	value = amount;
        	if (entry.getValue() == Attributes.MOVEMENT_SPEED) {
        		value = amount / 100.0; // Movement speed is a percentage
        		value = Math.min(value, 0.06); // Ensure max speed added is capped at 6%
        	}
			addAttribute(player, entry.getKey(), entry.getValue(), value);
		}
    }
    
    private static void addAttribute(Player player, String modifier, Attribute attribute, double value) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        Set<AttributeModifier> modifiers = attributeInstance.getModifiers();

        if (modifiers.size() >= 1) {
            attributeInstance.removeModifiers();
        }

        AttributeModifier amount = new AttributeModifier(modifier, value, AttributeModifier.Operation.ADDITION);
        attributeInstance.addPermanentModifier(amount);
    }
}
