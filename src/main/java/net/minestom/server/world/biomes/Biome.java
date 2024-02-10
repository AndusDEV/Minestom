package net.minestom.server.world.biomes;

import net.minestom.server.coordinate.Point;
import net.minestom.server.registry.ProtocolObject;
import net.minestom.server.registry.Registry;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Locale;

public sealed interface Biome extends ProtocolObject permits BiomeImpl {
    /**
     * Returns the entity registry.
     *
     * @return the entity registry
     */
    @Contract(pure = true)
    @Nullable Registry.BiomeEntry registry();

    @Override
    @NotNull NamespaceID namespace();
    int id();
    float depth();
    float temperature();
    float scale();
    float downfall();
    BiomeEffects effects();
    Precipitation precipitation();
    TemperatureModifier temperatureModifier();

    BiomeEffects DEFAULT_EFFECTS = BiomeEffects.builder()
            .fogColor(0xC0D8FF)
            .skyColor(0x78A7FF)
            .waterColor(0x3F76E4)
            .waterFogColor(0x50533)
            .build();

    enum Precipitation {
        NONE, RAIN, SNOW;
    }

    enum TemperatureModifier {
        NONE, FROZEN;
    }

    interface Setter {
        void setBiome(int x, int y, int z, @NotNull Biome biome);

        default void setBiome(@NotNull Point blockPosition, @NotNull Biome biome) {
            setBiome(blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ(), biome);
        }
    }

    interface Getter {
        @NotNull Biome getBiome(int x, int y, int z);

        default @NotNull Biome getBiome(@NotNull Point point) {
            return getBiome(point.blockX(), point.blockY(), point.blockZ());
        }
    }

    default @NotNull NBTCompound toNbt() {
        Check.notNull(name(), "The biome namespace cannot be null");
        Check.notNull(effects(), "The biome effects cannot be null");

        return NBT.Compound(nbt -> {
            nbt.setString("name", name());
            nbt.setInt("id", id());

            nbt.set("element", NBT.Compound(element -> {
                element.setFloat("depth", depth());
                element.setFloat("temperature", temperature());
                element.setFloat("scale", scale());
                element.setFloat("downfall", downfall());
                element.setByte("has_precipitation", (byte) (precipitation() == Precipitation.NONE ? 0 : 1));
                element.setString("precipitation", precipitation().name().toLowerCase(Locale.ROOT));
                if (temperatureModifier() != TemperatureModifier.NONE)
                    element.setString("temperature_modifier", temperatureModifier().name().toLowerCase(Locale.ROOT));
                element.set("effects", effects().toNbt());
            }));
        });
    }

    static Builder builder() {
        return new Builder();
    }

    final class Builder {
        private NamespaceID name;
        private float depth = 0.2f;
        private float temperature = 0.25f;
        private float scale = 0.2f;
        private float downfall = 0.8f;
        private BiomeEffects effects = DEFAULT_EFFECTS;
        private Precipitation precipitation = Precipitation.RAIN;
        private TemperatureModifier temperatureModifier = TemperatureModifier.NONE;

        public Builder name(NamespaceID name) {
            this.name = name;
            return this;
        }

        public Builder depth(float depth) {
            this.depth = depth;
            return this;
        }

        public Builder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder downfall(float downfall) {
            this.downfall = downfall;
            return this;
        }

        public Builder effects(BiomeEffects effects) {
            this.effects = effects;
            return this;
        }

        public Builder precipitation(Biome.Precipitation precipitation) {
            this.precipitation = precipitation;
            return this;
        }

        public Builder temperatureModifier(TemperatureModifier temperatureModifier) {
            this.temperatureModifier = temperatureModifier;
            return this;
        }

        public Biome build() {
            return new BiomeImpl(name, depth, temperature, scale, downfall, effects, precipitation, temperatureModifier);
        }
    }
}
