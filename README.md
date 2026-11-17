# 应季食谱

一款基于 Android 的应季食谱应用，帮助你根据时令选择健康食材。

## 功能特性

- 🍃 **节气浏览**：按二十四节气分类，了解每个节气的时令特点
- 🍲 **应季菜谱**：收录四季时令菜谱，提供食材推荐与做法指导
- 📖 **详情展示**：图文并茂展示菜谱详情，便于照着做
- 🏠 **首页推荐**：根据当前季节推荐合适的食材与食谱
- 👤 **个人中心**：管理个人设置与收藏内容

## 技术栈

| 分类 | 技术 |
|---|---|
| 开发语言 | Kotlin |
| UI 框架 | Jetpack Compose |
| 构建工具 | Gradle (Kotlin DSL) |
| 架构模式 | MVVM |

## 项目结构

```
seasonal-recipe/
├── app/
│   └── src/main/java/com/repair/
│       ├── MainActivity.kt        # 应用入口
│       ├── data/
│       │   ├── Term.kt            # 节气数据模型
│       │   └── TermData.kt        # 节气数据源
│       └── ui/
│           ├── HomeScreen.kt      # 首页
│           ├── TermsScreen.kt     # 节气列表页
│           ├── MenuScreen.kt      # 菜谱页
│           ├── DetailScreen.kt    # 详情页
│           ├── MeScreen.kt        # 个人中心
│           ├── Common.kt          # 公共组件
│           └── Theme.kt           # 主题配置
├── build.gradle.kts
├── settings.gradle.kts
└── README.md

```

## 环境要求

- Android Studio（最新版）
- JDK 17 及以上
- Android SDK（API 24 及以上）

## 快速开始

1. 克隆仓库

```bash
git clone https://github.com/hhmnb/seasonal-recipe.git

```

2. 用 Android Studio 打开项目，等待 Gradle 同步完成

3. 连接设备或启动模拟器，点击 **Run** 运行

## 截图

> 后续补充应用运行截图

## 许可证

本项目仅供学习交流使用。