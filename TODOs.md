## Camping

* Illegal state exception when actor is assigned to activity for the first time (no skill synced, automatically set the
  first viable one on activity drop)
* camping activities überspringen wenn prepare campsite null oder crit fail
* app ids setzen für settings popups
* prevent prepare campsite from being disabled in the activities config
* disabled rezepte werden nicht raus gefiltered
* rezept disablen setzt alle meals die es gesetzt habe auf "nothing"
* wenn kein cook meal activity besetzt ist oder prepare campsite null/crit fail ist, ist der roll button und degree of
  success dropdown disabled
* Changing the degree of success of a meal should:
    * post the message of the outcome to chat
    * post a button to remove ingredients & apply effects
    * post a button to just apply effects
* Resting (playlist + check + removing fx + healing + macro)
* Put combat effects to chat after rolling perception check
* remove provisions from inventories after rest
* Implement new food attributes:
    * halvesHealing
    * healAfterConsumptionAndRest
