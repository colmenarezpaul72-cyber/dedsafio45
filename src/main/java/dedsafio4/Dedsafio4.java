package dedsafio4;

import dedsafio4.entity.NautilusOseoEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dedsafio4 implements ModInitializer {
	public static final String ID = "dedsafio4";

	/** Mods propios: sus items entran al catalogo "Dedsafio". El orden es la prioridad cuando un item esta repetido. */
	public static final List<String> NAMESPACES = List.of(
			ID, "ruleta", "ruleta_anim", "ghostitem", "ghostsafio", "sacos", "silla");

	/** Las pestañas viejas de NUESTROS mods se esconden: sus items pasan a las pestañas "Dedsafio - ...". */
	public static final Set<String> PESTANAS_OCULTAS = Set.of(
			"ruleta", "ruleta_anim", "ghostitem", "ghostsafio", "sacos", "silla");

	public static boolean esPestanaOculta(String namespace) {
		return PESTANAS_OCULTAS.contains(namespace);
	}

	public static Item ANTIDOTO, FRUTA_ESPACIO, FRUTO_QUIU, GIGANTOFRUTA, PASTILLA_VACIA,
			SANGRE_REPTISAURIO, CARNE_GLEBANOIDE, CEREBRO_GLEBA, COSTILLA, CUERO, ESPADA_GLEBA,
			NAUTILUS_OSEO_HUEVO, CABINA_NAVE, ALA_IZQUIERDA_NAVE, ALA_DERECHA_NAVE, MOTOR_NAVE, FUSELAJE_NAVE;
	public static Block PELOS_DE_GLEBA, GARRA_GLEBA;
	public static EntityType<NautilusOseoEntity> NAUTILUS_OSEO;

	private static Item reg(String id, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(ID, id), item);
	}

	private static Block regBloque(String id, Block bloque) {
		Registry.register(Registries.BLOCK, Identifier.of(ID, id), bloque);
		Registry.register(Registries.ITEM, Identifier.of(ID, id), new BlockItem(bloque, new Item.Settings()));
		return bloque;
	}

	private static Item.Settings s() {
		return new Item.Settings();
	}

	@Override
	public void onInitialize() {
		ANTIDOTO = reg("antidoto", new AntidotoItem(s().maxCount(16).food(
				new FoodComponent.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build())));
		FRUTA_ESPACIO = reg("fruta_espacio", new Item(s().food(
				new FoodComponent.Builder().nutrition(4).saturationModifier(0.4f).alwaysEdible().build())));
		FRUTO_QUIU = reg("fruto_quiu", new Item(s().food(
				new FoodComponent.Builder().nutrition(3).saturationModifier(0.3f).build())));
		GIGANTOFRUTA = reg("gigantofruta", new Item(s().food(
				new FoodComponent.Builder().nutrition(8).saturationModifier(0.8f).build())));
		CARNE_GLEBANOIDE = reg("carne_glebanoide", new Item(s().food(
				new FoodComponent.Builder().nutrition(5).saturationModifier(0.5f)
						.statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 200), 0.3f).build())));
		PASTILLA_VACIA = reg("pastilla_vacia", new Item(s()));
		SANGRE_REPTISAURIO = reg("sangre_reptisaurio", new Item(s()));
		CEREBRO_GLEBA = reg("cerebro_gleba", new Item(s()));
		COSTILLA = reg("costilla", new Item(s()));
		CUERO = reg("cuero", new Item(s()));
		ESPADA_GLEBA = reg("espada_gleba", new SwordItem(ToolMaterials.IRON, s().attributeModifiers(
				SwordItem.createAttributeModifiers(ToolMaterials.IRON, 7, -2.4f))));

		// Piezas para armar la Nave (la nave en si es la de Ghostsafio)
		CABINA_NAVE = reg("cabina_nave", new Item(s()));
		ALA_IZQUIERDA_NAVE = reg("ala_izquierda_nave", new Item(s()));
		ALA_DERECHA_NAVE = reg("ala_derecha_nave", new Item(s()));
		MOTOR_NAVE = reg("motor_nave", new Item(s()));
		FUSELAJE_NAVE = reg("fuselaje_nave", new Item(s()));

		// Pelo de Gleba y Garra de Gleba ahora son bloques
		PELOS_DE_GLEBA = regBloque("pelos_de_gleba", new Block(AbstractBlock.Settings.create()
				.mapColor(MapColor.PINK).strength(0.8f).sounds(BlockSoundGroup.WOOL)));
		GARRA_GLEBA = regBloque("garra_gleba", new Block(AbstractBlock.Settings.create()
				.mapColor(MapColor.OFF_WHITE).strength(1.5f, 3.0f).sounds(BlockSoundGroup.BONE)));

		// Nautilus Óseo Pequeño
		NAUTILUS_OSEO = Registry.register(Registries.ENTITY_TYPE, Identifier.of(ID, "nautilus_oseo_pequeno"),
				EntityType.Builder.create(NautilusOseoEntity::new, SpawnGroup.MONSTER)
						.dimensions(0.9f, 0.8f).maxTrackingRange(10).build("nautilus_oseo_pequeno"));
		FabricDefaultAttributeRegistry.register(NAUTILUS_OSEO, NautilusOseoEntity.crearAtributos());
		SpawnRestriction.register(NAUTILUS_OSEO, SpawnLocationTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING,
				(tipo, mundo, motivo, pos, azar) -> true);
		NAUTILUS_OSEO_HUEVO = reg("nautilus_oseo_pequeno_spawn_egg",
				new SpawnEggItem(NAUTILUS_OSEO, 0xDCCDB0, 0x7A2E3A, s()));

		cargarCategorias();
		registrarPestanas();
		mejorarGleba();
		registrarAparicionGradual();
	}

	// ------------------------------------------------------------------ Categorias (pestañas)

	private static class Categoria {
		String id, nombre, icono;
		Set<String> items = new HashSet<>();
		Set<String> namespaces = new HashSet<>();
		boolean general;
	}

	private static List<Categoria> categorias = new ArrayList<>();
	private static Set<String> ocultos = new HashSet<>();
	private static Map<String, List<Item>> reparto = null;

	private static void cargarCategorias() {
		try (InputStream in = Dedsafio4.class.getResourceAsStream("/dedsafio4_categorias.json")) {
			JsonObject raiz = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
			for (JsonElement e : raiz.getAsJsonArray("categorias")) {
				JsonObject o = e.getAsJsonObject();
				Categoria c = new Categoria();
				c.id = o.get("id").getAsString();
				c.nombre = o.get("nombre").getAsString();
				c.icono = o.get("icono").getAsString();
				if (o.has("items")) for (JsonElement i : o.getAsJsonArray("items")) c.items.add(i.getAsString());
				if (o.has("namespaces")) for (JsonElement i : o.getAsJsonArray("namespaces")) c.namespaces.add(i.getAsString());
				categorias.add(c);
			}
			JsonObject g = raiz.getAsJsonObject("general");
			Categoria general = new Categoria();
			general.id = g.get("id").getAsString();
			general.nombre = g.get("nombre").getAsString();
			general.icono = g.get("icono").getAsString();
			general.general = true;
			categorias.add(general);
			if (raiz.has("ocultos")) {
				JsonArray arr = raiz.getAsJsonArray("ocultos");
				for (JsonElement i : arr) ocultos.add(i.getAsString());
			}
		} catch (Exception ex) {
			throw new RuntimeException("No se pudo leer dedsafio4_categorias.json", ex);
		}
	}

	/** Reparte todos los items de nuestros mods en las categorias (una sola vez). Los repetidos entre mods salen una vez. */
	private static synchronized Map<String, List<Item>> repartir() {
		if (reparto != null) return reparto;
		Map<String, List<Item>> resultado = new LinkedHashMap<>();
		for (Categoria c : categorias) resultado.put(c.id, new ArrayList<>());
		Set<String> vistos = new HashSet<>();
		for (String ns : NAMESPACES) {
			List<Identifier> ids = new ArrayList<>();
			for (Identifier id : Registries.ITEM.getIds()) {
				if (id.getNamespace().equals(ns)) ids.add(id);
			}
			ids.sort(null);
			for (Identifier id : ids) {
				String ruta = id.getPath();
				if (ocultos.contains(ruta)) continue;
				if (!vistos.add(ruta)) continue; // repetido en otro mod: se queda el de mayor prioridad
				Item item = Registries.ITEM.get(id);
				if (item == Items.AIR) continue;
				Categoria destino = null;
				for (Categoria c : categorias) {
					if (c.general) continue;
					if (c.items.contains(ruta) || c.namespaces.contains(ns)) { destino = c; break; }
				}
				if (destino == null) destino = categorias.get(categorias.size() - 1);
				resultado.get(destino.id).add(item);
			}
		}
		reparto = resultado;
		return reparto;
	}

	private static void registrarPestanas() {
		for (Categoria c : categorias) {
			RegistryKey<ItemGroup> clave = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(ID, c.id));
			Registry.register(Registries.ITEM_GROUP, clave, FabricItemGroup.builder()
					.icon(() -> {
						Identifier idIcono = Identifier.tryParse(c.icono);
						Item icono = idIcono == null ? Items.AIR : Registries.ITEM.get(idIcono);
						return new ItemStack(icono == Items.AIR ? GIGANTOFRUTA : icono);
					})
					.displayName(Text.translatable("itemGroup.dedsafio4." + c.id))
					.entries((contexto, entradas) -> {
						List<Item> lista = repartir().get(c.id);
						// primero los items, los bloques al final de cada pestaña
						for (Item item : lista) if (!(item instanceof BlockItem)) entradas.add(item);
						for (Item item : lista) if (item instanceof BlockItem) entradas.add(item);
					})
					.build());
		}
	}

	// ------------------------------------------------------------------ Gleba

	/** Gleba: features que ya existian en los datos de Ghostsafio. */
	private static void mejorarGleba() {
		RegistryKey<Biome> gleba = RegistryKey.of(RegistryKeys.BIOME, Identifier.of("ghostsafio", "gleba"));
		agregar(gleba, GenerationStep.Feature.UNDERGROUND_ORES, "grasa_ore");
		agregar(gleba, GenerationStep.Feature.VEGETAL_DECORATION, "raices_colgantes");
		agregar(gleba, GenerationStep.Feature.VEGETAL_DECORATION, "cuerdas_colgantes");
	}

	/**
	 * Aparicion gradual del Nautilus Óseo Pequeño en Gleba (igual que el Nautilus y el Cerebro de Ghostsafio):
	 * cada segundo, cerca de cada jugador, intenta sumar 1 o 2 y respeta un maximo.
	 */
	private static void registrarAparicionGradual() {
		RegistryKey<World> gleba = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("ghostsafio", "gleba"));
		ServerTickEvents.END_SERVER_TICK.register(servidor -> {
			if (servidor.getTicks() % 20 != 0) return;
			ServerWorld mundo = servidor.getWorld(gleba);
			if (mundo == null || mundo.getDifficulty() == Difficulty.PEACEFUL) return;
			Random azar = mundo.random;
			for (ServerPlayerEntity jugador : mundo.getPlayers()) {
				if (jugador.isSpectator()) continue;
				Box zona = jugador.getBoundingBox().expand(72.0);
				int cerca = mundo.getEntitiesByClass(NautilusOseoEntity.class, zona, e -> true).size();
				for (int i = 0; i < 2; i++) {
					if (cerca + i < 6 && azar.nextFloat() < 0.7f) intentarGenerar(mundo, jugador, azar);
				}
			}
		});
	}

	private static void intentarGenerar(ServerWorld mundo, ServerPlayerEntity jugador, Random azar) {
		for (int intento = 0; intento < 10; intento++) {
			double angulo = azar.nextDouble() * Math.PI * 2;
			double distancia = 18 + azar.nextDouble() * 26;
			int x = (int) Math.floor(jugador.getX() + Math.cos(angulo) * distancia);
			int z = (int) Math.floor(jugador.getZ() + Math.sin(angulo) * distancia);
			if (!mundo.isChunkLoaded(x >> 4, z >> 4)) continue;
			int y = mundo.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z) + 3 + azar.nextInt(5);
			if (y >= mundo.getTopY() - 2) continue;
			if (!mundo.isSpaceEmpty(NAUTILUS_OSEO.getSpawnBox(x + 0.5, y, z + 0.5))) continue;
			NAUTILUS_OSEO.spawn(mundo, new BlockPos(x, y, z), SpawnReason.EVENT);
			return;
		}
	}

	private static void agregar(RegistryKey<Biome> bioma, GenerationStep.Feature paso, String feature) {
		BiomeModifications.addFeature(BiomeSelectors.includeByKey(bioma), paso,
				RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("ghostsafio", feature)));
	}
}
