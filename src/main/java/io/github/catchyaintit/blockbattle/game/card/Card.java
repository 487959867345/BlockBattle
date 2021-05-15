package io.github.catchyaintit.blockbattle.game.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.catchyaintit.blockbattle.game.card.mob.CardMob;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class Card {
    public static final Codec<Card> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.fieldOf("name").forGetter(config -> config.name),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("mana").forGetter(config -> config.mana),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("damage").forGetter(config -> config.damage),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("health").forGetter(config -> config.health)

        ).apply(instance, Card::new);
    });

    public String name;
    public int mana;
    public int damage, health;
    public RaceType type;
    private CardMob mob;
    private Item cardItem;

    public Card(String name, int mana, int damage, int health) {
        this.name = name;
        this.mana = mana;
        this.damage = damage;
        this.health = health;
        this.cardItem = Items.PAPER;
        cardItem.getDefaultStack().setCustomName(new LiteralText(name).formatted(Formatting.GOLD));
        cardItem.getDefaultStack().addEnchantment(Enchantments.UNBREAKING, 1);
    }


    public String getName() {
        return name;
    }

    public int getMana() {
        return mana;
    }

    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }

    public RaceType getType() {
        return type;
    }

    public void setType(RaceType newType) {
        type = newType;
    }

    public ItemStack getCardItem() {
        return cardItem.getDefaultStack();
    }
}
