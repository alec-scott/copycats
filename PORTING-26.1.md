# 26.1.2 Fabric Port — Working Log

Branch `26.1-fabric`, from `multiloader` (Copycats+ 3.0.9, Create 6.0.8, Minecraft 1.21.1, Architectury with common,
NeoForge and an unfinished Fabric module). Goal: Copycats+ on **Fabric 26.1.2**, running on **Create Fly** (our fork
at `../Create-Fly`), alongside the Steam 'n' Rails, Design n' Decor, Create Aeronautics and Create Big Cannons ports.

For private server use only, with the authors' permission (Copycats+ is All Rights Reserved). Don't publish builds.

This file tracks the port's state so work can resume across sessions. Update it as you go.

## Building

```sh
export JAVA_HOME=/c/Users/akrsc/AppData/Roaming/PrismLauncher/java/java-runtime-epsilon   # Git Bash, JDK 25
./gradlew compileJava --console=plain      # or: ./gradlew build -x test
```

Create Fly comes from the local checkout's jar (`create_fly_jar` in `gradle.properties`); rebuild it there first.

## Decisions

- **One Fabric module.** `common` plus the NeoForge platform code (the up-to-date one; upstream's `fabric` module was
  still on Create 0.5 / 1.20.1 and is dropped). The NeoForge `*Impl` classes live in `.fabric` packages and were
  rewritten for Fabric; the `@ExpectPlatform` stubs call them directly.
- **Build, Registrate and datagen shims** from the D&D port (`com.copycatsplus.copycats.port.*`: relocated Registrate,
  `CreateRegistrate`, `AssetLookup`/`TagGen`/..., `ConfigBase`, `BETypeOverride`, `ItemUseOverrides`). Datagen is
  stubbed: the committed `src/generated/resources` (NeoForge's) are used, `datagen/**` is excluded, and the
  datagen-only builder calls (`.blockstate .loot .tag .recipe .model .addLayer`) were stripped.
- **Copycat model** (`foundation/copycat/model/fabric/CopycatModelFabric`): a Create Fly `WrapperBlockStateModel`.
  26.1 models emit `BlockStateModelPart`s and Create Fly passes the world and position while meshing, so the
  materials are read from the block entity (NeoForge used model data). Each model entry's material model is collected
  (CT models see the filtered/scaled world), assembled by the core, and emitted as one `SimpleModelWrapper` per source
  part. The quad assembler (`CopycatRenderContextFabric`) rebuilds 26.1 `BakedQuad` records and crops with Create
  Fly's `BakedModelHelper.cropAndMove`. Kinetic partials wrap the baked air model.
- **Tints** (`foundation/copycat/client/CopycatTints`): 26.1 block colours are `BlockTintSource` lists. Multi-state
  copycats encode the part in the quad's tint index (`part * 4 + materialIndex`) instead of NeoForge's
  sprite-wrapper + renderer mixin.
- **Per-position block hooks** (NeoForge `IBlockExtension`): sound, friction, explosion resistance, enchanting power
  and CT appearance use Create Fly's control interfaces (`SoundControlBlock`, `SlipperinessControlBlock`, ...), the
  same set Create Fly's own copycats use; pick block, fall damage and destroy speed are vanilla overrides.
- **Block entities**: `ValueInput`/`ValueOutput`; the multi-state storage is a codec with the old NBT keys
  (`material`, `consumedItem`, `enableCT`). Consumed items drop from `preRemoveSideEffects` (26.1 removed
  `onRemove`). Copycats subclassing Create Fly block entities that hardwire their type (shaft, cogwheel, glass pipe,
  sliding door) get the type through `BETypeOverride`.
- **Networking**: Fabric payloads (config sync S2C, fill-copycat C2S).
- **Recipes**: NeoForge's `copycats:feature_enabled` condition is a Fabric resource condition
  (`config/fabric/FeatureEnabledCondition`); recipes and advancements converted by `porting-tools/convert_data_26.pl`.
- **Copy cat**: data-driven cat variant (`data/copycats/cat_variant/copy_cat.json`, never spawns naturally); kittens
  use the all-black kitten texture (no copy cat kitten texture exists).
- **Dropped**:
  - compat with mods that have no 26.1 Fabric build (JEI/EMI refresh, Rubidium, Radium, Diagonal Blocks, Vertical
    Slabs Compat, Double Slabs, Additional Placements, Athena);
  - C:Connected / legacy-copycat migration (fresh 26.1 worlds only);
  - light emission from glowing materials (Fabric has no per-position light; Create Fly's copycats don't either);
  - harvest-level, entity-destroy and landing/running particle hooks (no Fabric equivalent; Create Fly matches);
  - neighbour face culling against copycats (`Block.shouldRenderFace` lost its level/position in 26.1) and fluid
    face occlusion against copycats (same for `FluidRenderer`): only overdraw / water faces visible at copycat edges;
  - the config-screen sync mixin (Create Fly has no config GUI).

## Session log

### 2026-11-01
- Flattened the modules, copied shims, mapped 112 Create imports by path and 3 moved; mechanical 26.1 renames.
- Ported platform layer (entrypoints, configs, packets, tabs, keys, tooltips), block entity serialization, copycat
  model/tints, block hooks, kinetic/sliding door/fluid pipe renderers on extract/submit.
- **`compileJava` succeeds**; `porting-tools/check_mixin_targets.pl` clean (32 mixins).
- Data: 68 recipes and 68 advancements converted; 45 item definitions; air loot tables removed.
- **Dedicated server boots clean** (Create Fly + Steam 'n' Rails + D&D + Copycats+).

## TODO

- [ ] Client test: copycat rendering (simple, multi-state, slopes, CT on/off), tints (grass/leaves), item models,
      placement helper ghosts, kinetic copycats (with and without Flywheel), sliding/folding doors, fluid pipes,
      breaking particles, copy cat.
- [ ] Multiplayer: config sync packet, fill-copycat packet.
- [ ] Revisit: light emission, Sodium compat (if the server pack uses it).
