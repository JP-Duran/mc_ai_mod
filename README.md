# Minecraft CSC 480 Mod

## Setup

#### For setting up this repository you will need:  
- Minecraft Java Edition 1.21.5
- Fabric 0.16.13
- Fabric API installed in minecraft
- IntelliJ IDEA Community Edition (this is what I used, at least)
  - [instructions for settings up IntelliJ for minecraft mod development](https://docs.fabricmc.net/develop/getting-started/setting-up-a-development-environment)

Import the project into IntelliJ, let Gradle sync, and download any dependencies that you need. You should then be able to click the 'Minecraft Client' Run button in the top right to get the mod working.  

NOTE: I had to switch the build and run tool from Gradle to the IntelliJ IDEA tools to get this to work. Otherwise it would get stuck in an infinite cycle of missing imports. To do this, go to Settings->Build Tools->Gradle and set the "Build and run using" to IntelliJ IDEA  

NOTE: Make sure you do all the quick fix class-path imports in IntelliJ if you're running into missing package errors  

[This video](https://www.youtube.com/watch?v=rQdXWM8Ud90&t=1146s) is what some of the code is based off of. It will give you a good idea of what each file is doing. However, I had to change a lot of small things for the newer versions of Minecraft/Fabric, so it wont match 1:1
