# IOBlockEntity 接入说明

配方逻辑不依赖旧的 `IOBE` 实现，新的 `IOBlockEntity` 只要接入，就能被主控识别。

## 现在的写法

- IO 方块继承 `IOBlockEntity`
- 实现 `supportedControllers()`
- 按需暴露 `ITEM_HANDLER`、`FLUID_HANDLER`、`ENERGY`
- `recipe()` 里直接操作 `IItemHandler` / `IFluidHandler` / `IEnergyStorage`

不要再在配方里写死具体 IO 类，比如直接依赖 `TestCokeOvenIOBlockEntity` 的自定义方法。

## lib 里新增的辅助方法

`IOBlockEntity`

- `getItemHandler()`
- `getFluidHandler()`
- `getEnergyStorage()`
- `hasItemHandler()`
- `hasFluidHandler()`
- `hasEnergyStorage()`

`MachineControllerBlockEntity`

- `findMatchedIOBlockEntities()`
- `findFirstMatchedIOBlockEntity()`
- `countMatchedItemIOBlockEntities()`
- `countMatchedFluidIOBlockEntities()`
- `countMatchedEnergyIOBlockEntities()`
- `findFirstMatchedItemHandler()`
- `findFirstMatchedFluidHandler()`
- `findFirstMatchedEnergyStorage()`

这些方法都会先检查 `supportedControllers()`，只会返回当前主控允许接入的 IO。

## 推荐用法

```java
@Override
public void recipe(MultiblockContext<TestCokeOvenBlockEntity> context) {
    IItemHandler itemHandler = findFirstMatchedItemHandler();
    IFluidHandler fluidHandler = findFirstMatchedFluidHandler();
    if (itemHandler == null || fluidHandler == null) {
        workTimer = 0;
        return;
    }

    // 后续直接把 handler 传给配方
}
```

## 什么时候还需要改

如果旧机器的 `recipe()`、IO 数量统计、结构校验里写死了某个旧 IO 方块或旧 BE 类，还是要一起改。

常见位置有：

- `recipe()` 里直接取旧 IO 实例
- `getActualItemIOCount()` / `getActualFluidIOCount()`
- 只按某一个固定方块统计 IO 数量

## 结论

新增 IO 时，不是只继承基类就一定够。

如果代码已经改成走 capability 和主控 helper，那么新 IO 一般只需要：

- 继承 `IOBlockEntity`
- 实现 `supportedControllers()`
- 暴露对应 capability
