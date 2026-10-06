// since we can't include foundry source, we need to predefine things that are imported in tests in here
// and fill them out with empty implementations so imports don't explode when they can't find
// existing classes in the global context
class Hooks {
    static on(key) {
    }
}

const _del = {}

const foundry = {
    ux: {},
    abstract: {
        DataModel: class {

        }
    },
    utils: {
        expandObject: () => {
        }
    },
    documents: {

    },
    data: {
        fields: {}
    },
    helpers: {},
    applications: {
        sidebar: {
            ActorDirectory: class {}
        },
        ui: {
            Hotbar: class {}
        },
        ux: {
            TextEditor: {
                implementation: class {
                }
            }
        },
        handlebars: {},
        api: {
            HandlebarsApplicationMixin: (klass) => {
                return class extends klass {
                }
            },
            ApplicationV2: class {
            },
            DocumentSheetV2: class {
            }
        }
    }
}