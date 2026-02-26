# CIT Recrafted

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.12.2-success)
![Forge](https://img.shields.io/badge/Modloader-Forge%20|%20Cleanroom-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

## A modern Custom Item Textures (CIT) engine for Minecraft 1.12.2
Built from the ground up to support legacy OptiFine/MCPatcher resource packs, CIT Recrafted brings modern performance and expanded features to 1.12.2 item rendering.
* Natively supports resource packs loaded directly from other mods.
* Supports NBT data, durability, stack size, enchantments, and pattern matching.
* Compatible with existing Optifine and MCPatcher-based resource packs out of the box.

<details>
<summary><b>Click to see all supported CIT rules</b></summary>

* `type`: (Required) Currently only supports the `item` type.
* `items`: (Required) The target item ID(s). Unlike in Optifine, this implementation supports regex/pattern matching.
* `nbt.*`: Matches NBT data.
* `damage`: Matches damage. Works with percentages and ranges.
* `enchantments`: Matches enchantments.
* `enchantmentLevels`: Matches enchantment levels to all enchantments. Works with ranges.
* `stackSize`: Matches count of items in stack. Works with ranges.
* `texture`: Texture to use for the item. Can be a full path or a lone name.
* `model`: Model to use for the item.
* `weight`: Priority above or below other textures.

See the [Optifine documentation](https://optifine.readthedocs.io/cit.html) for more information.

</details>
