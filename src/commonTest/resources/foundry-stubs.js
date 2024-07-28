const foundry = {
    utils: {
        expandObject: () => {
        }
    },
    data: {
        fields: {}
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