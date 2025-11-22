package com.theouterworld.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.theouterworld.OuterWorldMod;
import com.theouterworld.registry.ModDimensions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * File-backed storage for Quantum Pad links. Persisted as JSON per-world.
 */
public class QuantumPadLinkState {
	private static final String FILE_NAME = "theouterworld_quantum_pad_links.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Type MAP_TYPE = new TypeToken<Map<String, StoredEntry>>(){}.getType();
	private static final WeakHashMap<MinecraftServer, QuantumPadLinkState> CACHE = new WeakHashMap<>();

	public static class PadKey {
		public final Identifier worldId;
		public final BlockPos pos;

		public PadKey(Identifier worldId, BlockPos pos) {
			this.worldId = worldId;
			this.pos = pos.toImmutable();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof PadKey)) return false;
			PadKey padKey = (PadKey) o;
			return Objects.equals(worldId, padKey.worldId) && Objects.equals(pos, padKey.pos);
		}

		@Override
		public int hashCode() {
			return Objects.hash(worldId, pos);
		}
	}

	public static class LinkEntry {
		public final PadKey partner;
		public boolean severed;

		public LinkEntry(PadKey partner, boolean severed) {
			this.partner = partner;
			this.severed = severed;
		}
	}

	private static class StoredEntry {
		public String partnerDim;
		public int partnerX;
		public int partnerY;
		public int partnerZ;
		public boolean severed;
	}

	private final Map<PadKey, LinkEntry> links = new HashMap<>();
	private Path filePath;

	public static QuantumPadLinkState get(MinecraftServer server) {
		return CACHE.computeIfAbsent(server, s -> {
			QuantumPadLinkState st = new QuantumPadLinkState();
			st.filePath = resolvePath(s);
			st.load();
			return st;
		});
	}

	private static Path resolvePath(MinecraftServer server) {
		long seed = 0L;
		try {
			ServerWorld ow = server.getOverworld();
			if (ow != null) seed = ow.getSeed();
		} catch (Exception ignored) {}
		Path configDir = FabricLoader.getInstance().getConfigDir();
		return configDir.resolve("theouterworld").resolve("world-" + Long.toUnsignedString(seed)).resolve(FILE_NAME);
	}

	private void load() {
		if (filePath == null) return;
		if (!Files.exists(filePath)) return;
		try {
			String json = Files.readString(filePath, StandardCharsets.UTF_8);
			Map<String, StoredEntry> raw = GSON.fromJson(json, MAP_TYPE);
			if (raw != null) {
				links.clear();
				for (Map.Entry<String, StoredEntry> e : raw.entrySet()) {
					PadKey key = decodeKey(e.getKey());
					StoredEntry se = e.getValue();
					if (key != null && se != null) {
						PadKey partner = new PadKey(Identifier.of(se.partnerDim), new BlockPos(se.partnerX, se.partnerY, se.partnerZ));
						links.put(key, new LinkEntry(partner, se.severed));
					}
				}
			}
		} catch (Exception ex) {
			OuterWorldMod.LOGGER.error("Failed to load QuantumPad links", ex);
		}
	}

	private void save(MinecraftServer server) {
		if (filePath == null) filePath = resolvePath(server);
		try {
			Files.createDirectories(filePath.getParent());
			Map<String, StoredEntry> raw = new HashMap<>();
			for (Map.Entry<PadKey, LinkEntry> e : links.entrySet()) {
				String key = encodeKey(e.getKey());
				StoredEntry se = new StoredEntry();
				se.partnerDim = e.getValue().partner.worldId.toString();
				se.partnerX = e.getValue().partner.pos.getX();
				se.partnerY = e.getValue().partner.pos.getY();
				se.partnerZ = e.getValue().partner.pos.getZ();
				se.severed = e.getValue().severed;
				raw.put(key, se);
			}
			String json = GSON.toJson(raw, MAP_TYPE);
			Files.writeString(filePath, json, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			OuterWorldMod.LOGGER.error("Failed to save QuantumPad links", ex);
		}
	}

	private static String encodeKey(PadKey key) {
		BlockPos p = key.pos;
		return key.worldId + "|" + p.getX() + "|" + p.getY() + "|" + p.getZ();
	}

	private static PadKey decodeKey(String s) {
		try {
			String[] parts = s.split("\\|");
			if (parts.length != 4) return null;
			Identifier dim = Identifier.of(parts[0]);
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			int z = Integer.parseInt(parts[3]);
			return new PadKey(dim, new BlockPos(x, y, z));
		} catch (Exception e) {
			return null;
		}
	}

	private static PadKey keyOf(RegistryKey<World> worldKey, BlockPos pos) {
		return new PadKey(worldKey.getValue(), pos);
	}

	public void registerPad(RegistryKey<World> worldKey, BlockPos pos, MinecraftServer server) {
		PadKey k = keyOf(worldKey, pos);
		if (!links.containsKey(k)) {
			links.put(k, new LinkEntry(new PadKey(Identifier.of("minecraft", "empty"), BlockPos.ORIGIN), false));
			save(server);
			OuterWorldMod.LOGGER.info("[QuantumPad] Pad registered at {} in {}", formatPos(pos), formatWorldName(worldKey));
			broadcast(server, "[QuantumPad] Pad registered at " + formatPos(pos) + " in " + formatWorldName(worldKey));
		}
	}

	public void unregisterPad(RegistryKey<World> worldKey, BlockPos pos, MinecraftServer server) {
		PadKey k = keyOf(worldKey, pos);
		links.remove(k);
		save(server);
		OuterWorldMod.LOGGER.info("[QuantumPad] Pad unregistered at {} in {}", formatPos(pos), formatWorldName(worldKey));
		broadcast(server, "[QuantumPad] Pad unregistered at " + formatPos(pos) + " in " + formatWorldName(worldKey));
	}

	public void linkPads(RegistryKey<World> aWorld, BlockPos aPos, RegistryKey<World> bWorld, BlockPos bPos, MinecraftServer server) {
		PadKey a = keyOf(aWorld, aPos);
		PadKey b = keyOf(bWorld, bPos);
		links.put(a, new LinkEntry(b, false));
		links.put(b, new LinkEntry(a, false));
		save(server);
		String msg = "[QuantumPad] Link formed between " + formatWorldName(aWorld) + " @ " + formatPos(aPos)
			+ " and " + formatWorldName(bWorld) + " @ " + formatPos(bPos);
		OuterWorldMod.LOGGER.info(msg);
		broadcast(server, msg);
	}

	public boolean isSevered(RegistryKey<World> worldKey, BlockPos pos) {
		LinkEntry e = links.get(keyOf(worldKey, pos));
		return e != null && e.severed;
	}

	public void severForPartnerOf(RegistryKey<World> worldKey, BlockPos pos, MinecraftServer server) {
		PadKey self = keyOf(worldKey, pos);
		LinkEntry e = links.get(self);
		if (e != null) {
			LinkEntry partnerEntry = links.get(e.partner);
			if (partnerEntry != null) {
				partnerEntry.severed = true;
				save(server);
				String msg = "[QuantumPad] Link severed! Partner at " + formatWorldNameId(e.partner.worldId) + " @ " + formatPos(e.partner.pos);
				OuterWorldMod.LOGGER.info(msg);
				broadcast(server, msg);
			}
		}
	}

	public LinkEntry getLink(RegistryKey<World> worldKey, BlockPos pos) {
		return links.get(keyOf(worldKey, pos));
	}

	private static void broadcast(MinecraftServer server, String message) {
		server.getPlayerManager().broadcast(Text.literal(message), false);
	}

	public static String formatPos(BlockPos pos) {
		return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
	}

	public static String formatWorldName(RegistryKey<World> key) {
		if (key == World.OVERWORLD) return "Overworld";
		if (key.equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) return "Outerworld";
		return key.getValue().toString();
	}

	private static String formatWorldNameId(Identifier id) {
		if (id.equals(World.OVERWORLD.getValue())) return "Overworld";
		if (id.equals(ModDimensions.OUTER_WORLD_WORLD_KEY.getValue())) return "Outerworld";
		return id.toString();
	}
}

