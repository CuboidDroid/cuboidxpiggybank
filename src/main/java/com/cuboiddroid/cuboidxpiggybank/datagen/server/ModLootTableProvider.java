package com.cuboiddroid.cuboidxpiggybank.datagen.server;

import com.cuboiddroid.cuboidxpiggybank.setup.ModBlocks;
import com.cuboiddroid.cuboidxpiggybank.setup.Registration;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
                Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationTracker) {
        map.forEach((p1, p2) -> LootTableManager.validate(validationTracker, p1, p2));
    }

    public static class ModBlockLootTables extends BlockLootTables {
        @Override
        protected void addTables() {
            addDropMachine(ModBlocks.XPIGGYBANK.get(), new String[]{"fluid", "output", "ticker"}, false);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Registration.BLOCKS.getEntries().stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }

        private void addDropMachine(Block machine, String[] keepInventoryTags, boolean keepEnergy)
        {
            add(machine, (block) -> {
                CopyNbt.Builder nbtBuilder = CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY);
                if (keepInventoryTags != null) {
                    for (String tag: keepInventoryTags) {
                        nbtBuilder = nbtBuilder.copy(tag, "BlockEntityTag." + tag, CopyNbt.Action.REPLACE);
                    }
                }
                if (keepEnergy)
                    nbtBuilder = nbtBuilder.copy("energy", "BlockEntityTag.energy", CopyNbt.Action.REPLACE);

                return createSingleItemTable(machine)
                        .apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
                        .apply(nbtBuilder);
            });
        }
    }
}