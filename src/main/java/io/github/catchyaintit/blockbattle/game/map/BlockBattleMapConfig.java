package io.github.catchyaintit.blockbattle.game.map;



import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class BlockBattleMapConfig {
    public static final Codec<BlockBattleMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("spawn_block").forGetter(map -> map.spawnBlock)
    ).apply(instance, BlockBattleMapConfig::new));

    public final BlockState spawnBlock;

    public BlockBattleMapConfig(BlockState spawnBlock) {
        this.spawnBlock = spawnBlock;
    }
}
