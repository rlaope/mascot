# Mascot

A lightweight IntelliJ IDEA plugin that displays a small mascot image in the bottom-right corner of the IDE window.
Users can fully customize the mascot image via the Settings UI without rebuilding the plugin.

## Features

* Displays a floating mascot image on top of the IDE.
* Position is fixed at the bottom-right corner.
* Users can select any local image file as the mascot.
* Settings are persisted across IDE restarts.
* Includes fallback default mascot if user customization is not provided.

---

![](/assets/roxy.png)

# How to Build

Run the plugin build command:

```
./gradlew buildPlugin
```

The distributable ZIP file will be created in:

```
build/distributions/
```

---

# How to Install (Local)

Inside IntelliJ IDEA:

Settings → Plugins → Gear Icon → Install Plugin from Disk → Choose the ZIP file.

---

# How Users Customize the Mascot

After installation:

Settings → Tools → Corner Mascot

You can:

* Set a custom image path
* Browse and select an image
* Apply immediately

---

# Marketplace Upload Guide

This section explains exactly how to publish this plugin to **JetBrains Marketplace**.

## 1. Prepare Your JetBrains Account

* Go to: [https://plugins.jetbrains.com](https://plugins.jetbrains.com)
* Log in with your JetBrains account.
* Open “My Plugins” page.

## 2. Create a New Plugin Entry

When uploading the first time, you must create the listing:

1. Click **New Plugin**
2. Enter:

    * Plugin Name
    * Plugin ID (must be globally unique)
    * Description
    * Vendor Name, Email
3. Upload the built ZIP from `build/distributions/`

Marketplace automatically validates and publishes it.

---

## 3. Publishing with Gradle (Optional)

To enable CLI publishing, configure your `build.gradle.kts`:

```
publishPlugin {
token.set(System.getenv("JB_PLUGIN_TOKEN"))
}
```

Generate your token here:

[https://plugins.jetbrains.com/author/me/tokens](https://plugins.jetbrains.com/author/me/tokens)

Then publish via:

```
export JB_PLUGIN_TOKEN=your_token_here
./gradlew publishPlugin
```

---

## 4. Updating Plugin Versions

Each update requires:

1. Increase version number in plugin.xml
2. Rebuild plugin
3. Upload new ZIP via the Marketplace UI
   or
   Use the CLI:

```
./gradlew publishPlugin
```

---

# Project Structure

```
corner-image-plugin/
build.gradle.kts
settings.gradle.kts
src/
main/
kotlin/
com/example/cornerimage/CornerImageStartupActivity.kt
com/example/cornerimage/MascotSettingsConfigurable.kt
com/example/cornerimage/MascotSettingsState.kt
resources/
META-INF/
plugin.xml
icons/
mascot.png
```

---

# Notes

* For production release, include pluginIcon.svg and pluginIcon_dark.svg.
* Ensure `since-build` and `until-build` in plugin.xml match your target IDE versions.


