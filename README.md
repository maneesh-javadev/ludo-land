# 🎲 Ludo Land

**Ludo Land** is a vibrant, modern Android application built with **Jetpack Compose** and **Material 3**. It features a 3D claymorphic UI design, animated background, pure Kotlin Ludo game engine, intelligent AI difficulty strategies, customizable game setups, haptic/audio feedback, and persistent game state saving.

---

## 📁 Project Structure Overview

```
com.creative.ludoland/
├── MainActivity.kt                  # Main Host Activity & screen navigation controller
├── data/
│   ├── DataStoreManager.kt         # Preferences DataStore for games played & win stats
│   ├── SettingsRepository.kt       # Persistent settings repository (sound, vibration, color-blind mode, AI difficulty, player name)
│   └── GameSaveRepository.kt       # Serialized GameState persistence via kotlinx.serialization
├── game/                            # Pure Kotlin Game Engine Domain (No Android dependencies)
│   ├── PlayerColor.kt               # PlayerColor enum (RED, YELLOW, GREEN, BLUE)
│   ├── Token.kt                     # Token data class (id, color, position -1..58)
│   ├── Player.kt                    # Player data class & PlayerType enum (HUMAN, AI)
│   ├── GameState.kt                 # State container & GamePhase enum (WAITING_ROLL, WAITING_MOVE, GAME_OVER)
│   ├── BoardMapping.kt              # 15x15 Ludo board grid path mapping, start cells & safe cells
│   ├── GameEngine.kt                # Rule engine, dice rolls, captures, exact finishes & undo history
│   ├── AiStrategy.kt                # AI strategy implementations (EasyAi, MediumAi, HardAi)
│   └── LudoViewModel.kt             # Android ViewModel managing game UI flows, timers & persistence
├── ui/
│   ├── theme/                       # Colors, typography, custom theme & PlayerColor extensions
│   ├── components/                  # ClayButton, GameBackground, ClayCard, DiceView, LudoBoard, TokenSkin & VisualEffects
│   └── screens/                     # SplashScreen, HomeScreen, PlayerSetupScreen, GameScreen, ResultScreen & SettingsScreen
└── util/
    ├── SoundManager.kt              # Audio player for rolls, hops, captures & wins with SoundPool / ToneGenerator
    └── HapticManager.kt             # Haptic vibration generator for dice rolls & captures
```

---

## 📜 Game Rules

1. **Board Layout**:
   - 4 bases (Red, Yellow, Green, Blue) with 4 tokens each.
   - 52-cell main circuit, 4 start cells, 4 star safe cells, and 4 dedicated 6-cell home columns leading to the center goal (position 58).
2. **Exiting Base**:
   - A token can exit the base to the start cell (`position = 0`) **only on rolling a 6**.
3. **Extra Turns & 3-Sixes Rule**:
   - Rolling a **6** grants an extra turn.
   - Rolling **three 6s in a row forfeits the turn** immediately.
4. **Captures & Safe Cells**:
   - Landing on an opponent's token on a non-safe cell captures it and sends it back to their base (`position = -1`). Capturing awards a **bonus turn**.
   - Safe cells (start cells + star cells) protect tokens from capture.
5. **Finishing & Winning**:
   - Tokens require an **exact roll** to reach position 58. Overshooting is illegal.
   - Reaching the center awards a **bonus turn**.
   - Game continues until 1st, 2nd, and 3rd ranks are decided.

---

## 🎨 How to Replace Artwork & Audio

1. **Token Skins & Pawn Headgear**:
   - Open [`ui/components/TokenSkin.kt`](file:///C:/Users/kumar/AndroidStudioProjects/ludoland/app/src/main/java/com/creative/ludoland/ui/components/TokenSkin.kt).
   - Implement the `TokenSkin` interface to draw custom Canvas shapes or load vector/image drawables (`ImageVector` / `Bitmap`).
2. **Custom Sound Resources**:
   - Drop MP3/WAV files into `app/src/main/res/raw/` with exact filenames:
     - `dice_roll.mp3`
     - `token_move.mp3`
     - `capture.mp3`
     - `home_reach.mp3`
     - `win.mp3`
     - `button_click.mp3`
   - See [`app/src/main/res/raw/audio_readme.txt`](file:///C:/Users/kumar/AndroidStudioProjects/ludoland/app/src/main/res/raw/audio_readme.txt) for details.

---

## 🌐 How to Add Online Multiplayer Later

To expand **Ludo Land** into a real-time online multiplayer game:

1. **Decoupled Engine Ready**:
   - The [`GameEngine.kt`](file:///C:/Users/kumar/AndroidStudioProjects/ludoland/app/src/main/java/com/creative/ludoland/game/GameEngine.kt) domain is pure Kotlin and fully serializable via `kotlinx.serialization`.
2. **Network Layer (Firebase / WebSockets / Ktor)**:
   - Create a `MultiplayerRepository` observing a WebSocket or Firebase Realtime Database room node (e.g. `/rooms/{roomId}/gameState`).
   - When a local player acts, call `GameEngine.rollDice(state)` or `GameEngine.applyMove(state, token)` and send the updated `GameState` JSON payload over the socket.
   - On incoming remote network events, update `_uiState.value = GameUiState(gameState = receivedState)`.

---

## 🐛 Known Bugs & Remaining TODOs

- **No Known Bugs**: All 12 unit tests pass and debug builds compile cleanly.
- **Future Enhancements**:
  - Add optional 3D sound effects and customizable dice skin colors.
  - Implement online room matchmaking with Firebase or Ktor WebSockets.
