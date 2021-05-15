package io.github.catchyaintit.blockbattle.game.card;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.catchyaintit.blockbattle.game.card.mob.CardMob;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.Plasmid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class CardRegistry {
    static HashMap<Card, CardMob> registryHashMap = new HashMap<>();

    public static void register(RaceType type, String identifier, CardMob mob) {
        ResourceManagerHelper helper = ResourceManagerHelper.get(ResourceType.SERVER_DATA);
        helper.registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public void apply(ResourceManager manager) {
                Collection<Identifier> resources = manager.findResources("cards", path -> path.endsWith(".json"));

                    try {
                        Resource resource = manager.getResource(Identifier.tryParse(identifier));

                        try(Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                            JsonElement json = new JsonParser().parse(reader);


                            DataResult<Card> result = Card.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);

                            result.result().ifPresent(card -> {
                                card.setType(type);
                                registryHashMap.put(card, mob);
                            });
                            result.error().ifPresent(error -> Plasmid.LOGGER.error("Failed at decoding card {}: {}", Identifier.tryParse(identifier), error.toString()));
                        }

                    }catch (IOException err) {
                        Plasmid.LOGGER.error(err.getMessage());
                    }
            }

            @Override
            public Identifier getFabricId() {
                return null;
            }
        });
    }
    public static Card getRandom() {
        Random random = new Random();
        int target = random.nextInt(registryHashMap.size() - 1);
        int count = 0;
        for (Card card : registryHashMap.keySet()) {
            if (count == target) {
                return card;
            }
            count++;
        }
        return null;
    }
}
