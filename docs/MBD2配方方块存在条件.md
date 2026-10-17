# MBD2 `BlockExistCondition` 使用教程

## 一、背景与动机

MBD2 的配方条件 ([`RecipeCondition`](https://github.com/Low-Drag-MC/Multiblocked2/blob/1.20.1/src/main/java/com/lowdragmc/mbd2/api/recipe/RecipeCondition.java)) 在组合时遵循如下规则:

| 条件关系 | 逻辑 |
| :---: | :---: |
| **同一种注册类型**的多个条件 | **OR** |
| **不同注册类型**的条件 | **AND** |
| `reverse` | 在分组前对单个条件取反 |

这意味着: 如果在一个配方里放几个[方块条件](https://github.com/Low-Drag-MC/Multiblocked2/blob/1.20.1/src/main/java/com/lowdragmc/mbd2/common/recipe/BlockCondition.java), 期望「有X方块 **且** 有Y方块 **且** 没有Z方块」, 实际会被解释为「有X方块 **或** 有Y方块 **或** 没有Z方块」, 与预期不符.

`BlockCondition.isOr()` 继承了 `RecipeCondition.isOr()`,  返回 `true`, 这正是 OR 行为的来源. 

`RecipeCondition` 中 [`isOr()`](https://github.com/Low-Drag-MC/Multiblocked2/blob/1.20.1/src/main/java/com/lowdragmc/mbd2/api/recipe/RecipeCondition.java#L62) 是 **非 `final` 的 `public` 方法**, 可以被继承并重写为 `false`, 从而让同类型条件之间也使用 AND 组合.

```java
public boolean isOr() {
	return true;
}
```

`BlockExistCondition` 就是基于这一机制实现的一个极简条件: 

 - 只需要一个方块列表字段
 - 多个 `BlockExistCondition` 之间通过 `isOr() = false` 变为 AND
 - 不需要 `minCount` / `maxCount` 字段, 编辑器面板更干净

---

## 二、效果与语义

### 单个条件内部

```java
new BlockExistCondition(Block A, Block B);
```

 - 结构中存在 A **或** B 任意一个 -> **满足**
 - A 和 B 都不存在 -> **失败**

### 多个条件之间

```java
builder
	.addCondition(new BlockExistCondition(Block A))
	.addCondition(new BlockExistCondition(Block B))
```

 - A 存在 **且** B 存在 -> **满足**
 - 其余情况 -> **失败**

这正是解决「有X方块 且 有Y方块」这类需求的关键.

---

## 三、Java 实现

具体代码参考 [`BlockExistCondition.java`](../src/main/java/dev/celestiacraft/cmi/compat/mbd2/BlockExistCondition.java)、[`CmiRecipeCondition.java`](../src/main/java/dev/celestiacraft/cmi/common/register/CmiRecipeCondition.java)

### 关键点说明

| 元素 | 作用 |
| :---: | :--- |
| `MBDRegistries.RECIPE_CONDITIONS.register("cmi:block_exist", BlockExistCondition.class)` | 注册到 MBD2 条件注册表, 注册 key 为 `cmi:block_exist` (见下文"为什么不用 `@LDLRegister`") |
| `CmiRecipeCondition.register()` 在 `Cmi.onCommonSetup` 的 `enqueueWork` 中调用 | 在主线程串行执行, 与 `UIFactory.register(...)`、`Regions.register(...)` 同一时机, 确保 MBD2 注册表容器已就绪 |
| `@Configurable(...)` | 让 `blocks` 出现在编辑器参数面板中 |
| 无参构造器 | `RecipeCondition.create()` 通过 `newInstance()` 创建实例, **必须保留**；字段默认值写 `new Block[0]` 以避免反序列化时 NPE |
| `getType()` | 返回 `"cmi:block_exist"`, **必须与注册 key 一字不差**, MBD2 按 `getType()` 字符串回查 `RECIPE_CONDITIONS` |
| `test()` | 遍历结构缓存方块, 命中任意一个目标立即返回 `true` |
| `isOr() -> false` | **核心**, 将同类型条件之间的 OR 改为 AND |

### 为什么不用 `@LDLRegister(registry = ...)`

LDLib 1.0.52.a 的 `@LDLRegister` 只有 `name` / `group` / `modID` / `priority` 四个属性, **没有 `registry`**, 且 `group()` 无默认值. 原写法: 

```java
@LDLRegister(name = "block_exist", registry = "mbd2:recipe_condition")
```

会让 javac 在注解处理阶段直接报错, 进而中止后续 AP 轮次、Lombok 不生成任何成员, 引发全模块 79 个级联错误. 因此在本项目锁定的 LDLib 版本下, 改为**编程式注册**: 

- 注册入口: `com.lowdragmc.mbd2.api.registry.MBDRegistries.RECIPE_CONDITIONS`
- 类型: `MBDRegistry.String<Class<? extends RecipeCondition>>` (注册的是**条件类**, 不是实例) 
- 注册 key: `"cmi:block_exist"` (与 `getType()` 返回值保持一致) 
- 注册时机: 模组初始化事件, 本项目放在 `Cmi.onCommonSetup` 的 `enqueueWork` 内

配方 JSON 中条件写: 

```json
"type": "cmi:block_exist"
```

### 为什么不直接继承 `BlockCondition`?

`BlockCondition` 有 `minCount` / `maxCount` 字段, 即使不需要也会出现在编辑器面板中, 容易误导使用者. 独立实现后, 编辑器里只有一个方块列表, 语义单一、无冗余.


## 四、在 MBD2 编辑器中使用

1. 打开任意配方类型, 选中内置配方
2. 在上方中间 **条件** 面板点击添加
3. 条件类型下拉选择 **方块存在**
4. 参数面板只有一个字段 **方块**, 点击选择你需要的方块
5. 保存并测试

如需多个「存在」条件同时满足, **添加多个 `block_exist` 条目**即可, 它们会自动 AND 组合.

---

## 五、在配方 JSON 中使用

MBD2 会把编辑器配置导出为如下 JSON: 

```json
"recipeConditions": [
	{
		"data": {
			"blocks": [
				"cmi:xxx"
			]
		},
		"type": "block_exist"
	},
	{
		"data": {
			"blocks": [
				"cmi:yyy"
			]
		},
		"type": "block_exist"
	}
]
```

 - `blocks` 数组内部是 **OR**
 - 数组与数组之间是 **AND**
 - 不需要 `minCount` / `maxCount` / `reverse`, 它们会使用 `RecipeCondition` 的默认值

---

## 六、KubeJS 绑定与脚本用法

在`kubejs/server_scripts/`下创建`MBD2RecipeConditionTest.js`
```javascript
ServerEvents.recipes((event) => {
	let { cmi } = event.getRecipes()

	cmi.some_recipe_type()
		.duration(10)
		.outputItems("cmi:any_item")
		.addCondition(new BlockExistCondition(Block.getBlock("cmi:xxx")))
		.addCondition(new BlockExistCondition(Block.getBlock("cmi:yyy")))
		.id("cmi:test")
})
```

`BlockExistCondition` 已在 KubeJS 中裸露, 直接`new BlockExistCondition(Block.getBlock())`.

---

## 七、设计要点与注意事项

| 要点 | 说明 |
| :---: | :---|
| **同类型 AND** | `isOr()` 返回 `false` 是实现这一点的唯一开关 |
| **类型内 OR** | `blocks` 数组内部多个方块之间是 OR, 符合直觉 |
| **不做计数** | `test()` 一旦命中立即返回, 比 `BlockCondition` 的全量计数略省 |

### 与 MBD2 内置 `BlockCondition` 对比

| 特性 | `block` | `block_exist` |
| :---: | :--- | :--- |
| 参数 | `minCount`、`maxCount`、`blocks`、`reverse` | `blocks` |
| 判定 | 数量在闭区间内 | 数量 ≥ 1 |
| 同类型组合 | OR | **AND** |
| 用途 | 精确方块数量控制 | 只检测方块存在 |

---
