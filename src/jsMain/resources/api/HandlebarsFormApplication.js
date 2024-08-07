/**
 * Needed to assign to the static parts variable
 */
function HandlebarsFormApplication(clazz) {
    return class Hack extends foundry.applications.api.HandlebarsApplicationMixin(clazz) {
        static PARTS = {}

        constructor(config) {
            super(
                {
                    ...config,
                    form: config.form !== undefined ?
                        {...config.form, handler: Hack._onSubmit}
                        : undefined,
                });
            if (config.form) {
                Hack.PARTS = {
                    form: {
                        template: config.templatePath,
                        scrollable: config.scrollable,
                    }
                }
            }
        }

        static async _onSubmit(event, form, formData) {
            await this.onSubmit(event, form, formData)
        }

        async onSubmit(event, form, formData) {
        }
    }
}
