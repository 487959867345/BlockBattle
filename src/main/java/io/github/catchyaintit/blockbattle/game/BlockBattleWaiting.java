package io.github.catchyaintit.blockbattle.game;


import io.github.catchyaintit.blockbattle.game.map.BlockBattleMap;
import io.github.catchyaintit.blockbattle.game.map.BlockBattleMapGenerator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.plasmid.game.*;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;

public class BlockBattleWaiting {
    private final GameSpace gameSpace;
    private final BlockBattleMap map;
    private final BlockBattleConfig config;
    private final BlockBattleSpawnLogic spawnLogic;

    private BlockBattleWaiting(GameSpace gameSpace, BlockBattleMap map, BlockBattleConfig config) {
        this.gameSpace = gameSpace;
        this.map = map;
        this.config = config;
        this.spawnLogic = new BlockBattleSpawnLogic(gameSpace, map);
    }

    public static GameOpenProcedure open(GameOpenContext<BlockBattleConfig> context) {
        BlockBattleConfig config = context.getConfig();
        BlockBattleMapGenerator generator = new BlockBattleMapGenerator(config.mapConfig);
        BlockBattleMap map = generator.build();

        BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                .setGenerator(map.asGenerator(context.getServer()))
                .setDefaultGameMode(GameMode.SPECTATOR);

        return context.createOpenProcedure(worldConfig, game -> {
            BlockBattleWaiting waiting = new BlockBattleWaiting(game.getSpace(), map, context.getConfig());

            GameWaitingLobby.applyTo(game, config.playerConfig);

            game.on(RequestStartListener.EVENT, waiting::requestStart);
            game.on(PlayerAddListener.EVENT, waiting::addPlayer);
            game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
        });
    }

    private StartResult requestStart() {
        BlockBattleActive.open(this.gameSpace, this.map, this.config);
        return StartResult.OK;
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnPlayer(player);
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        player.setHealth(20.0f);
        this.spawnPlayer(player);
        return ActionResult.FAIL;
    }

    private void spawnPlayer(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
    }
}
