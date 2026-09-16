package dev.celestiacraft.cmi.client.autoglow;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * AutoGlow —— 把 KubeJS 那个 "自动补发光层" 脚本搬到 Java 里
 * <p>
 * 贴图按兄弟贴图命名丢进去就行:
 * assets/&lt;ns&gt;/textures/block/foo.png      底层
 * assets/&lt;ns&gt;/textures/block/foo_g.png    发光层(后缀 _g, 也认 _e)
 * <p>
 * 客户端启动时(FMLClientSetupEvent, 早于第一次资源重载)跑一遍, 会:
 * 1. 扫模型(盘上的 kubejs/assets + mods/*.jar 里的), 找出引用了 foo 的面
 * 2. 给模型补一份外扩 0.01 的发光层(引用 foo_g), 写到 kubejs/assets 里
 * 3. 给发光贴图补 mcmeta: {"ctm":{"ctm_version":1,"layer":"CUTOUT","extra":{"light":15}}}
 * 贴图在 jar 里的, mcmeta 写到 kubejs/assets 里覆盖
 * <p>
 * 发光层元素带 "__glow": true 标记, 每次先删旧的再加新的, 所以重新导出模型之后再启动一次就补回来了
 *
 * @author CelestiaCraft
 */
public class AutoGlowGenerator {
	// 发光贴图后缀
	private static final List<String> SUFFIXES = List.of("_g", "_e");
	// jar 里只认这些命名空间的发光贴图(免得去动别人的模型)
	private static final Set<String> JAR_NAMESPACES = Set.of(Cmi.MODID);
	// 发光层外扩(方块单位, 16 = 一格)
	private static final double INFLATE = 0.01;
	private static final int LIGHT = 15;
	private static final String LAYER = "CUTOUT";
	private static final Set<String> CTM_TYPES = Set.of(
			"normal",
			"ctm",
			"ctm_horizontal",
			"edges",
			"edges_full",
			"eldritch",
			"random",
			"pattern",
			"pillar",
			"ctmv",
			"plane",
			"sctm",
			"custom"
	);
	private final Path assets;
	private final Path mods;
	private final Map<String, JsonObject> builtin = new LinkedHashMap<>();
	private final Map<String, Path> textures = new LinkedHashMap<>();
	private final Map<String, String> glowOf = new LinkedHashMap<>();
	private final Set<String> allowedNs = new LinkedHashSet<>();
	private final Map<String, JsonObject> models = new LinkedHashMap<>();
	private final Map<String, Path> modelFiles = new LinkedHashMap<>();
	private final List<String> changedModels = new ArrayList<>();
	private final List<String> changedMcmetas = new ArrayList<>();
	private final List<String> warnings = new ArrayList<>();
	private final Set<String> unknownParents = new TreeSet<>();
	private final Set<String> usedGlow = new LinkedHashSet<>();
	private Map<ResourceLocation, byte[]> output;
	private int files;
	private int jarCount;

	private AutoGlowGenerator(Path gameDir) {
		assets = gameDir.resolve("kubejs/assets");
		mods = gameDir.resolve("mods");
		registerBuiltin();
	}

	/**
	 * @param output 生成结果(ResourceLocation -> 内容), 直接交给虚拟资源包
	 */
	public AutoGlowGenerator(Map<ResourceLocation, byte[]> output) {
		this(FMLPaths.GAMEDIR.get());
		this.output = output;
	}
	public void generate() {
		scanTextures();
		scanModels();
		patchModels();
		writeMcmetas();
		report();
	}

	private void scanTextures() {
		for (Path jar : listJars()) {
			try (ZipFile zip = new ZipFile(jar.toFile())) {
				jarCount++;
				Enumeration<? extends ZipEntry> entries = zip.entries();

				while (entries.hasMoreElements()) {
					String[] matched = match(entries.nextElement().getName(), "textures", ".png");

					if (matched == null) {
						continue;
					}

					textures.putIfAbsent(matched[0] + ":" + matched[1], null);
					registerGlow(matched[0], matched[1], true);
				}
			} catch (IOException e) {
				warnings.add("打不开 " + jar.getFileName() + ": " + e.getMessage());
			}
		}

		files = walk(assets, (file, relative) -> {
			String[] matched = match("assets/" + relative, "textures", ".png");

			if (matched == null) {
				return;
			}

			textures.put(matched[0] + ":" + matched[1], file);
			registerGlow(matched[0], matched[1], false);
		});

		for (String base : glowOf.keySet()) {
			allowedNs.add(base.substring(0, base.indexOf(':')));
		}
	}

	private void scanModels() {
		for (Path jar : listJars()) {
			try (ZipFile zip = new ZipFile(jar.toFile())) {
				Enumeration<? extends ZipEntry> entries = zip.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					String[] matched = match(entry.getName(), "models", ".json");

					if (matched == null || !allowedNs.contains(matched[0])) {
						continue;
					}

					JsonObject json = parse(read(zip.getInputStream(entry)));

					if (json != null) {
						addModel(matched[0], matched[1], json, null);
					}
				}
			} catch (IOException e) {
				warnings.add("打不开 " + jar.getFileName() + ": " + e.getMessage());
			}
		}

		walk(assets, (file, relative) -> {
			String[] matched = match("assets/" + relative, "models", ".json");

			if (matched == null) {
				return;
			}

			JsonObject json = parse(Files.readString(file, StandardCharsets.UTF_8));

			if (json != null) {
				addModel(matched[0], matched[1], json, file);
			}
		});
	}

	private void addModel(String ns, String path, JsonObject json, Path file) {
		String id = ns + ":" + path;

		if (modelFiles.get(id) != null && file == null) {
			// 盘上的优先于 jar 里的
			return;
		}

		models.put(id, json);
		modelFiles.put(id, file);
	}

	private void registerGlow(String namespace, String path, boolean fromJar) {
		if (fromJar && !JAR_NAMESPACES.contains(namespace)) {
			return;
		}

		for (String suffix : SUFFIXES) {
			if (path.length() > suffix.length() && path.endsWith(suffix)) {
				glowOf.putIfAbsent(namespace + ":" + path.substring(0, path.length() - suffix.length()), namespace + ":" + path);
			}
		}
	}

	private void patchModels() {
		for (Map.Entry<String, JsonObject> entry : models.entrySet()) {
			String id = entry.getKey();
			String path = id.substring(id.indexOf(':') + 1);

			if (!path.startsWith("block/")) {
				continue;
			}

			JsonObject own = entry.getValue();

			if (own.has("__autoglow") || isLayered(own)) {
				// 已经生成过 / 已经挂双层父模型了, 别再动它
				continue;
			}

			Resolved resolved = resolve(id, own);

			if (resolved == null || resolved.elements().isEmpty()) {
				continue;
			}

			List<JsonElement> base = new ArrayList<>();
			boolean hadGlow = false;

			for (JsonElement element : resolved.elements()) {
				if (isGlow(element)) {
					hadGlow = true;
				} else {
					base.add(element);
				}
			}

			String ns = id.substring(0, id.indexOf(':'));
			List<JsonElement> glowElements = new ArrayList<>();

			for (JsonElement element : base) {
				JsonObject faces = element.getAsJsonObject().getAsJsonObject("faces");

				if (faces == null) {
					continue;
				}

				JsonObject glowFaces = new JsonObject();

				for (Map.Entry<String, JsonElement> face : faces.entrySet()) {
					String textureId = resolveTexture(getString(face.getValue().getAsJsonObject(), "texture"), resolved.textures(), ns, 0);
					String glowId = textureId == null ? null : glowOf.get(textureId);

					if (glowId == null) {
						continue;
					}

					JsonObject copy = face.getValue().getAsJsonObject().deepCopy();
					copy.addProperty("texture", glowId);
					glowFaces.add(face.getKey(), copy);
					usedGlow.add(glowId);
				}

				if (glowFaces.size() > 0) {
					glowElements.add(buildGlowElement(element.getAsJsonObject(), glowFaces));
				}
			}

			if (glowElements.isEmpty() && !hadGlow) {
				continue;
			}

			// 整块就是普通立方体的话, 直接挂 nebula_libs 的双层父模型(它自带 cutout + 外扩的第二层)
			JsonObject layered = layeredModel(resolved.textures(), base, ns);

			if (layered != null) {
				layered.addProperty("__autoglow", true);
				writeModel(id, ns, path, layered);
				continue;
			}

			JsonObject patched = own.deepCopy();
			patched.addProperty("__autoglow", true);
			JsonArray elements = new JsonArray();
			base.forEach(elements::add);
			glowElements.forEach(elements::add);
			patched.add("elements", elements);

			// 不是普通立方体: 保留原模型, 补发光层, 并强制 cutout(不然透明像素会画成黑块)
			if (!patched.has("render_type")) {
				patched.addProperty("render_type", "cutout");
			}

			writeModel(id, ns, path, patched);
		}
	}

	/**
	 * 普通立方体的方块: 直接用 nebula_libs 的双层父模型
	 * 全部面同一张贴图 -> ore/simple_double_layered, 上/下/侧面 -> double_layered_top_side
	 *
	 * @param textures 解析后的 textures
	 * @param base     底层元素
	 * @param ns       命名空间
	 * @return 生成好的模型, 不适用就返回 null
	 */
	private JsonObject layeredModel(JsonObject textures, List<JsonElement> base, String ns) {
		if (base.size() != 1) {
			return null;
		}

		JsonObject element = base.get(0).getAsJsonObject();

		if (!isFullCube(element)) {
			return null;
		}

		JsonObject faces = element.getAsJsonObject("faces");
		String top = resolveTexture(getString(faces.getAsJsonObject("up"), "texture"), textures, ns, 0);
		String bottom = resolveTexture(getString(faces.getAsJsonObject("down"), "texture"), textures, ns, 0);
		String side = resolveTexture(getString(faces.getAsJsonObject("north"), "texture"), textures, ns, 0);

		if (top == null || side == null) {
			return null;
		}

		JsonObject result = new JsonObject();

		if (top.equals(side) && (bottom == null || bottom.equals(side))) {
			// 全同一张
			if (!glowOf.containsKey(side)) {
				return null;
			}

			result.addProperty("parent", "nebula_libs:block/ore/simple_double_layered");
			result.addProperty("render_type", "cutout");
			JsonObject layerTextures = new JsonObject();
			layerTextures.addProperty("background", side);
			layerTextures.addProperty("ore", glowOf.get(side));
			result.add("textures", layerTextures);
			return result;
		}

		// 上/下/侧面
		result.addProperty("parent", "nebula_libs:block/double_layered_top_side");
		result.addProperty("render_type", "cutout");
		JsonObject layerTextures = new JsonObject();
		JsonObject bottomLayer = new JsonObject();
		String bottomGlow = bottom == null ? null : glowOf.get(bottom);
		String topGlow = glowOf.get(top);
		String sideGlow = glowOf.get(side);

		if (topGlow == null && sideGlow == null && bottomGlow == null) {
			return null;
		}

		layerTextures.addProperty("background_top", top);
		layerTextures.addProperty("background_side", side);
		layerTextures.addProperty("background_bottom", bottom == null ? side : bottom);
		// 没发光图的面就让第二层画底层贴图, 免得引用到不存在的贴图
		layerTextures.addProperty("layered_top", topGlow == null ? top : topGlow);
		layerTextures.addProperty("layered_side", sideGlow == null ? side : sideGlow);
		layerTextures.addProperty("layered_bottom", bottomGlow == null ? (bottom == null ? side : bottom) : bottomGlow);
		result.add("textures", layerTextures);

		if (topGlow != null) {
			usedGlow.add(topGlow);
		}

		if (sideGlow != null) {
			usedGlow.add(sideGlow);
		}

		if (bottomGlow != null) {
			usedGlow.add(bottomGlow);
		}

		return result;
	}

	/**
	 * 模型自己没写 elements, 父模型已经是双层结构 -> 不用再生成
	 *
	 * @param json 模型
	 * @return 是否已经是双层
	 */
	private static boolean isLayered(JsonObject json) {
		if (json.has("elements")) {
			return false;
		}

		String parent = getString(json, "parent");

		return parent != null && (parent.contains("double_layered") || parent.equals("nebula_libs:block/ore/simple_double_layered"));
	}

	private static boolean isFullCube(JsonObject element) {
		if (!(element.get("from") instanceof JsonArray from) || !(element.get("to") instanceof JsonArray to)) {
			return false;
		}

		return from.size() == 3 && to.size() == 3
				&& from.get(0).getAsDouble() == 0.0 && from.get(1).getAsDouble() == 0.0 && from.get(2).getAsDouble() == 0.0
				&& to.get(0).getAsDouble() == 16.0 && to.get(1).getAsDouble() == 16.0 && to.get(2).getAsDouble() == 16.0;
	}

	private void writeModel(String id, String namespace, String path, JsonObject json) {
		output.put(ResourceLocation.fromNamespaceAndPath(namespace, "models/" + path + ".json"), write(json).getBytes(StandardCharsets.UTF_8));
		changedModels.add(id);
	}

	private Resolved resolve(String id, JsonObject json) {
		List<JsonObject> chain = new ArrayList<>();
		Set<String> visited = new LinkedHashSet<>();
		JsonObject current = json;
		String currentId = id;

		while (current != null) {
			chain.add(current);

			String parent = getString(current, "parent");

			if (parent == null || !visited.add(currentId)) {
				break;
			}

			String parentId = parent.indexOf(':') >= 0 ? parent : "minecraft:" + parent;
			JsonObject next = models.get(parentId);

			if (next == null) {
				next = builtin.get(parentId);
			}

			if (next == null) {
				unknownParents.add(parentId);
				break;
			}

			currentId = parentId;
			current = next;
		}

		JsonObject textures = new JsonObject();
		JsonArray elements = null;

		for (int i = chain.size() - 1; i >= 0; i--) {
			JsonObject own = chain.get(i).getAsJsonObject("textures");

			if (own != null) {
				for (Map.Entry<String, JsonElement> entry : own.entrySet()) {
					textures.add(entry.getKey(), entry.getValue());
				}
			}
		}

		for (JsonObject entry : chain) {
			if (elements == null && entry.get("elements") instanceof JsonArray array) {
				elements = array;
			}
		}

		return elements == null ? null : new Resolved(textures, elements);
	}

	private String resolveTexture(String ref, JsonObject textures, String ns, int depth) {
		if (ref == null || depth > 16) {
			return null;
		}

		if (!(!ref.isEmpty() && ref.charAt(0) == '#')) {
			return ref.indexOf(':') >= 0 ? ref : ns + ":" + ref;
		}

		JsonElement next = textures.get(ref.substring(1));

		return next == null ? null : resolveTexture(next.getAsString(), textures, ns, depth + 1);
	}

	private JsonObject buildGlowElement(JsonObject element, JsonObject faces) {
		JsonObject glow = new JsonObject();
		glow.addProperty("__glow", true);
		glow.addProperty("name", "__glow");

		if (element.get("from") instanceof JsonArray from) {
			glow.add("from", offset(from, -INFLATE));
		}

		if (element.get("to") instanceof JsonArray to) {
			glow.add("to", offset(to, INFLATE));
		}

		if (element.get("rotation") instanceof JsonObject rotation) {
			glow.add("rotation", rotation);
		}

		glow.addProperty("shade", false);
		glow.add("faces", faces);
		return glow;
	}

	private JsonArray offset(JsonArray values, double delta) {
		JsonArray result = new JsonArray();

		for (int i = 0; i < 3 && i < values.size(); i++) {
			result.add(round(values.get(i).getAsDouble() + delta));
		}

		return result;
	}

	private static double round(double value) {
		return Math.round(value * 10000.0) / 10000.0;
	}

	private static boolean isGlow(JsonElement element) {
		if (!element.isJsonObject()) {
			return false;
		}

		JsonObject object = element.getAsJsonObject();
		String name = getString(object, "name");

		return object.has("__glow") && object.get("__glow").getAsBoolean() || name != null && name.startsWith("__glow");
	}

	private void writeMcmetas() {
		for (String glowId : usedGlow) {
			Path png = textures.get(glowId);

			if (glowId.indexOf(':') < 0) {
				continue;
			}

			String mcmetaNs = glowId.substring(0, glowId.indexOf(':'));
			String mcmetaPath = glowId.substring(glowId.indexOf(':') + 1);
			JsonObject json = parse(png != null ? read(Path.of(png + ".mcmeta")) : null);

			if (json == null) {
				json = new JsonObject();
			}

			JsonObject ctm = json.get("ctm") instanceof JsonObject existing ? existing : new JsonObject();
			boolean changed = false;

			if (!json.has("ctm")) {
				json.add("ctm", ctm);
				changed = true;
			}

			if (!ctm.has("ctm_version") || ctm.get("ctm_version").getAsInt() != 1) {
				ctm.addProperty("ctm_version", 1);
				changed = true;
			}

			String type = getString(ctm, "type");

			if (type != null && !CTM_TYPES.contains(type.toLowerCase())) {
				warnings.add(glowId + " 的 mcmeta 里 type \"" + type + "\" 不是 CTM 内置类型, 已移除");
				ctm.remove("type");
				changed = true;
			}

			if (!LAYER.equals(getString(ctm, "layer"))) {
				ctm.addProperty("layer", LAYER);
				changed = true;
			}


			JsonObject extra = ctm.get("extra") instanceof JsonObject existing ? existing : new JsonObject();

			if (!ctm.has("extra")) {
				ctm.add("extra", extra);
				changed = true;
			}

			if (!extra.has("light")) {
				extra.addProperty("light", LIGHT);
				changed = true;
			}

			if (!changed) {
				continue;
			}

			output.put(ResourceLocation.fromNamespaceAndPath(mcmetaNs, "textures/" + mcmetaPath + ".png.mcmeta"), write(json).getBytes(StandardCharsets.UTF_8));
			changedMcmetas.add(glowId);
		}
	}

	private void report() {
		Cmi.LOGGER.info(
				"[AutoGlow] 资源目录: {}, 模型 {} 个, 贴图 {} 张, jar {} 个, 文件 {} 个",
				assets,
				models.size(),
				textures.size(),
				jarCount,
				files
		);
		Cmi.LOGGER.info(
				"[AutoGlow] 发光贴图 {} 张, 其中 {} 张被模型用到, 已生成 {} 个模型 / {} 个 mcmeta",
				glowOf.size(),
				usedGlow.size(),
				changedModels.size(),
				changedMcmetas.size()
		);

		for (String id : changedModels) {
			Cmi.LOGGER.info("[AutoGlow]     - {}", id);
		}

		for (String glowId : glowOf.values()) {
			if (!usedGlow.contains(glowId)) {
				Cmi.LOGGER.warn("[AutoGlow] 没被用到的发光贴图: {}", glowId);
			}
		}

		for (String parent : unknownParents) {
			Cmi.LOGGER.warn("[AutoGlow] 认不出的父模型: {}", parent);
		}

		for (String warning : warnings) {
			Cmi.LOGGER.warn("[AutoGlow] {}", warning);
		}
	}

	private void registerBuiltin() {
		builtin.put("minecraft:block/block", new JsonObject());
		builtin.put("minecraft:block/cube", model("block/block", cubeElements(), null));
		builtin.put("minecraft:block/cube_all", model("block/cube", null, textures(
				"particle",
				"#all",
				"down",
				"#all",
				"up",
				"#all",
				"north",
				"#all",
				"east",
				"#all",
				"south",
				"#all",
				"west",
				"#all"
		)));
		builtin.put("minecraft:block/cube_bottom_top", model("block/cube", null, textures(
				"particle",
				"#side",
				"down",
				"#bottom",
				"up",
				"#top",
				"north",
				"#side",
				"east",
				"#side",
				"south",
				"#side",
				"west",
				"#side"
		)));
		builtin.put("minecraft:block/cube_column", model("block/cube", null, textures(
				"particle",
				"#side",
				"down",
				"#end",
				"up",
				"#end",
				"north",
				"#side",
				"east",
				"#side",
				"south",
				"#side",
				"west",
				"#side"
		)));
		builtin.put("minecraft:block/cube_top", model("block/cube", null, textures(
				"particle",
				"#side",
				"down",
				"#side",
				"up",
				"#top",
				"north",
				"#side",
				"east",
				"#side",
				"south",
				"#side",
				"west",
				"#side"
		)));
		builtin.put("minecraft:block/orientable_with_bottom", model("block/cube", null, textures(
				"particle",
				"#front",
				"down",
				"#bottom",
				"up",
				"#top",
				"north",
				"#front",
				"east",
				"#side",
				"south",
				"#side",
				"west",
				"#side"
		)));
		builtin.put("minecraft:block/orientable", model("block/orientable_with_bottom", null, textures("bottom", "#top")));
		builtin.put("minecraft:block/cross", model(null, crossElements(), textures("particle", "#cross")));
		builtin.put("nebula_libs:block/ore/simple_double_layered", model(null, doubleLayeredElements(), textures("particle", "#ore")));
		builtin.put("nebula_libs:block/double_layered_orientable", model(null, orientableElements(), textures("particle", "#layered")));
	}

	private static JsonObject model(String parent, JsonArray elements, JsonObject textures) {
		JsonObject json = new JsonObject();

		if (parent != null) {
			json.addProperty("parent", parent);
		}

		if (textures != null) {
			json.add("textures", textures);
		}

		if (elements != null) {
			json.add("elements", elements);
		}

		return json;
	}

	private static JsonObject textures(String... pairs) {
		JsonObject json = new JsonObject();

		for (int i = 0; i + 1 < pairs.length; i += 2) {
			json.addProperty(pairs[i], pairs[i + 1]);
		}

		return json;
	}

	private static JsonArray cubeElements() {
		JsonObject faces = new JsonObject();
		faces.add("down", face("#down", "down"));
		faces.add("up", face("#up", "up"));
		faces.add("north", face("#north", "north"));
		faces.add("south", face("#south", "south"));
		faces.add("west", face("#west", "west"));
		faces.add("east", face("#east", "east"));

		JsonArray elements = new JsonArray();
		JsonObject element = new JsonObject();
		element.add("from", numbers(0, 0, 0));
		element.add("to", numbers(16, 16, 16));
		element.add("faces", faces);
		elements.add(element);
		return elements;
	}

	private static JsonArray crossElements() {
		JsonArray elements = new JsonArray();

		JsonObject first = new JsonObject();
		first.add("from", numbers(0.8, 0, 8));
		first.add("to", numbers(15.2, 16, 8));
		first.addProperty("shade", false);
		first.add("rotation", rotation(45, "y", 8, 8, 8));
		JsonObject firstFaces = new JsonObject();
		firstFaces.add("north", uvFace("#cross", 0, 0, 16, 16));
		firstFaces.add("south", uvFace("#cross", 0, 0, 16, 16));
		first.add("faces", firstFaces);
		elements.add(first);

		JsonObject second = new JsonObject();
		second.add("from", numbers(8, 0, 0.8));
		second.add("to", numbers(8, 16, 15.2));
		second.addProperty("shade", false);
		second.add("rotation", rotation(45, "y", 8, 8, 8));
		JsonObject secondFaces = new JsonObject();
		secondFaces.add("west", uvFace("#cross", 0, 0, 16, 16));
		secondFaces.add("east", uvFace("#cross", 0, 0, 16, 16));
		second.add("faces", secondFaces);
		elements.add(second);

		return elements;
	}

	private static JsonArray doubleLayeredElements() {
		JsonArray elements = new JsonArray();

		JsonObject background = new JsonObject();
		background.addProperty("name", "background");
		background.add("from", numbers(0, 0, 0));
		background.add("to", numbers(16, 16, 16));
		JsonObject backgroundFaces = new JsonObject();

		for (String direction : List.of("north", "east", "south", "west", "up", "down")) {
			backgroundFaces.add(direction, face("#background", direction));
		}

		background.add("faces", backgroundFaces);
		elements.add(background);

		JsonObject ore = new JsonObject();
		ore.addProperty("name", "ore");
		ore.add("from", numbers(-0.01, -0.01, -0.01));
		ore.add("to", numbers(16.01, 16.01, 16.01));
		JsonObject oreFaces = new JsonObject();

		for (String direction : List.of("north", "east", "south", "west", "up", "down")) {
			JsonObject face = face("#ore", direction);
			face.addProperty("tintindex", 0);
			oreFaces.add(direction, face);
		}

		ore.add("faces", oreFaces);
		elements.add(ore);

		return elements;
	}

	private static JsonArray orientableElements() {
		JsonArray elements = new JsonArray();

		JsonObject base = new JsonObject();
		base.add("from", numbers(0, 0, 0));
		base.add("to", numbers(16, 16, 16));
		JsonObject baseFaces = new JsonObject();

		for (String direction : List.of("north", "east", "south", "west", "up", "down")) {
			baseFaces.add(direction, uvFace("#background", 0, 0, 16, 16));
		}

		base.add("faces", baseFaces);
		elements.add(base);

		JsonObject overlay = new JsonObject();
		overlay.add("from", numbers(0, 0, -0.01));
		overlay.add("to", numbers(16, 16, 0));
		overlay.add("rotation", rotation(0, "y", 0, 0, -1));
		JsonObject overlayFaces = new JsonObject();
		overlayFaces.add("north", uvFace("#layered", 0, 0, 16, 16));
		overlayFaces.add("east", uvFace("#layered", 1.99, 0, 2, 16));
		overlayFaces.add("south", uvFace("#layered", 0, 0, 16, 16));
		overlayFaces.add("west", uvFace("#layered", 0, 0, 0.01, 16));
		overlayFaces.add("up", uvFace("#layered", 0, 0, 16, 0.01));
		overlayFaces.add("down", uvFace("#layered", 0, 1.99, 16, 2));
		overlay.add("faces", overlayFaces);
		elements.add(overlay);

		return elements;
	}

	private static JsonArray numbers(double... values) {
		JsonArray array = new JsonArray();

		for (double value : values) {
			array.add(value);
		}

		return array;
	}

	private static JsonObject face(String texture, String cullface) {
		JsonObject face = new JsonObject();
		face.addProperty("texture", texture);
		face.addProperty("cullface", cullface);
		return face;
	}

	private static JsonObject uvFace(String texture, double u1, double v1, double u2, double v2) {
		JsonObject face = new JsonObject();
		face.add("uv", numbers(u1, v1, u2, v2));
		face.addProperty("texture", texture);
		return face;
	}

	private static JsonObject rotation(int angle, String axis, double x, double y, double z) {
		JsonObject rotation = new JsonObject();
		rotation.add("origin", numbers(x, y, z));
		rotation.addProperty("axis", axis);
		rotation.addProperty("angle", angle);
		rotation.addProperty("rescale", true);
		return rotation;
	}

	private List<Path> listJars() {
		List<Path> jars = new ArrayList<>();

		if (!Files.isDirectory(mods)) {
			return jars;
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(mods, "*.jar")) {
			stream.forEach(jars::add);
		} catch (IOException e) {
			warnings.add("读不了 mods 目录: " + e.getMessage());
		}

		return jars;
	}

	private int walk(Path dir, FileVisitor visitor) {
		int found = 0;

		if (!Files.isDirectory(dir)) {
			return found;
		}

		List<Path> stack = new ArrayList<>();
		stack.add(dir);

		while (!stack.isEmpty()) {
			Path current = stack.remove(stack.size() - 1);

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(current)) {
				for (Path child : stream) {
					if (Files.isDirectory(child)) {
						stack.add(child);
					} else if (Files.isRegularFile(child)) {
						visitor.visit(child, dir.relativize(child).toString().replace('\\', '/'));
						found++;
					}
				}
			} catch (IOException e) {
				warnings.add("读不了目录 " + current + ": " + e.getMessage());
			}
		}

		return found;
	}

	@FunctionalInterface
	private interface FileVisitor {
		void visit(Path file, String relative) throws IOException;
	}

	private static String[] match(String name, String folder, String extension) {
		String prefix = "assets/";

		if (!name.startsWith(prefix) || !name.endsWith(extension)) {
			return null;
		}

		int nsEnd = name.indexOf('/', prefix.length());
		int folderStart = nsEnd + 1;

		if (nsEnd < 0 || !name.startsWith(folder + "/", folderStart)) {
			return null;
		}

		int pathStart = folderStart + folder.length() + 1;
		String path = name.substring(pathStart);

		return path.isEmpty() ? null : new String[] {name.substring(prefix.length(), nsEnd), path.substring(0, path.length() - extension.length())};
	}

	private static JsonObject parse(String text) {
		if (text == null || text.isBlank()) {
			return null;
		}

		try {
			JsonElement element = JsonParser.parseString(text);

			return element.isJsonObject() ? element.getAsJsonObject() : null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	private static String read(Path file) {
		try {
			return Files.isRegularFile(file) ? Files.readString(file, StandardCharsets.UTF_8) : null;
		} catch (IOException e) {
			return null;
		}
	}

	private static String read(InputStream stream) throws IOException {
		try (InputStream input = stream) {
			return new String(input.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private static String getString(JsonObject json, String key) {
		JsonElement element = json.get(key);

		return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
	}

	/**
	 * 和 JSON.stringify(obj, null, "\t") 一样的输出(用 tab 缩进, 保持键顺序)
	 *
	 * @param element json
	 * @return 文本(带结尾换行)
	 */
	private static String write(JsonElement element) {
		StringBuilder builder = new StringBuilder();
		append(builder, element, 0);
		return builder.append('\n').toString();
	}

	private static void append(StringBuilder builder, JsonElement element, int depth) {
		if (element == null || element.isJsonNull()) {
			builder.append("null");
			return;
		}

		if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();

			if (array.isEmpty()) {
				builder.append("[]");
				return;
			}

			builder.append("[\n");

			for (int i = 0; i < array.size(); i++) {
				indent(builder, depth + 1);
				append(builder, array.get(i), depth + 1);
				builder.append(i + 1 < array.size() ? ",\n" : "\n");
			}

			indent(builder, depth);
			builder.append(']');
			return;
		}

		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();

			if (object.size() == 0) {
				builder.append("{}");
				return;
			}

			builder.append("{\n");
			int index = 0;

			for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
				indent(builder, depth + 1);
				builder.append('"').append(entry.getKey()).append("\": ");
				append(builder, entry.getValue(), depth + 1);
				builder.append(++index < object.size() ? ",\n" : "\n");
			}

			indent(builder, depth);
			builder.append('}');
			return;
		}

		builder.append(element);
	}

	private static void indent(StringBuilder builder, int depth) {
		builder.append("\t".repeat(Math.max(0, depth)));
	}

	private record Resolved(JsonObject textures, JsonArray elements) {
	}
}