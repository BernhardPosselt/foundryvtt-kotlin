# TODO

* Update manual
* Add upgrading notice
* Always display manual in chat unless specifically disabled
* Test camping & rolling random encounters

## Camping

* Firstrun tutorial
* Changing the degree of success value of an activity should post random encounters or other values to chat if required
* Don't consider locked activities for effect syncing unless companion checkboxes are clicked
* Sync camping effects
* Update docs
* Resting (playlist + check + removing fx + healing + macro)
* Put combat effects to chat after rolling perception check
* Apply meal effects should ask if it should consume rations and ingredients instead of clicking on separate buttons
* remove provisions from inventories after rest
* Implement new food attributes:
    * halvesHealing
    * healFormula
    * damageFormula
    ```js
  (await new (CONFIG.Dice.rolls.find(r => r.name == "DamageRoll"))("3d6[fire]").roll()).toMessage()
    ```
    * healAfterConsumptionAndRest