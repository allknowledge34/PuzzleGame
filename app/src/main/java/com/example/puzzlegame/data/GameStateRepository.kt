package com.example.puzzlegame.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.puzzlegame.model.BlockColor
import com.example.puzzlegame.model.Cell
import com.example.puzzlegame.model.CellOffset
import com.example.puzzlegame.model.GameState
import com.example.puzzlegame.model.Shape
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_state_prefs")

class GameStateRepository(context: Context) {

    private val dataStore = context.dataStore

    suspend fun save(state: GameState) {
        val json = JSONObject().apply {
            put("score", state.score)
            put("isGameOver", state.isGameOver)
            put("grid", serializeGrid(state.grid))
            put("shapes", serializeShapes(state.currentShapes))
            if (state.holdShape != null) {
                put("holdShape", serializeShape(state.holdShape))
            }
        }
        dataStore.edit { prefs ->
            prefs[KEY_SAVED_GAME] = json.toString()
        }
    }

    suspend fun load(): GameState? {
        val prefs = dataStore.data.first()
        val jsonStr = prefs[KEY_SAVED_GAME] ?: return null
        return try {
            val json = JSONObject(jsonStr)
            GameState(
                grid = deserializeGrid(json.getJSONArray("grid")),
                currentShapes = deserializeShapes(json.getJSONArray("shapes")),
                score = json.getInt("score"),
                isGameOver = json.getBoolean("isGameOver"),
                holdShape = json.optJSONObject("holdShape")?.let { deserializeShape(it) }
            )
        } catch (_: Exception) {
            null
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_SAVED_GAME)
        }
    }

    private fun serializeGrid(grid: List<List<Cell>>): JSONArray {
        val rows = JSONArray()
        for (row in grid) {
            val cells = JSONArray()
            for (cell in row) {
                cells.put(JSONObject().apply {
                    put("f", cell.filled)
                    put("c", cell.color.name)
                })
            }
            rows.put(cells)
        }
        return rows
    }

    private fun deserializeGrid(arr: JSONArray): List<List<Cell>> {
        return List(arr.length()) { r ->
            val row = arr.getJSONArray(r)
            List(row.length()) { c ->
                val obj = row.getJSONObject(c)
                Cell(
                    filled = obj.getBoolean("f"),
                    color = BlockColor.valueOf(obj.getString("c"))
                )
            }
        }
    }

    private fun serializeShape(shape: Shape): JSONObject = JSONObject().apply {
        put("color", shape.color.name)
        put("cells", JSONArray().apply {
            for (cell in shape.cells) {
                put(JSONObject().apply {
                    put("r", cell.row)
                    put("c", cell.col)
                })
            }
        })
    }

    private fun deserializeShape(obj: JSONObject): Shape {
        val cells = obj.getJSONArray("cells")
        return Shape(
            cells = List(cells.length()) { c ->
                val cell = cells.getJSONObject(c)
                CellOffset(row = cell.getInt("r"), col = cell.getInt("c"))
            },
            color = BlockColor.valueOf(obj.getString("color"))
        )
    }

    private fun serializeShapes(shapes: List<Shape?>): JSONArray {
        val arr = JSONArray()
        for (shape in shapes) {
            if (shape == null) arr.put(JSONObject.NULL)
            else arr.put(serializeShape(shape))
        }
        return arr
    }

    private fun deserializeShapes(arr: JSONArray): List<Shape?> {
        return List(arr.length()) { i ->
            if (arr.isNull(i)) null else deserializeShape(arr.getJSONObject(i))
        }
    }

    companion object {
        private val KEY_SAVED_GAME = stringPreferencesKey("saved_game")
    }
}