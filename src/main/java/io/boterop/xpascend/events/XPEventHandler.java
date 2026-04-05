package io.boterop.xpascend.events;

import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;

import io.boterop.xpascend.XPAscend;
import io.boterop.xpascend.utils.Difficulty;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class XPEventHandler {
    @SubscribeEvent
    public static void onXPChange(PlayerXpEvent.XpChange event) {
        updateHealth(event.getEntity());
    }

    @SubscribeEvent
    public static void onLogged(PlayerEvent.PlayerLoggedInEvent event) {
        updateHealth(event.getEntity());
    }

    private static void updateHealth(Player player) {
        int playerExp = player.experienceLevel;
        float difficulty = Difficulty.getDifficulty(player);

        double amount = Math.floor(playerExp * difficulty);

        amount = amount < 0 ? 0 : amount;

        String attrs[] = {"max_health", "attack_damage", "movement_speed", "knockback_resistance", "attack_speed", "attack_knockback", "luck"};

        AttributeMap attributes = player.getAttributes();
        for (int i = 0; i < attrs.length; i++) {
            String attr = attrs[i];
            Identifier id = Identifier.fromNamespaceAndPath(XPAscend.MODID, attr);
            
            AttributeModifier modifier = new AttributeModifier(id, fixedValue(attr, amount), AttributeModifier.Operation.ADD_VALUE);
            
            getInstance(attributes, attr).addOrReplacePermanentModifier(modifier);
        }
    }

    private static AttributeInstance getInstance(AttributeMap attributes, String attr) {
        switch (attr) {
            case "max_health":
                return attributes.getInstance(Attributes.MAX_HEALTH);
            case "attack_damage":
                return attributes.getInstance(Attributes.ATTACK_DAMAGE);
            case "movement_speed":
                return attributes.getInstance(Attributes.MOVEMENT_SPEED);
            case "knockback_resistance":
                return attributes.getInstance(Attributes.KNOCKBACK_RESISTANCE);
            case "attack_speed":
                return attributes.getInstance(Attributes.ATTACK_SPEED);
            case "attack_knockback":
                return attributes.getInstance(Attributes.ATTACK_KNOCKBACK);
            case "luck":
                return attributes.getInstance(Attributes.LUCK);
            default:
                return null;
        }
    }

    private static double fixedValue(String attr, double amount) {
        Map<String, Double> maxValues = new HashMap<>();
        maxValues.put("attack_knockback", 5d);
        maxValues.put("knockback_resistance", 1d);
        maxValues.put("movement_speed", 20d);


        switch (attr) {
            case "attack_knockback":
                return Double.min(amount * 0.2, maxValues.get(attr));
            case "knockback_resistance":
                return Double.min(amount, maxValues.get(attr)) / 100;
            case "movement_speed":
                return Double.min(amount * 0.2, maxValues.get(attr)) / 100;
            case "attack_speed":
                return amount * 0.5;
            default:
                return amount;
        }
    }
}
