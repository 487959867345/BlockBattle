package io.github.catchyaintit.blockbattle.game.map;

import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.plasmid.map.template.MapTemplate;

public class BlockBattleMapGenerator {

    private final BlockBattleMapConfig config;

    public BlockBattleMapGenerator(BlockBattleMapConfig config) {
        this.config = config;
    }

    public BlockBattleMap build() {
        MapTemplate template = MapTemplate.createEmpty();
        BlockBattleMap map = new BlockBattleMap(template, this.config);

        this.buildSpawn(template);
        map.spawn = new BlockPos(0,65,0);

        return map;
    }

    private void buildSpawn(MapTemplate builder) {
        BlockPos min = new BlockPos(-5, 64, -5);
        BlockPos max = new BlockPos(5, 64, 5);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            builder.setBlockState(pos, this.config.spawnBlock);
        }
    }
}
