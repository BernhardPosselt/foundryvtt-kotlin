package at.posselt.pfrpg.camping.dialogs

import at.posselt.pfrpg.app.forms.CheckboxInput
import at.posselt.pfrpg.app.CrudApplication
import at.posselt.pfrpg.app.CrudData
import at.posselt.pfrpg.app.CrudItem
import at.posselt.pfrpg.camping.*
import at.posselt.pfrpg.utils.buildPromise
import at.posselt.pfrpg.utils.launch
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
    id = "kmManageActivities"
) {
    override fun deleteEntry(id: String) = buildPromise {
        actor.getCamping()?.let { camping ->
            camping.homebrewCampingActivities =
                camping.homebrewCampingActivities.filter { it.name != id }.toTypedArray()
            camping.campingActivities.filter { it.activity != id }
            actor.setCamping(camping)
            render()
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
                    val canBeEdited = activity.isHomebrew
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
                            disabled = activity.isPrepareCampsite(),
                            name = "enabledIds.$name",
                        ).toContext(),
                        canBeEdited = canBeEdited,
                        canBeDeleted = canBeEdited,
                    )
                }.toTypedArray()
        } ?: emptyArray()
    }

    override fun getHeadings(): Promise<Array<String>> = buildPromise {
        emptyArray()
    }

    override fun onParsedSubmit(value: CrudData): Promise<Void> = buildPromise {
        val enabled = value.enabledIds.toSet() + "Prepare Campsite"
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