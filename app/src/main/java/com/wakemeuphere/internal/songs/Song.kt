package com.wakemeuphere.internal.songs

import android.net.Uri

class Song {

    var id: String = ""
        private set

    var cursorId: Long = 0
    var title: String = ""
    var artist: String = ""
    var isMusic: Boolean = false
    var isAlarm: Boolean = false

    lateinit var uri: Uri

    constructor(id: Long, title: String, artist: String, isMusic: Boolean, isAlarm: Boolean)
    {
        this.cursorId = id
        this.title = title
        this.artist = artist
        this.isMusic = isMusic
        this.isAlarm = isAlarm

        //create Id
        this.id = "$artist;$title;"
    }

    override fun toString() : String
    {
        if (artist.isNullOrBlank())
            return  title
        return "$title - $artist"
    }
}