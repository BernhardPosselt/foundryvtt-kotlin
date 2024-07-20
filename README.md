# FoundryVTT Kotlin

## Development Setup

Install the following things:

* JVM 21
* git
* node
* npm

Then link this directory to your foundry data folder:

    ln -s /home/bernhard/dev/pf2e-kingmaker-tools-ng/ /home/bernhard/.local/share/FoundryVTT/Data/modules/pf2e-kingmaker-tools-ng/

Build the project using:

    ./gradlew build

Finally, start foundry

    cd dev/FoundryVTT-12.328/
    ./foundryvtt