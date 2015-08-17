Crafting Tweaks
==============

Minecraft Mod. Allows you to rotate, balance or clear the crafting matrix by the press of a button, in any (supported) crafting window.

##Useful Links
* [Latest Builds](http://jenkins.blay09.net) on my Jenkins
* [Minecraft Forum Topic](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2482146-crafting-tweaks-rotate-balance-or-clear-the) for discussion, support and feature requests 

##API
The easiest way to add Crafting Tweaks to your development environment is to do some additions to your build.gradle file. First, register Crafting Tweaks's maven repository by adding the following lines:

```
repositories {
    maven {
        name = "eiranet"
        url ="http://repo.blay09.net"
    }
}
```

Then, add a dependency to either just the Crafting Tweaks API (api) or, if you want Crafting Tweaks to be available while testing as well, the deobfuscated version (dev):

```
dependencies {
    compile 'net.blay09.mods:craftingtweaks:major.minor.build:dev' // or just api instead of dev
}
```

*Important*: If you do use the dev version like that, make sure that you still only use code within the API packages! Rikka will get mad at you and give you a dose of Schwarz Sechs if you mess with any of Crafting Tweaks' internal classes.

Make sure you enter the correct version number for the Minecraft version you're developing for. The major version is the important part here; it is increased for every Minecraft update. See the jenkins to find out the latest version number.

Done! Run gradle to update your project and you'll be good to go.

The latest Crafting Tweaks API and an unobfuscated version of the mod can also be downloaded from my [Jenkins](http://jenkins.blay09.net), if you're not into all that maven stuff.