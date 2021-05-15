package io.github.catchyaintit.blockbattle.game.card.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class CardMob extends MobEntity {

    public CardMob(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
    }
}
