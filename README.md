## StiQ Fighter

*StiQ Fighter* is a simple two-dimensional fighting game that showcases reinforcement learning behaviour
using a variation of Q-learning.

### Getting Started
In its current state, *StiQ Fighter* can only be accessed by cloning the repository and running the application directly:

```bash
git clone https://github.com/decales/StiQ-Fighter && cd StiQ-Fighter && ./run
```
Should you wish to build an executable JAR file, you may do so by executing ```./build``` within the repository, and the
JAR will be found in the *out/* directory. Please note that Java 21+ is required to run the application.

### Gameplay

Upon launching the application, you may select one of the three game modes
from the menu:

- *PvP* – combat between two human players
- *PvC* – combat between one human player (left-side) and one computer player (right-side)
- *CvC* – combat between two computer players

At the start of a round, each fighter has 7 health-points (represented by the bar of heart sprites located at the top
of the screen on the side the player begins at) and must deplete their opponent’s health-points through combat to
win the round. The combat system is kept minimalist in an attempt to not over complicate the Q-learning
implementation, limiting each fighter to the following actions with the following gameplay mechanics:

- Move-left
- Move-right
- Attack
    - a fighter is only considered to be *attacking* during the ‘thrusting’ part of the animation, and not
       during the animation’s wind-up or wind-down
    - attacks are successful when the opponent is not *blocking, deflecting,* or *invincible* and the tip of a
       fighter’s weapon reaches or passes the opponent’s wielding hand (roughly)
    - successful attacks reduce the opponent’s health-points by 1
    - successful attacks while the opponent is *parried* reduce their health-points by 3
    - after receiving damage from an attack, the opponent is *invincible* for the next 75 frames (1250 ms)
    - while *attacking*, a fighter’s hit-box moves slightly forward in the direction of the attack before
       returning to its original position to reflect where their sprite appears to be in the animation
    - a fighter cannot execute other actions until the entire attack animation completes
- Block
    - a fighter is only considered to be *blocking* after the wind-up of the block animation and before the wind-down
    - while *blocking*, a fighter does not receive damage from the opponent’s next attack that would
       otherwise be successful
    - after the opponent’s attack lands when a fighter is *blocking* , the fighter returns to the *idle* state after a
       brief *deflecting* animation to indicate that the block was successful
    - in addition to preventing damage to a fighter, the opponent becomes *parried* if their next attack
       lands within 5 frames (~83 ms) after the fighter begins *blocking – parried* opponents cannot execute
       any actions for the next 65 frames (~1083 ms) or until they receive damage.
    - a fighter cannot execute other actions until the entire block animation completes.
    - **TIP** – note the star that appears near a fighter’s hand during the wind-up of the block animation – upon disappearing, this indicates that a fighter is now blocking , and can be helpful in timing the parry window.

#### Controls

In *PvC* mode, the human player is always the left fighter, and the right-fighter can only be controlled in *PvP*.
The key binding for each action are as follows:

| Action | Left fighter | Right righter (*PvP* only) |
| ------ | ------------ | -------------------------- |
| Move left | A | K |
| Move right | S | L |
| Attack | Q | I |
| Block | W | O |

<br>

### Structure

The game is implemented as a JavaFX application that uses an MVC-like design pattern to structure its various
classes. In this case, there is a single model class containing the game logic and data that communicates with
various view classes through a publish-subscribe mechanism. A simple controller class is used to handle the
events generated in the view classes and update the model. The project directory structure is organized to reflect
the components of MVC, where most of the classes are organized into separate *model/* and *view/* sub-directories.
The rest of the classes, including the controller class, are not contained in a sub-directory:

- *model/*
    - *ComputerFighter.java –* class containing logic to control computer fighters
    - *Fighter.java –* base class containing the logic and data to generally control a fighter
    - *GameState.java –* data class to used by computer fighter to representing a state of the game
    - *Model.java –* main class containing the game and application logic
    - *PlayerFighter.java –* class containing logic to control a fighter with keyboard input
    - *PublishSubscribe.java –* interface to send data from the model to the view components
- *view/*
    - *game/*
       - *FighterBar.java –* parent container class representing the top bar for a fighter
       - *FighterView.java –* component to visually represent a fighter on-screen
       - *GameView.java –* parent container class containing all components of an ongoing match
       - *HealthBar.java –* component of *FighterBar* to display a fighter’s current health
       - *QuitButton.java – Button* component to return to the menu screen during an ongoing match
       - *WinMarker.java –* component of *FighterBar* to display how many rounds a fighter has won
    - *menu/*
       - *MenuButton.java –* component to select a game mode from the menu
       - *MenuSelection.java –* component to display the current game mode selected in the menu
       - *MenuView.java –* parent container class representing the entire menu as a whole
- *App.java –* class to initialize components related to the application itself (stage, scene, root, etc.)
- *Controller.java –* event handling class used by view components to perform actions in the model
- *Main.java –* entry point class required to build JavaFX .jar files

The files of interest relating to the implementation of the AI fighter are all contained in the model directory, 
and consist specifically of *ComputerFighter.java* and *GameState.java.* The former of these classes is where the 
Q-learning algorithm is set up and implemented, while the latter is used within *ComputerFighter* to support its implementation.

### Q-learning

Within *ComputerFighter,* the Q-learning process is centered around maintaining the *qTable* , which maps game
states to action-value pairs, implemented specifically as a *HashMap* of type *HashMap<GameState,
Map<Action, Double>>.* Regarding the states themselves, there is little to comment on other than that they are
designed to be as coarse as possible while capturing the necessary data that forms the basis of the an AI fighter’s
behaviour. In this case, a *GameState* is comprised mostly of boolean values regarding the AI fighter’s opponent,
such as whether they are in attacking distance, or whether they were recently hit. Each frame, the
*determineAction()* function is called from *Model.java,* where the AI observes the current game state, selects an
action using an epsilon-greedy policy (balancing exploration and exploitation), and executes it. The state-action
pair is then evaluated based on the immediate reward function, *scoreAction(),* which assigns positive or negative
rewards depending on the consequences of the chosen action (such as successfully attacking an opponent or
making an unnecessary block). Finally, the *updateTable()* function updates the Q-value for the previous state-
action pair using the following variant of the Q-learning formula:

*Q(s, a) = (1 − α)* * *Q(s , a) + α * (r + γ * max(Q(s′, a′)))*

where α (alpha) is the learning rate, r is the immediate reward, γ (gamma) is the discount factor, and *max(Q(s′,
a′)* is the highest Q-value for the new state. Over time, the AI refines its strategy, favouring high-reward actions
while gradually reducing exploration through state-dependent epsilon decay.

#### State-dependent Epsilon Exploitation

Rather than a universal epsilon value, the Q-learning implementation uses various state-dependent epsilons to
control the rate of exploitation. In this case, new states are initialized with a baseline epsilon value (0.5) which
decays by 1.5% each time a state is revisited. The intuition of this approach is to promote exploration in
unfamiliar states and exploitation in familiar ones. Rather than treating each state equally in terms of an AI
fighter’s mastery of the entire state space, mastery is considered on a per-state basis, where common states are
mastered earlier than rare ones.

In practice, this shows clear benefits in states where the fighters are not in attacking distance of each other. This
is particularly useful in *CvC* mode, as both fighters quickly learn that the best move to exploit in these states is
simply to approach their opponent. As opposed to the standard epsilon exploration method used in previous
versions of the game, this significantly speeds up of the fighters’ training by allowing them to explore the more
complex (and arguably more important) in-attacking-distance states sooner. On the contrary this also means that
rare states will maintain relatively high baseline values into later rounds of the game, such as the states where a
fighter has parried their opponent. As rare and as crucial as these states are towards a fighter’s likelihood of
winning a round, there is still a <50% chance to capitalize on a parry with a follow-up attack so long as the
fighter has explored doing so in a state prior.

#### Behaviour

In my experience playing the game and observing the AI in PvC mode, I find that its behaviour varies with each
play test. In general, it seems to take ~10 rounds on average for the AI to "master" the state space, and while I
have noticed common patterns of behaviour between each test, the way it masters the state space is often
different. For example, in most play tests, I encountered a predictable, impatient form of the AI that avoids
blocking and decides that it is nearly always in its best interest to trade attacks with the player. In other cases, the
AI can take on a more defensive play-style, waiting patiently for the player to enter a vulnerable state before it
attacks, or for the player to attack so it can capitalize on a parry. I believe each play style is highly dependent on
how often the AI is successful to either land an attack or block the opponent’s attack earlier on its its training,
which is ultimately decided by how its opponent is playing and the random nature of exploration. This diversity
makes the AI much more suited for *PvC* mode, as once it is considered to have mastered the state space, it is
capable of fairly ‘intelligent’ behaviour relevant to the simplicity of the actions available to it. Some of these
behaviours include side-stepping attacks, optimally spacing from the opponent, parrying (which is rate for the
reasons previously discussed), and attacking the opponent in vulnerable states, whether they be in a block or
attack wind-down animation. I consider the latter of these vulnerable state exploitation actions to be the most
difficult and rewarding form of skill expression in the game, even for human players. After attacking (feel free to
skip this part), there is a precise window at the beginning of the attacker’s wind-down animation where their
opponent can follow-up with an attack without receiving damage. This occurs because an attacker’s hit-box
moves slightly forward with their thrust before gradually returning to its original position in the wind-down
animation. In this scenario, if the fighter receiving the attack positions themselves just within attack range of
their opponent, quickly side steps the attack right before it would land, then immediately moves back into
range to follow-up with a precisely timed attack, they are able to damage the opponent while preventing taking
themselves. In my experience, this is easier said than done, and makes for a formidable AI opponent when it
learn this behaviour.

As for *CvC* mode, I notice that the AI fighters tend to be more rigid and predictable in their actions. Again, it
takes ~10 round for both AI fighters to master the state space and reach a point where they appear to fight
fluently. Unlike PvC, however, this state of mastery rarely diverts from the same behaviours. While both fighters
learn when they should attack and when they should block, these actions are usually restricted to the states where
their opponent is winding down from a block or initiating attack, respectively. This can cause the fighters to
remain fixed in place, stuck in a cycle of attacking and blocking each other until one fighter finally executes a
random action (which can be a painfully drawn out process to sit through). In some cases, a fighter may favour
side-stepping an attack over blocking, but aside from this, their behaviour remains consistent from trial to trial.
This makes sense the AI’s implementation – both fighters have the exact same reward system, and the set of
actions is not broad enough to promote a diversity of behaviours when the fighters are within attack range. I
considered addressing this by implementing different ‘profiles’ for the AI fighters to punish and reward them
differently to promote different play styles, but this ultimately did not make it into the final version of the game.

### Bugs and Issues

At the moment, there are no significant issues with the game, and given the black-box nature of Q-learning, it
can be difficult to gauge the quality of the AI fighter’s behaviour and whether it is behaving exactly according to
how it is rewarded. The only bug that comes to mind is related to the animation of an attacking AI fighter – at
times, the attack wind-up animation does not play, and the AI is suddenly thrust forward into an attack.
