# Music Player
## Overview
The Music Player app is an Android application that allows users to browse their music library, play songs, and perform various actions such as shuffling, repeating, and navigating through tracks. It includes a user interface with multiple fragments and uses Android's MediaPlayer API for music playback.

## Features
### Browse Music Library:
Lists all available songs on the device.
### Play Music: 
Play, pause, skip, and rewind songs.
### Shuffle and Repeat: 
Options to shuffle the playlist and repeat the current song.
### Visualizer:
Audio visualizer to show music dynamics.
### Contextual Menu: 
Delete multiple songs using a contextual menu.
### Seekbar: 
Allows users to seek through the song.

## Components
### MainActivity.java
#### Description: 
Manages the main screen of the app, displays the list of songs, and handles user interactions like shuffling and deleting songs.
#### Key Functions:
runtime_permission(): Requests runtime permissions for accessing storage and recording audio.

Find_Songs(): Retrieves the list of songs from the device’s storage.

Display_Songs(): Displays the list of songs in a ListView.

customAdapter: Adapter for displaying songs in the ListView with support for selecting and deleting songs.

### Player.java
#### Description:
Handles the music player interface, including play, pause, skip, rewind, and shuffle functionalities.
#### Key Functions:
onCreate(): Initializes the player and sets up the UI elements like buttons and seekbar.

play_btn, next_btn, previous_btn, forward_btn, rewind_btn, repeat_btn, shuffle_btn: Buttons for controlling playback.

seek_music: Seekbar for navigating through the song.

visualizer: Displays audio visualization.

### FirstFragment.java
#### Description: 
A fragment that serves as the initial screen of the app, allowing navigation to the second fragment.

 
## This app also contains a cool visualization while playing the songs;  
 
 ![image](https://github.com/amax33/MusicPlayer/assets/77959684/19beacb8-fd46-4c2a-959d-beba9a6a59ae)
