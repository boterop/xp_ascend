package com.boterop.xpascend.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.boterop.xpascend.XPAscend;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.ai.attributes.Attributes;

@Mod.EventBusSubscriber(modid = XPAscend.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XPEventHandler {
    private static final float EASY = 0.4f;
    private static final float MEDIUM = 0.3f;
    private static final float HARD = 0.2f;
    
    @SubscribeEvent
    public static void onXPChange(PlayerXpEvent.XpChange event) {
        update(event.getEntity());
    }

    @SubscribeEvent
    public static void onLogged(PlayerEvent.PlayerLoggedInEvent event) {
        update(event.getEntity());
    }
    
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        int playerExp = player.experienceLevel;
        int levelDifficulty = player.level().getDifficulty().ordinal();

        float difficulty = switch (levelDifficulty) {
            case 1 -> EASY * 15;
            case 2 -> MEDIUM * 13;
            case 3 -> HARD * 10;
            default -> 0f;
        };

        float reductionFactor = (float) playerExp * difficulty / 100f;

        reductionFactor = Math.min(reductionFactor, 0.9f);

        float originalDamage = event.getAmount();
        float reducedDamage = originalDamage * (1 - reductionFactor);

        event.setAmount(reducedDamage);

    }

    private static void update(Player player) {
        int playerExp = player.experienceLevel;
        int levelDifficulty = player.level().getDifficulty().ordinal();
        float difficulty = switch (levelDifficulty) {
	        case 1 -> EASY;
	        case 2 -> MEDIUM;
	        case 3 -> HARD;
	        default -> 0f;
		};
        
        int amount = (int) Math.floor(playerExp * difficulty);
        amount = amount < 0 ? 0 : amount;
        
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
        
        Map<String, Float> customValues = new HashMap<>();
        customValues.put("AttackKnockback", amount * 0.2f);
        customValues.put("MaxHealth", amount * 2f);
        customValues.put("MovementSpeed", amount * 0.2f);
        

        double value;
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
        	value = amount;
        	String key = entry.getKey();
        	if(customValues.containsKey(key)) {
        		value = customValues.get(key);
        	}
        	if (percentedAttributes.contains(key)) {
        		value = value / 100f;
        	}
        	if (attributeMaxValues.containsKey(key)) {
				value = Math.min(value, attributeMaxValues.get(key));
			}
			addAttribute(player, key, entry.getValue(), value);
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
