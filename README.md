# Yarn Mapping Provider Description

Yarn mapping is a project that aims to create a human-readable mapping file for the Minecraft codebase. Mapping files are used to translate obfuscated class, field, and method names to their original names.

Minecraft's codebase is obfuscated, which means that the names of classes, fields, and methods are intentionally obfuscated to make it more difficult for others to understand and modify the code. This is done for legal reasons and to prevent cheating in multiplayer environments.

However, this obfuscation can make it difficult for developers to read and understand the code. Mapping files provide a way to translate the obfuscated names to their original names, making it easier to read and understand the code.

## Features

- Automatically download mapping file.
- Provide "named" mapping to [MappingResolver](https://maven.fabricmc.net/docs/fabric-loader-0.14.14/net/fabricmc/loader/api/MappingResolver.html) without having to use development environment. But runtime namespace still the same.

## License

I have no license.
