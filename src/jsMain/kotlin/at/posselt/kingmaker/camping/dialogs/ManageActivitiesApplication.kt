package at.posselt.kingmaker.camping.dialogs

import at.posselt.kingmaker.app.forms.CheckboxInput
import at.posselt.kingmaker.app.CrudApplication
import at.posselt.kingmaker.app.CrudData
import at.posselt.kingmaker.app.CrudItem
import at.posselt.kingmaker.camping.*
import at.posselt.kingmaker.utils.buildPromise
import at.posselt.kingmaker.utils.launch
import com.foundryvtt.core.Game
import com.foundryvtt.pf2e.actor.PF2ENpc
import js.core.Void
import kotlin.js.Promise

@JsExport
class ManageActivitiesApplication(
    private val game: Game,
    private val actor: PF2ENpc,
) : CrudApplication(
    title = "Manage Activities",
    debug = true,
) {
    override fun deleteEntry(id: String) = buildPromise {
        actor.getCamping()?.let { camping ->
            camping.homebrewCampingActivities =
                camping.homebrewCampingActivities.filter { it.name != id }.toTypedArray()
            camping.campingActivities.filter { it.activity != id }
            actor.setCamping(camping)
        }
        undefined
    }

    override fun addEntry(): Promise<Void> = buildPromise {
        ActivityApplication(
            game,
            actor,
            afterSubmit = { render() },
        ).launch()
        undefined
    }

    override fun editEntry(id: String) = buildPromise {
        ActivityApplication(
            game,
            actor,
            actor.getCamping()?.homebrewCampingActivities?.find { it.name == id },
            afterSubmit = { render() },
        ).launch()
        undefined
    }

    override fun getItems(): Promise<Array<CrudItem>> = buildPromise {
        actor.getCamping()?.let { camping ->
            val locked = camping.lockedActivities.toSet()
            camping.getAllActivities()
                .sortedWith(compareBy(CampingActivityData::name))
                .map { activity ->
                    val name = activity.name
                    val isHomebrew = activity.isHomebrew ?: false
                    val enabled = !locked.contains(name)
                    CrudItem(
                        id = name,
                        name = name,
                        nameIsHtml = false,
                        additionalColumns = emptyArray(),
                        enable = CheckboxInput(
                            value = enabled,
                            label = "Enable",
                            hideLabel = true,
                            name = "enabledIds.$name",
                        ).toContext(),
                        canBeEdited = isHomebrew,
                        canBeDeleted = isHomebrew,
                    )
                }.toTypedArray()
        } ?: emptyArray()
    }

    override fun getHeadings(): Promise<Array<String>> = buildPromise {
        emptyArray()
    }

    override fun onParsedSubmit(value: CrudData): Promise<Void> = buildPromise {
        val enabled = value.enabledIds.toSet()
        actor.getCamping()?.let { camping ->
            camping.lockedActivities = camping.getAllActivities()
                .filter { !enabled.contains(it.name) }
                .map { it.name }
                .toTypedArray()
            actor.setCamping(camping)
        }
        undefined
    }
}