# Component Viewer
[![Actively Developed](https://img.shields.io/badge/status-actively_developed-brightgreen?style=for-the-badge)](https://github.com/fixyldev/fixyldev/blob/main/STATUS.md#actively-developed)
[![Modrinth](https://img.shields.io/modrinth/dt/P9vIqP8R?style=for-the-badge&logo=modrinth&labelColor=gray&color=00af5c&label)](https://modrinth.com/mod/component-viewer)
[![Latest](https://img.shields.io/modrinth/game-versions/P9vIqP8R?style=for-the-badge&label=latest)](https://modrinth.com/mod/component-viewer/versions)

## 📥 Download
[<img src="https://github.com/fixyldev/fixyldev/blob/main/download/modrinth.svg" height="80">](https://modrinth.com/mod/component-viewer)

## 🗒️ Description
Component Viewer is a mod for Minecraft Java Edition that allows you to view and copy components of any item stack. This mod is configurable out of the box by pressing `J`. If tooltips become extensively large, you can use a mod like [Tooltip Scroll](https://modrinth.com/mod/tooltip-scroll) or just copy the data to a separate text file. [Fabric API](https://modrinth.com/mod/fabric-api) is required when using Fabric or Quilt.

## 🚀 Building
To build this mod, you will need **JDK 21** installed and properly configured on your system.

1. **Clone the repository**
    ```sh
    git clone https://github.com/fixyldev/component-viewer.git
    cd component-viewer
    ```
2. **Build the mod for Fabric and NeoForge**
    ```sh
    ./gradlew build
    ```
    - Fabric builds are located at `build/fabric/libs`
    - NeoForge builds are located at `build/neoforge/libs`

To run Minecraft with the desired mod loader, use:
```sh
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

To build independently, use:
```sh
./gradlew :fabric:build
./gradlew :neoforge:build
```

## 🔍 Useful Resources
- 🐛 [**Issues**](https://github.com/fixyldev/component-viewer/issues): For reporting bugs or suggesting features
- 📝 [**Changelog**](https://modrinth.com/mod/component-viewer/changelog): Keep track of all notable changes made to the mod
