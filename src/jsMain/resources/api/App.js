/**
 * Sane wrapper around app v2 to get rid of all the bullshit
 */
class App {
    constructor(
        {
            title,
            templatePath,
            submitOnChange = false,
            closeOnSubmit = false,
            isForm = true,
        }
    ) {
        const that = this;
        const form = isForm ? {
            tag: 'form',
            form: {
                handler: async (event, form, data) => {
                    await that.onSubmit(event, form, data);
                },
                submitOnChange,
                closeOnSubmit,
            }
        } : {}
        that.app = class AppV2Condom extends foundry.applications.api.HandlebarsApplicationMixin(foundry.applications.api.ApplicationV2) {
            constructor() {
                super();
                that.onInit();
            }

            static DEFAULT_OPTIONS = {
                ...form,
                window: {
                    title,
                }
            }
            static PARTS = {
                form: {
                    template: templatePath
                }
            }

            _onRender() {
                super._onRender()
                that.bindEventListeners(this.element);
            }

            async _preClose(options) {
                await super._preClose(options)
                await that.beforeClose(options)
            }
        }

    }

    bindEventListeners(element) {

    }

    onInit() {

    }

    async beforeClose() {

    }

    async onSubmit(event, form, data) {

    }

    async reRender() {
        await this.instance.render();
    }

    async launch() {
        this.instance = await (new this.app).render({force: true});
    }

    async close() {
        await this.instance.close();
    }
}