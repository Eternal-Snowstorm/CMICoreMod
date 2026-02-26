# 更新 Nebula Libs 流程

## 前置知识

CMICore 依赖 Nebula Libs，但这个库不在任何远程仓库上（坐等亓才孑），所以我们用本地 Maven 仓库（`mavenLocal`）来管理。

简单来说就是：`publishToMavenLocal` 就是把构建好的 jar 复制到你电脑的 `C:\Users\你的用户名\.m2\repository\` 下面，Gradle
会从这里找依赖。

## 更新步骤（以升级到 1.6.0 为例）

### 1. 在 Nebula-Utils 项目构建并发布

打开终端，进入 Nebula-Utils 项目目录：

```bash
cd /你的盘/你的文件夹/Nebula-Utils（lib模组）
```

先改 `gradle.properties` 里的版本号为 `1.6.0`，以及 `build.gradle` 里 publishing 块的 version。

然后执行：

```bash
./gradlew build publishToMavenLocal
```

这一步做了两件事：

- `build` → 编译代码，生成正式 jar（SRG 混淆）、-dev jar、-sources jar
- `publishToMavenLocal` → 把正式 jar 扔到本地 `.m2` 仓库

### 2. 更新 CMICore 的 libs 文件夹

libs 文件夹里要放两个文件，作用各不同：

| 文件                              | 用途                   | 来源                           |
|---------------------------------|----------------------|------------------------------|
| `Nebula Libs-1.6.0.jar`         | jarJar 打包（最终发布给玩家用的） | Nebula-Utils 的 `build/libs/` |
| `Nebula Libs-1.6.0-sources.jar` | IDEA 看源码用            | Nebula-Utils 的 `build/libs/` |

操作：

1. 删掉 libs 下旧的 `Nebula Libs-1.5.0.jar` 和 `Nebula Libs-1.5.0-sources.jar`
2. 从 Nebula-Utils 的 `build/libs/` 把上面两个新文件复制过来
3. `-dev.jar` 不需要了，可以不放

### 3. 修改 CMICore 的 build.gradle

把版本号从 `1.5.0` 改成 `1.6.0`，一共三处：

```groovy
// 第一处：主依赖
implementation fg.deobf("top.nebula:nebula_libs:1.6.0")

// 第二处：sources jar
compileOnly files("libs/Nebula Libs-1.6.0-sources.jar")

// 第三处：jarJar 范围（按需调整）
jarJar(group: "dev.celestiacraft", name: "Nebula Libs", version: "[1.6.0,)")
```

### 4. 刷新 Gradle

在 IDEA 里点 Gradle 面板的刷新按钮，或者命令行执行：

```bash
./gradlew --refresh-dependencies
```

搞定，可以正常导包和 runClient 了。

## 注意事项

每个开发者拉取 CMICore 代码后，都需要自己跑一次步骤 1（在 Nebula-Utils 执行 `publishToMavenLocal`），因为本地 `.m2`
仓库是每个人自己电脑上的，不会跟着 git 走。

## 常见问题

**Q: IDEA 报红 / 找不到类？**
A: 大概率是没跑 `publishToMavenLocal`，或者版本号对不上。

**Q: runClient 崩溃 NoSuchMethodError？**
A: 检查是不是直接用了 `implementation files(...)` 而不是 `fg.deobf()`。必须走 `fg.deobf()` 才能正确处理映射。

**Q: 能不能把 .m2 里的 jar 提交到 git？**
A: 不行，`.m2` 是 Gradle 的缓存目录，不归 git 管。老老实实每人跑一次发布。
