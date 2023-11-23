package com.seven.astonplayer

class Song(val iD: Long, val title: String, val artist: String = "", val resID: Int)

val listSong: List<Song> = listOf(Song(iD = 0, title = "Voin", resID = R.raw.voin, artist = "Unknown"),
    Song(iD = 1, title = "In the End", resID = R.raw.in_the_end, artist = "Enigma"),
    Song(iD = 2, title = "We will rock you", artist = "Queen", resID = R.raw.we_will_rock_you)
    )