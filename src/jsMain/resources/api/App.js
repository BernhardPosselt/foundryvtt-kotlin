/**
 * Sane wrapper around app v2 to get rid of all the bullshit
 */
class App {
    registeredHooks = [];
    eventListeners = [];

    constructor(
        {
            title,
            templatePath,
            submitOnChange = false,
            closeOnSubmit = false,
            isForm = true,
            dataModel,
            menuButtons = [],
        }
    ) {
        const that = this;
        const form = isForm ? {
            tag: 'form',
            form: {
                handler: async (event, form, data) => {
                    await that.onSubmit(data);
                },
                submitOnChange,
                closeOnSubmit,
            }
        } : {}
        that.app = class AppV2Condom extends foundry.applications.api.HandlebarsApplicationMixin(foundry.applications.api.ApplicationV2) {
            registeredHooks = [];

            constructor() {
                super();
                that.onInit();
                that.registeredHooks.forEach((hook) => {
                    Hooks.on(hook.key, hook.callback);
                    this.registeredHooks.push(hook);
                });
                if (dataModel) {
                    dataModel.apps[this.appId] = this;
                }
            }

            static DEFAULT_OPTIONS = {
                ...form,
                window: {
                    title,
                },
                actions: {
                    ...Object.fromEntries(menuButtons.map(button => {
                        return [button.action, () => {
                            that.onMenu(button.action);
                        }]
                    }))
                }
            }
            static PARTS = {
                form: {
                    template: templatePath
                }
            }

            _getHeaderControls() {
                return menuButtons;
            }

            _onRender() {
                super._onRender()
                that.eventListeners.forEach(({selector, callback, eventType = 'click'}) => {
                    this.element.querySelectorAll(selector)
                        ?.forEach(el => el.addEventListener(eventType, callback))
                });
                that.bindEventListeners(this.element);
            }

            async _preClose(options) {
                await super._preClose(options)
                this.registeredHooks.forEach((hook) => {
                    Hooks.off(hook.key, hook.callback);
                });
                await that.beforeClose(options)
            }
        }

    }

    bindEventListeners(element) {

    }

    onMenu(action) {

    }

    on(selector, eventType = 'click', callback) {
        this.eventListeners.push({selector, callback, eventType});
    }

    registerHook(key, callback) {
        this.registeredHooks.push({key, callback})
    }

    onInit() {

    }

    async beforeClose() {

    }

    async onSubmit(data) {

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