# TODO

## Migrations
* All enemies and scenes with combat tracks:
  * read "name" property of actor and replace it with "playlistUuid"
* All actors that reference the following need to have their img and token img changed:
  * "Bridge, Stone"
  * "Bridge"
  * "Gladiatorial Arena"
  * "Magical Streetlamps"
  * "Paved Streets"
  * "Printing House"
  * "Sewer System"
  * "Wall, Stone"
  * "Wall, Wooden"
* CampingData needs to move the following settings off to the following properties:
  * proxyEncounterTable (a name) -> proxyRandomEncounterTableUuid (a uuid) 
  * randomEncounterRollMode -> randomEncounterRollMode
* set minimum migration level on first launch and warn when no migration is present
## Influence System
* Check what can be shipped under the OGL
* Ship Influence Statblocks
* Link statblocks to journal entries and if they don't exist, ask to import it

## Camping

* Roll random encounter button to chat on certain failed activities
* Roll activity check
* Sync camping effects
* Prepare Camp
* Implement navigation
* Update docs
* Remove JS
* Homebrew: Activities & Recipes
* Settings + Migration
* Reset Button for activities & prepare camp
* Design for eating
* Design Skill dropdowns/degree of success
* Add setting to increase Hexploration activities
* Resting (playlist + check + removing fx + healing + macro)
* Put combat efects to chat after rolling perception check
* Apply meal effects should ask if it should consume rations and ingredients instead of clicking on separate buttons