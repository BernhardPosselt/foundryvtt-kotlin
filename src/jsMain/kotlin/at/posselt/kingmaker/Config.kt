package at.posselt.kingmaker

object Config {
    const val moduleId = "pf2e-kingmaker-tools"
    const val moduleName = "Kingdom Building, Camping & Weather"

    object rollTables {
        const val compendium = "$moduleId.kingmaker-tools-rolltables"
        const val weather = "Weather Events"
        const val randomEncounters = "Random Encounters"
    }

    object regions {
        const val defaultRegion = "Rostland Hinterlands"
    }

    object kingmakerModule {
        object combat {
            val playlistId = "7CiwVus60FiuKFhK"
        }

        object weather {
            const val playlistId = "c6WJzHWMM72zP19H"
        }
    }
}



