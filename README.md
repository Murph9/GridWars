GridWars
========

Well the git description should do, its a GridWars clone.
Made by Jake Murphy

========
Structure of the project:

GameEngine:
- 
TheGame:
- 
GameObject:
- 
OtherObjects:
- 
Player:
- 
========

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

========
0.22: Actual Collision
- has been written

========

0.21: Starting Collision
- Actually started rewriting the collision system, now each file handles collision on its own objects
  + The PlayerBullet class deals with ALL types of objects now, rather than GameEngine doing it
  + This new collision system allows the spinners to actually bounce off each other !!
    - This actual version only contains code to start writing it, not actually use ii
- Scoring still needs to be done and all the other actual game stuff

========
0.2: Scoring/Snakes/ShySquare/Annoying
- added a rewritten collision system that functions exactly the same (still needs fixing)
- Better Scoring (with values taken from the speedrun-wiki)
- added the 2 simple enemies butterfly and circle (Note: they move exactly the same as homingSquare)
- added a early version of the ShySquare (needs a little smoothing)
- fixed snakes to be better looking

========

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
