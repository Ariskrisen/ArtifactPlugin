# ArtifactPlugin 🎒

> Passive artifacts for Minecraft servers with Anomalous Bags support

**ArtifactPlugin** is a Minecraft plugin for Paper 1.21+ that adds unique passive artifacts which work simply by being in the player's inventory.

## Features

✨ **Passive Artifacts** — Effects activate automatically when artifacts are in your hotbar  
🎒 **Anomalous Bags** — Store artifacts inside special bags that work in your hotbar  
⚡ **Powerful Effects** — Potion effects, attack effects, defense abilities, and more  
🎨 **Custom Textures** — Support for ItemsAdder and CustomModelData  
🔧 **Fully Configurable** — All messages, effects, and settings are customizable  

## Documentation

| Language | Link |
|----------|------|
| 🇷🇺 Русский | [https://ariskrisen.github.io/Docs/docs/Artifacts/intro/](https://ariskrisen.github.io/Docs/docs/Artifacts/intro/) |
| 🇬🇧 English | [https://ariskrisen.github.io/Docs/en/docs/Artifacts/intro/](https://ariskrisen.github.io/Docs/en/docs/Artifacts/intro/) |

## Quick Start

### Requirements

- Paper/Spigot 1.21+
- Java 21

### Installation

1. Download the latest `ArtifactPlugin-X.X.X.jar`
2. Place it in your server's `plugins/` folder
3. Restart the server

### Commands

| Command | Description |
|---------|-------------|
| `/artifact give <id> [player] [amount]` | Give an artifact |
| `/artifact list` | List all artifacts |
| `/artifact info <id>` | Artifact information |
| `/artifact bag <tier> [player]` | Give an Anomalous Bag (1-4) |
| `/artifact reload` | Reload configuration |

### Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `artifact.give` | Give artifacts | OP |
| `artifact.list` | List artifacts | Everyone |
| `artifact.reload` | Reload config | OP |

## Anomalous Bags

Anomalous Bags are special bundles that can store artifacts inside them:

| Tier | Slots | Description |
|------|-------|-------------|
| I | 2 | Basic bag |
| II | 4 | Advanced bag |
| III | 6 | Rare bag |
| IV | 8 | Legendary bag |

**How it works:**
1. Get a bag: `/artifact bag 2`
2. Put artifacts inside via drag & drop
3. Put the bag in your hotbar
4. All artifacts inside work passively!

## License

This project is licensed under the MIT License.
