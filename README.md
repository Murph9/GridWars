GridWars
========

Well the git description should do, its a GridWars clone.
Made by Jake Murphy 0447024028

========

Structure of the project:
- GameEngine: Makes sure everything is drawn an updated
- TheGame: Is the interface of the project (can be interchanged with other TheGame's so far)
- GameObject: The base object class, every objects hierarchy is handled in this file
- OtherObjects: All enemies basically, all handle what happens when they collide
- Player: Button listener and moves by itself, is a GameObject
- GameState: handles the current game stats, like kills, lives, bombs and score

--------

0.27 Menu:
- There is now an intro screen with 3 buttons that do the same thing, and 2 check boxes for fun
- Sound has been removed from the game due to the weirdness of mono sound.

0.26: GameState/Scoring + Shaders
- Blackholes really needs a new "suck" method
- Scoring is now handled by a protected variable that all GameObjects have
  + some related methods renamed, and a killed objects counter added
  + The counter, for the first time ever actually increases the multiplier
- Added shader files "f.txt" and "v.txt" (not included in git repo) [now included]
  + they don't do anything fancy yet
- Slightly changed how the Player's bullet work


0.25: All problems now done:
- selfCol() is completed but could use some fine tuning
- Started allowing for power ups and lives decreases again
- bullets physically rotate
- GameEngine now has a subclass that handles the game state information
  + including score, lives, multiplier, bullet speed and bullet count 
- some very minor fixes..


0.24: Various:
- Blackholes now move and explode
- Snakes now get eaten and the body parts don't do weird things
- Some files were renamed
- Self collision is now handled using the method called selfCol()
  + of which the homing objects' versions have been fixed
- Todo: Fix bullet rotation and other selfCol()


0.23: BlackHoles
- they now: 
  + eat things
  + suck things
  + push bullets away (not balanced I might add) 
  + die
  + grow
- they don't:
  + explode into little pieces 
  + do anything with snakes 
  + move


0.22: Actual Collision
- has been written


0.21: Starting Collision
- Actually started rewriting the collision system, now each file handles collision on its own objects
  + The PlayerBullet class deals with ALL types of objects now, rather than GameEngine doing it
  + This new collision system allows the spinners to actually bounce off each other !!
    - This actual version only contains code to start writing it, not actually use ii
- Scoring still needs to be done and all the other actual game stuff


0.2: Scoring/Snakes/ShySquare/Annoying
- added a rewritten collision system that functions exactly the same (still needs fixing)
- Better Scoring (with values taken from the speedrun-wiki)
- added the 2 simple enemies butterfly and circle (Note: they move exactly the same as homingSquare)
- added a early version of the ShySquare (needs a little smoothing)
- fixed snakes to be better looking


0.1: Everything that is not above
- Moving player + shooting in the correct directions
- GameObjects:
  + SimpleSpinner: moves straight and bounces off walls
  + HomingDiamond: moves towards player at all times
  + SplittingSquare: moves towards the player and rotates about a point, GameEngine handles the exploding
  + ShieldedClone: movement restricted by max angle change, with a working shield
  + Snake: contains 2 files and moves with a linked-list
- Collision between objects functioning, causing some problems yet, need reworking
- Scoring very simple, no menus of anything yet (just a simple score display)
