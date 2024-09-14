## Camping

* Illegal state exception when actor is assigned to activity for the first time (no skill synced, automatically set the
  first viable one on activity drop)
* app ids setzen für settings popups
* prevent prepare campsite from being disabled in the activities config
* meals
    * if a recipe is disabled, set all actors choosing it to nothing
    * filter out recipes that are not enabled
    * disable roll, skill and degree of success form elements if cook meal is not filled or prepare campsite is not at
      least a failure
    * sync meals dropdowns (keep in mind that disabled elements are omitted from form data)
    * Changing the degree of success of a meal should:
        * post the message of the outcome to chat
        * post a button to remove ingredients & apply effects
        * post a button to just apply effects
