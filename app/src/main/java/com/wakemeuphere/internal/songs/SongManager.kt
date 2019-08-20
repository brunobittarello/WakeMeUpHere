package com.wakemeuphere.internal.songs

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.content.ContentUris


//https://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764
//https://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778
object SongManager {

    var songs: MutableList<Song> = mutableListOf()

    fun getSongById(id: String) : Song
    {
        var song = songs.find { it.id == id }
        if (song == null) song = songs[0]
        return  song
    }

    fun loadSongs(context: Context)
    {
        Log.w("SongManager", "loadSongs")


        loadFromUri(context.contentResolver, android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI)
        loadFromUri(context.contentResolver, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)

        songs.forEach { Log.d("Songs", it.toString())}
    }

    private fun loadFromUri(musicResolver: ContentResolver, uri: Uri)
    {
        val musicCursor = musicResolver.query(uri, null, null, null, null)

        if (musicCursor == null)
        {
            Log.w("SongManager", "Cursor is null")
            return
        }

        if (musicCursor.moveToFirst() == false)
        {
            Log.w("SongManager", "No song found")
            return
        }

        //get columns
        val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
        val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
        val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
        val isAlarmColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.IS_ALARM)
        val isMusicColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.IS_MUSIC)
        //val isMusicColumn2 = musicCursor.getColumnIndex(android.provider.MediaStore.Images.Media.DISPLAY_NAME)//file name
        //val isMusicColumn2 = musicCursor.getColumnIndex(android.provider.MediaStore.Images.Media.DATE_ADDED)//file name

        //add songs to list
        do {
            val id = musicCursor.getLong(idColumn)
            val title = musicCursor.getString(titleColumn)
            val artist = musicCursor.getString(artistColumn)
            val isAlarm = musicCursor.getInt(isAlarmColumn) != 0
            val isMusic = musicCursor.getInt(isMusicColumn) != 0

            if (isMusic || isAlarm)
            {
                var song = Song(id, title, artist, isMusic, isAlarm)
                song.uri = ContentUris.withAppendedId(uri, id)
                songs.add(song)
            }
        } while (musicCursor.moveToNext())
    }
}