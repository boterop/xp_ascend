package com.boterop.xpascend.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final float HARD = 0.2f;
    
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
        
        if (amount == level) return;
        
		level = amount;
        player.displayClientMessage(Component.literal("level up: " + amount), false);
        
        Map<String, Attribute> attributes = new HashMap<>();
        attributes.put("AttackDamage", Attributes.ATTACK_DAMAGE);
        attributes.put("AttackKnockback", Attributes.ATTACK_KNOCKBACK);
        attributes.put("AttackSpeed", Attributes.ATTACK_SPEED);
        attributes.put("KnockbackResistance", Attributes.KNOCKBACK_RESISTANCE);
        attributes.put("Luck", Attributes.LUCK);
        attributes.put("MaxHealth", Attributes.MAX_HEALTH);
        attributes.put("MovementSpeed", Attributes.MOVEMENT_SPEED);
        
        Map<String, Float> attributeMaxValues = new HashMap<>();
        attributeMaxValues.put("AttackKnockback", 5f);
        attributeMaxValues.put("KnockbackResistance", 1f);
        attributeMaxValues.put("MovementSpeed", 0.08f);
        
        List<String> percentedAttributes = new ArrayList<>();
        percentedAttributes.add("KnockbackResistance");
        percentedAttributes.add("MovementSpeed");

        double value;
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
        	value = amount;
        	if (percentedAttributes.contains(entry.getKey())) {
        		value = amount / 100f;
        	}
        	if (attributeMaxValues.containsKey(entry.getKey())) {
				value = Math.min(value, attributeMaxValues.get(entry.getKey()));
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
