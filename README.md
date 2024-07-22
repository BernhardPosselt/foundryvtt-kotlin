# FoundryVTT Kotlin

If you are interested in hacking on the code base, take a look at the [Kotlin JS Primer](./Kotlin%20JS%20Primer.md) for a quick intro. 

## Development Setup

Install the following things:

* JVM 21
* git
* node
* yarn

Then link this directory to your foundry data folder:

    ln -s /home/bernhard/dev/pf2e-kingmaker-tools-ng/ /home/bernhard/.local/share/FoundryVTT/Data/modules/pf2e-kingmaker-tools-ng/

Build the project using:

    ./gradlew build

Finally, start foundry

    cd dev/FoundryVTT-12.328/
    ./foundryvtt