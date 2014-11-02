GridWars
========

Well the git description should do, its a GridWars clone.


========

0.1: (even though a lot of things have happened before this)
- Moving player + shooting in the correct directions
- GameObjects:
  + SimpleSpinner: moves straight and bounces off walls
  + HomingDiamond: moves towards player at all times
  + SplittingSquare: moves towards the player and rotates about a point, GameEngine handles the exploding
  + ShieldedClone: movement restricted by max angle change, with a working shield
  + Snake: contains 2 files and moves with a linked-list
- Collision between objects functioning, causing some problems yet, need reworking
- Scoring very simple, no menus of anything yet (just a simple score display)