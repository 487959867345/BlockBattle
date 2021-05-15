package io.github.catchyaintit.blockbattle.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.catchyaintit.blockbattle.game.map.BlockBattleMapConfig;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class BlockBattleConfig {
    public static final Codec<BlockBattleConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            BlockBattleMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs),
            Codec.INT.fieldOf("round_time").forGetter(config -> config.roundTime)
    ).apply(instance, BlockBattleConfig::new));

    public final PlayerConfig playerConfig;
    public final BlockBattleMapConfig mapConfig;
    public final int timeLimitSecs;
    public final int roundTime;

    public BlockBattleConfig(PlayerConfig players, BlockBattleMapConfig mapConfig, int timeLimitSecs, int roundTime) {
        this.playerConfig = players;
        this.mapConfig = mapConfig;
        this.timeLimitSecs = timeLimitSecs;
        this.roundTime = roundTime;
    }
}
