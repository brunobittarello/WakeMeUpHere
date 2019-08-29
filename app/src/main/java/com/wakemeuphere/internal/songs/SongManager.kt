package com.wakemeuphere.internal.songs

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.content.ContentUris
import android.database.Cursor
import android.provider.MediaStore.Audio.Media

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
        searchForAudios(context.contentResolver)
        songs.forEach { Log.d("Songs", it.toString())}
    }

    private fun searchForAudios(musicResolver: ContentResolver)
    {
        val projection = arrayOf (Media.TITLE, Media._ID, Media.ARTIST, Media.IS_ALARM, Media.IS_MUSIC)
        var musicCursor = musicResolver.query(Media.INTERNAL_CONTENT_URI, projection, null, null, null)
        loadFromUri(musicCursor, true, Media.INTERNAL_CONTENT_URI)

        musicCursor = musicResolver.query(Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        loadFromUri(musicCursor, false, Media.EXTERNAL_CONTENT_URI)
    }

    private fun loadFromUri(musicCursor: Cursor?, isInternal: Boolean, uri: Uri)
    {
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
        val titleColumn   = musicCursor.getColumnIndex(Media.TITLE)
        val idColumn      = musicCursor.getColumnIndex(Media._ID)
        val artistColumn  = musicCursor.getColumnIndex(Media.ARTIST)
        val isAlarmColumn = musicCursor.getColumnIndex(Media.IS_ALARM)
        val isMusicColumn = musicCursor.getColumnIndex(Media.IS_MUSIC)
        //val isMusicColumn2 = musicCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)//file name
        //val isMusicColumn2 = musicCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)//file name
        //val length = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        //val isNotificationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE)

        //add songs to list
        do {
            val id = musicCursor.getLong(idColumn)
            val title = musicCursor.getString(titleColumn)
            val artist = musicCursor.getString(artistColumn)
            val isAlarm = musicCursor.getInt(isAlarmColumn) != 0
            val isMusic = musicCursor.getInt(isMusicColumn) != 0
            //val duration = musicCursor.get(isMusicColumn)
            //val isNotification = musicCursor.getInt(isNotificationColumn) != 0

            if ((isInternal && isAlarm) || (!isInternal && isMusic))
            {
                var song = Song(id, title, artist, isMusic, isAlarm)
                song.uri = ContentUris.withAppendedId(uri, id)
                songs.add(song)
            }
        } while (musicCursor.moveToNext())
    }
}

/*
_id
_data
_display_name
_size
mime_type
date_added
is_drm
date_modified
title
title_key
duration
artist_id
composer
album_id
track
year
is_ringtone
is_music
is_alarm
is_notification
is_podcast
bookmark
album_artist
artist_id:1
artist_key
artist
album_id:1
album_key
album
*/