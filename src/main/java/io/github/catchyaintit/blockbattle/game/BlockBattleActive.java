package io.github.catchyaintit.blockbattle.game;

import io.github.catchyaintit.blockbattle.game.battle.Battle;
import io.github.catchyaintit.blockbattle.game.battle.BattleManager;
import io.github.catchyaintit.blockbattle.game.card.CardRegistry;
import io.github.catchyaintit.blockbattle.game.card.mob.CardMob;
import io.github.catchyaintit.blockbattle.game.map.BlockBattleMap;
import io.github.catchyaintit.blockbattle.game.round.RoundManager;
import io.github.catchyaintit.blockbattle.game.round.States;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.PlayerRef;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

import java.util.Set;
import java.util.stream.Collectors;

public class BlockBattleActive {
    private final BlockBattleConfig config;

    public final GameSpace gameSpace;
    private final BlockBattleMap gameMap;

    // TODO replace with ServerPlayerEntity if players are removed upon leaving
    private final Object2ObjectMap<PlayerRef, BlockBattlePlayer> participants;
    private final BlockBattleSpawnLogic spawnLogic;
    private final BlockBattleStageManager stageManager;
    private final boolean ignoreWinState;
    private final BlockBattleTimerBar timerBar;
    private int curTick;
    private int targetTick;
    private int tickToSecond;
    private int roundTimeRef;
    private RoundManager roundManager = new RoundManager();
    private BattleManager battleManager = new BattleManager();

    private BlockBattleActive(GameSpace gameSpace, BlockBattleMap map, GlobalWidgets widgets, BlockBattleConfig config, Set<PlayerRef> participants) {
        this.gameSpace = gameSpace;
        this.config = config;
        this.gameMap = map;
        this.spawnLogic = new BlockBattleSpawnLogic(gameSpace, map);
        this.participants = new Object2ObjectOpenHashMap<>();
        this.curTick = gameSpace.getServer().getTicks();
        this.targetTick = gameSpace.getServer().getTicks() + (config.roundTime * 20);
        this.roundTimeRef = config.roundTime;

        for (PlayerRef player : participants) {
            this.participants.put(player, new BlockBattlePlayer());
        }

        this.stageManager = new BlockBattleStageManager();
        this.ignoreWinState = this.participants.size() <= 1;
        this.timerBar = new BlockBattleTimerBar(widgets);
    }

    public static void open(GameSpace gameSpace, BlockBattleMap map, BlockBattleConfig config) {
        gameSpace.openGame(game -> {
            Set<PlayerRef> participants = gameSpace.getPlayers().stream()
                    .map(PlayerRef::of)
                    .collect(Collectors.toSet());
            GlobalWidgets widgets = new GlobalWidgets(game);
            BlockBattleActive active = new BlockBattleActive(gameSpace, map, widgets, config, participants);

            game.setRule(GameRule.CRAFTING, RuleResult.DENY);
            game.setRule(GameRule.PORTALS, RuleResult.DENY);
            game.setRule(GameRule.PVP, RuleResult.DENY);
            game.setRule(GameRule.HUNGER, RuleResult.DENY);
            game.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
            game.setRule(GameRule.INTERACTION, RuleResult.DENY);
            game.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
            game.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);
            game.setRule(GameRule.UNSTABLE_TNT, RuleResult.DENY);

            game.on(GameOpenListener.EVENT, active::onOpen);
            game.on(GameCloseListener.EVENT, active::onClose);

            game.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            game.on(PlayerAddListener.EVENT, active::addPlayer);
            game.on(PlayerRemoveListener.EVENT, active::removePlayer);

            game.on(GameTickListener.EVENT, active::tick);

            game.on(PlayerDamageListener.EVENT, active::onPlayerDamage);
            game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
            game.on(UseItemListener.EVENT, active::onItemUse);
            game.on(EntityDeathListener.EVENT, active::onEntityDeath);
        });
    }




    private void onOpen() {
        ServerWorld world = this.gameSpace.getWorld();
        for (PlayerRef ref : this.participants.keySet()) {
            ref.ifOnline(world, this::spawnParticipant);
        }
        this.stageManager.onOpen(world.getTime(), this.config);
        // TODO setup logic
    }

    private void onClose() {
        // TODO teardown logic
    }

    private void addPlayer(ServerPlayerEntity player) {
        if (!this.participants.containsKey(PlayerRef.of(player))) {
            this.spawnSpectator(player);
        }
    }

    private void removePlayer(ServerPlayerEntity player) {
        this.participants.remove(PlayerRef.of(player));
    }

    private ActionResult onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        // TODO handle damage
        return ActionResult.FAIL;
    }
    private TypedActionResult<ItemStack> onItemUse(ServerPlayerEntity player, Hand hand) {
        if (player.getMainHandStack().getItem() == Items.STICK) {

        }
        return TypedActionResult.success(player.getMainHandStack());
    }
    private ActionResult onEntityDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (roundManager.getState() == States.FIGHTING && livingEntity instanceof CardMob) {
            for (Battle battle : battleManager.getBattles()) {
                if (battle.getPlayerOnePlayedCards().contains(livingEntity.getEntityId())) {
                    battle.getPlayerOnePlayedCards().remove(livingEntity.getEntityId());
                }else if (battle.getPlayerTwoPlayedCards().contains(livingEntity.getEntityId())) {
                    battle.getPlayerTwoPlayedCards().remove(livingEntity.getEntityId());
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        // TODO handle death
        this.spawnParticipant(player);
        return ActionResult.FAIL;
    }

    private void spawnParticipant(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
    }

    private void spawnSpectator(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
        this.spawnLogic.spawnPlayer(player);
    }

    private void alertPlayersOfRound() {
        for (PlayerRef ref : participants.keySet()) {
            ServerPlayerEntity player = ref.getEntity(gameSpace.getServer());
            player.networkHandler.sendPacket(new TitleS2CPacket(2 * 20, 5 * 20, 2 * 20));
            player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText("Round " + roundManager.getRound())));
        }
    }
    private void dealCards() {
        for (PlayerRef ref: participants.keySet()) {
            ServerPlayerEntity player = ref.getEntity(gameSpace.getServer());
            player.giveItemStack(CardRegistry.getRandom().getCardItem());
        }
    }


    private void tick() {
        ServerWorld world = this.gameSpace.getWorld();
        long time = world.getTime();
        curTick = gameSpace.getServer().getTicks();

        if (!stageManager.gameStart) {
            targetTick++;
            return;
        }else {
            roundManager.setState(States.PLACING);
            dealCards();
        }


        if (roundManager.getState() == States.PLACING) {
            if (roundTimeRef == 0) {
                roundManager.setState(States.FIGHTING);
            } else {
                if (this.gameSpace.getServer().getTicks() == targetTick) {
                    --roundTimeRef;
                }
            }
        } else if (roundManager.getState() == States.FIGHTING) {
            if (battleManager.getBattleCount() == 0) {
                roundTimeRef = config.roundTime;
                roundManager.setState(States.PLACING);
                targetTick = roundTimeRef * 20;
                roundManager.setRound(roundManager.getRound() + 1);
                alertPlayersOfRound();
                dealCards();
            }
        }




        BlockBattleStageManager.IdleTickResult result = this.stageManager.tick(time, gameSpace);

        switch (result) {
            case CONTINUE_TICK:
                break;
            case TICK_FINISHED:
                return;
            case GAME_FINISHED:
                this.broadcastWin(this.checkWinResult());
                return;
            case GAME_CLOSED:
                this.gameSpace.close();
                return;
        }


        // TODO tick logic
    }

    private void broadcastWin(WinResult result) {
        ServerPlayerEntity winningPlayer = result.getWinningPlayer();

        Text message;
        if (winningPlayer != null) {
            message = winningPlayer.getDisplayName().shallowCopy().append(" has won the game!").formatted(Formatting.GOLD);
        } else {
            message = new LiteralText("The game ended, but nobody won!").formatted(Formatting.GOLD);
        }

        PlayerSet players = this.gameSpace.getPlayers();
        players.sendMessage(message);
        players.sendSound(SoundEvents.ENTITY_VILLAGER_YES);
    }

    private WinResult checkWinResult() {
        // for testing purposes: don't end the game if we only ever had one participant
        if (this.ignoreWinState) {
            return WinResult.no();
        }

        ServerWorld world = this.gameSpace.getWorld();
        ServerPlayerEntity winningPlayer = null;

        // TODO win result logic
        return WinResult.no();
    }

    static class WinResult {
        final ServerPlayerEntity winningPlayer;
        final boolean win;

        private WinResult(ServerPlayerEntity winningPlayer, boolean win) {
            this.winningPlayer = winningPlayer;
            this.win = win;
        }

        static WinResult no() {
            return new WinResult(null, false);
        }

        static WinResult win(ServerPlayerEntity player) {
            return new WinResult(player, true);
        }

        public boolean isWin() {
            return this.win;
        }

        public ServerPlayerEntity getWinningPlayer() {
            return this.winningPlayer;
        }
    }
}
