package dev.celestiacraft.cmi.compat.create;

import com.jesz.createdieselgenerators.content.bulk_fermenter.BulkFermentingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

/**
 * 改编自 Jasons-impart 的 Create-Delight-Core 项目.
 * <p>
 * 感谢 Jasons-impart 使用 MIT
 *
 * @see <a href="https://github.com/Jasons-impart/Create-Delight-Core">Create-Delight-Core</a>
 */
public interface IBulkFermenterFilteringAccess {
	FilteringBehaviour cmi$getFilter();

	BulkFermentingRecipe cmi$getCurrentRecipe();

	int cmi$getWidth();

	int cmi$getHeight();
}