package io.github.catchyaintit.blockbattle;

import io.github.catchyaintit.blockbattle.game.BlockBattleConfig;
import io.github.catchyaintit.blockbattle.game.BlockBattleWaiting;
import io.github.catchyaintit.blockbattle.game.card.CardRegistry;
import io.github.catchyaintit.blockbattle.game.card.RaceType;
import io.github.catchyaintit.blockbattle.game.card.mob.CardMob;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.game.GameType;

public class BlockBattle implements ModInitializer {

    public static final String ID = "blockbattle";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<BlockBattleConfig> TYPE = GameType.register(
            new Identifier(ID, "blockbattle"),
            BlockBattleWaiting::open,
            BlockBattleConfig.CODEC
    );

    public static CardMob mob;

    @Override
    public void onInitialize()  {
        CardRegistry.register(RaceType.UNKNOWN, "test", mob);
    }
}
