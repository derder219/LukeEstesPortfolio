Project 2 for my class CS61B, worked solo to build a seeded-random world generator based off player input with some specific requirements. 
The spec for this project can be found here: https://sp18.datastructur.es/materials/proj/proj2/proj2
This class was meant as more of a full-dive into programming than the previous class, CS61A. This class, deservingly titled Data Structures, was all about this concept.
To help express the concept of data structures well, the course taught us the Java programming language in-depth to give us the power to implement such data structure.

Creator: Luke Estes (with some code from CS61B Spring 2018 staff to explain and implement the tile engine we had been given).

Directory structure: This was a project made on IntelliJ. 
All of the scripts I wrote are located in the proj2>byog>Core folder. 
All of the scripts provided to us for this project are located in proj2>byog>TileEngine (I did edit the Tileset links because I used custom pictures). 
The folders lab5 and lab6 are labs we did to learn the tile engine better, they aren't apart of the project.

Explanation/description of each code sample: Game.java is the file with all functions related to the game, essentially the GameManager for this project. gameTestDisplayer.java and Tester.java are files for testing out my code.
Main.java is how the game is run in IntelliJ, like other java projects, you just run it in IntelliJ or other means (like the Console). NPC.java is a script for the NPCs I ended up adding to the game later on. 
RandomUtils.java was provided to us for dealing with the tileset errors that may arrise. Room.java was a room class I made for each room I constructed in the seeded-random world generator. 
ScreenMaster.java controlled the screens, of which I have a title screen, gameplay screen, and ending screen. ThemeImplmeneter.java was a class I wrote to help implement different themes using the seeded-random generator.
UserCharacter.java was the player's script for movement and interaction in the world.

Date when written: 2/21/18 - 3/5/18 I wrote and edited these scripts as I progressed on the project.