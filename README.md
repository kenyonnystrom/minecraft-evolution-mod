# Evolution Mod
This mod is meant to simulate the natural selection of sheep.
We modified the color, reproduction, life cycle of sheep and the hunting behavior of wolves to simulate the natural selection in Minecraft using Java. 
Please use IntelliJ Idea open it.

You can download IntelliJ Idea using this link: https://www.jetbrains.com/idea/download/#section=mac, or just search Intellij Download on Google.
(community version is enough for this project). Please choose the right operating system (i.e. I'm using macOS m1 chip, then I would choose macOS below the "Download IntelliJ IDEA" and under community, choose .dmg(Apple Silicon) and then click download).
![download_intelliJ_idea](images/download_IntelliJ_IDEA1.png)
Then, run the build.gradle file.
![run_build.gradle](images/run_build.gradle.png)
After that, in the upper right corner, click gradle, and in fabric dropdown menu, click runClient.
![runClient](images/runClient.png) 

About Mixin:
This is a supper useful website that introduces how mixin works.
https://fabricmc.net/wiki/tutorial:mixin_introduction

Remarks:

1,Please register your mixin at eliarbogast.evolution.mod.mixins after creating it
2, use abstract class when creating a mixin.

Q&A:


Algo:

For this project, we want to manipulate the rate of death for sheep when it's chased by a wolf by its difference between it's skin and the surrounding color.

Therefore, I wrote the SheepLifeCycleMixin class, which has the functions that 1, can detect the surrounding color for sheep 2, calculate the difference between the color of 
sheep and the surrounding color, and 3, kill the sheep based on this difference.
The difference is defined as the difference between the color of ring defined in DyeUtils in utils, as visualized below.
![color_ring](images/color_ring.png)

(more detail about it please check the code comments.)

Then, by modifying the meleeAttackGoal in wolf Class, I created the attackSheepGoal. Inside this goal, I changed the attack() function in meleeAttackGoal. (more details see the comments of the code)

The attack() will detect if the target is a sheep, then kill the sheep by chance based on the difference between the surrounding color and color of sheep. (more details see the comments of the code )
