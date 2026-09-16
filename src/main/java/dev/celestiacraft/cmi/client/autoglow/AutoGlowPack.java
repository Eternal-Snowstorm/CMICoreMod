package dev.celestiacraft.cmi.client.autoglow;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.fml.loading.FMLPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * AutoGlow 的虚拟资源包
 * <p>
 * 生成出来的模型和 mcmeta 只放在内存里, 由这个包提供给游戏(插在 pack 列表最前面, 优先级最高),
 * kubejs/assets 里不会再被写进任何文件
 *
 * @author CelestiaCraft
 */
public class AutoGlowPack implements PackResources {
	private static final String PACK_ID = "cmi_autoglow";
	// 同时把生成结果写一份到 kubejs/assets(先用这个保证能亮, 确认虚拟包没问题后改成 false)
	public static final boolean WRITE_TO_DISK = true;
	private final Map<ResourceLocation, byte[]> files;

	private AutoGlowPack(Map<ResourceLocation, byte[]> files) {
		this.files = files;
	}

	/**
	 * 现场生成一份虚拟包(每次资源重载都会重新生成)
	 *
	 * @return 虚拟包
	 */
	public static PackResources create() {
		Map<ResourceLocation, byte[]> files = new HashMap<>();

		try {
			new AutoGlowGenerator(files).generate();
		} catch (Throwable e) {
			Cmi.LOGGER.error("[AutoGlow] 生成失败", e);
		}

		if (WRITE_TO_DISK) {
			// 双保险: 同时写一份到 kubejs/assets(确认虚拟包没问题后把这个开关关掉)
			Path root = FMLPaths.GAMEDIR.get().resolve("kubejs/assets");

			for (Map.Entry<ResourceLocation, byte[]> entry : files.entrySet()) {
				try {
					Path target = root.resolve(entry.getKey().getNamespace()).resolve(entry.getKey().getPath());
					Files.createDirectories(target.getParent());
					Files.write(target, entry.getValue());
				} catch (IOException e) {
					Cmi.LOGGER.warn("[AutoGlow] 落盘失败 {}", entry.getKey(), e);
				}
			}
		}

		return new AutoGlowPack(files);
	}

	private static String path(PackType type, ResourceLocation location) {
		return type.getDirectory() + "/" + location.getNamespace() + "/" + location.getPath();
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String @NotNull ... paths) {
		return null;
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(@NotNull PackType type, @NotNull ResourceLocation location) {
		byte[] data = files.get(location);

		return data == null ? null : () -> new ByteArrayInputStream(data);
	}

	@Override
	public void listResources(@NotNull PackType type, @NotNull String namespace, String path, @NotNull ResourceOutput output) {
		String prefix = !path.isEmpty() && path.charAt(path.length() - 1) == '/' ? path : path + "/";

		for (Map.Entry<ResourceLocation, byte[]> entry : files.entrySet()) {
			ResourceLocation location = entry.getKey();

			if (location.getNamespace().equals(namespace) && location.getPath().startsWith(prefix)) {
				byte[] data = entry.getValue();
				output.accept(location, () -> new ByteArrayInputStream(data));
			}
		}
	}

	@Override
	public @NotNull Set<String> getNamespaces(@NotNull PackType type) {
		Set<String> namespaces = new HashSet<>();

		for (ResourceLocation location : files.keySet()) {
			namespaces.add(location.getNamespace());
		}

		return namespaces;
	}

	@Nullable
	@Override
	public <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> serializer) {
		if (serializer == PackMetadataSection.TYPE) {
			return (T) new PackMetadataSection(Component.literal("CMI AutoGlow"), 15);
		}

		return null;
	}

	@Override
	public @NotNull String packId() {
		return PACK_ID;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public void close() {
	}
}
