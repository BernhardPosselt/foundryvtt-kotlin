package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import com.foundryvtt.core.abstract.Document
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface CreateThumbnailOptions {
    val width: Int?
    val height: Int?
    val img: String?
    val format: String?
    val quality: Double
}

@JsPlainObject
external interface Rect {
    val x: Int
    val y: Int
    val width: Int
    val height: Int
}

@JsPlainObject
external interface SceneDimensions {
    val width: Int
    val height: Int
    val size: Int
    val sceneX: Int
    val sceneY: Int
    val sceneWidth: Int
    val sceneHeight: Int
    val rect: Rect
    val sceneRect: Rect
    val distance: Int
    val distancePixels: Int
    val ration: Double
    val maxR: Int
    val rows: Int
    val columns: Int
}

@JsPlainObject
external interface SceneInitial {
    val x: Int?
    val y: Int?
    val scale: Double?
}

@JsPlainObject
external interface SceneGrid {
    val type: Int
    val size: Int
    val style: String
    val thickness: Int
    val color: String
    val alpha: String?
    val distance: Int
    val unit: String
}

@JsName("CONFIG.Scene.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class Scene : Document {
    companion object : DocumentStatic<Scene>

    override fun delete(operation: DatabaseGetOperation): Promise<Scene>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<Scene>

    var name: String
    var img: String
    var active: Boolean
    var nagivation: Boolean
    var navOrder: Int
    var navName: String
    var foreground: String
    var thumb: String
    var width: Int
    var height: Int
    var padding: Int
    var initial: SceneInitial
    var grid: SceneGrid

    //    var environment: SceneEnvironment
    //    var drawings: EmbeddedCollection<Drawing>
    //    var tokens: EmbeddedCollection<Token>
    //    var lights: EmbeddedCollection<Light>
    //    var notes: EmbeddedCollection<Note>
    //    var sounds: EmbeddedCollection<AmbientSound>
    //    var templates: EmbeddedCollection<MeasuredTemplate>
    //    var tiles: EmbeddedCollection<Tile>
    //    var walls : EmbeddedCollection<Wall>
    var playlist: Playlist?
    var playlistSound: PlaylistSound?

    //    var journal: Journal?
    //    var journalEntryPage: JournalEntryPage?
    var weather: String?
    var folder: Folder?
    var ownership: Ownership
    var sort: Int

    val thumbnail: String
    fun activate(): Promise<Unit>
    fun view(): Promise<Unit>
    fun getDimensions(): SceneDimensions
    fun createThumbnail(options: CreateThumbnailOptions = definedExternally): Promise<AnyObject>
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Scene.update(data: Scene, operation: DatabaseGetOperation = jso()): Promise<Scene> =
    update(data as AnyObject, operation)