const foundry = {
    utils: {
        expandObject: () => {
        }
    },
    applications: {
        api: {
            HandlebarsApplicationMixin: (klass) => {
                return class extends klass {
                }
            },
            ApplicationV2: class {
            }
        }
    }
}