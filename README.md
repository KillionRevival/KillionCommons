# KillionCommons

Developer-facing utility plugin that handles common functionality for the Killion Detention Center Revival server.

Should include functionality for:
* Creating, updating, or performing actions with Killion items
* Utility methods used in other Killion plugins
* Handling common forms of user interaction, such as chat strings or UI/UX

## Requirements

* Targets Minecraft **26.2** and requires **Java 25** to build and run.
* Requires a **ProtocolLib dev build** at runtime. The latest ProtocolLib release (5.3.0) predates
  MC 26.x; only the dev builds support it, and they are not published to Maven, so the plugin is
  compiled against 5.3.0 but needs a dev build on the server.
* Since MC 26.1 the server jar is no longer obfuscated, so this plugin is published
  **Mojang-mapped** rather than reobfuscated to Spigot mappings.