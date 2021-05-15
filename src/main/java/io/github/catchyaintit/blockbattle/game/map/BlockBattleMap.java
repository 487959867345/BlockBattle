package io.github.catchyaintit.blockbattle.game.map;


import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateChunkGenerator;

public class BlockBattleMap {
    private final MapTemplate template;
    private final BlockBattleMapConfig config;
    public BlockPos spawn;

    public BlockBattleMap(MapTemplate template, BlockBattleMapConfig config) {
        this.template = template;
        this.config = config;
    }

    public ChunkGenerator asGenerator(MinecraftServer server) {
        return new TemplateChunkGenerator(server, this.template);
    }
}
