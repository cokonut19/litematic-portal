# Litematic Portal

Client-side Fabric mod for Minecraft 26.3. Press **J** (rebindable in the controls menu)
to move all `.litematic` files from your `~/Downloads` folder into the game's
`schematics` folder, where [Litematica](https://modrinth.com/mod/litematica) finds them.
The result is shown in chat.

- Only the top level of the Downloads folder is searched.
- Files with the same name in `schematics` are replaced.
- Litematica is recommended, but not required.

## Development

Requires JDK 25.

```
./gradlew build        # build the mod to build/libs
./gradlew runClient    # start the game with Litematica and MaLiLib
```
