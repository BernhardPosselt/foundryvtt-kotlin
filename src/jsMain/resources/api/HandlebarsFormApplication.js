/**
 * Get rid of the crap mixin pattern and make it possible to define parts
 * without statics
 */
class HandlebarsFormApplication extends foundry.applications.api.ApplicationV2 {
    constructor(config) {
        super(config);
        console.log(config)
        this.PARTS.form.template = config.templatePath;
    }

    static DEFAULT_OPTIONS = {
        tag: "form",
        form: {
            handler: HandlebarsFormApplication._onSubmit,
            submitOnChange: true,
            closeOnSubmit: false
        }
    }

    static async _onSubmit(event, form, formData) {
        return this.onSubmit(event, form, formData)
    }

    async onSubmit(event, form, formData) {

    }

    PARTS = {
        form: {
            template: null
        }
    }

    get parts() {
        return this.#parts;
    }

    #parts = {};

    _configureRenderOptions(options) {
        super._configureRenderOptions(options);
        options.parts ??= Object.keys(this.PARTS);
    }

    async _preFirstRender(context, options) {
        await super._preFirstRender(context, options);
        const allTemplates = new Set();
        for (const part of Object.values(this.PARTS)) {
            const partTemplates = part.templates ?? [part.template];
            for (const template of partTemplates) allTemplates.add(template);
        }
        await loadTemplates(Array.from(allTemplates));
    }

    async _renderHTML(context, options) {
        const rendered = {};
        for (const partId of options.parts) {
            const part = this.PARTS[partId];
            if (!part) {
                ui.notifications.warn(`Part "${partId}" is not a supported template part for ${this.constructor.name}`);
                continue;
            }
            const partContext = await this._preparePartContext(partId, context, options);
            try {
                const htmlString = await renderTemplate(part.template, partContext);
                rendered[partId] = this.#parsePartHTML(partId, part, htmlString);
            } catch (err) {
                throw new Error(`Failed to render template part "${partId}":\n${err.message}`, {cause: err});
            }
        }
        return rendered;
    }

    async _preparePartContext(partId, context, options) {
        context.partId = `${this.id}-${partId}`;
        return context;
    }

    #parsePartHTML(partId, part, htmlString) {
        const t = document.createElement("template");
        t.innerHTML = htmlString;
        if ((t.content.children.length !== 1)) {
            throw new Error(`Template part "${partId}" must render a single HTML element.`);
        }
        const e = t.content.firstElementChild;
        e.dataset.applicationPart = partId;
        if (part.id) e.setAttribute("id", `${this.id}-${part.id}`);
        if (part.classes) e.classList.add(...part.classes);
        return e;
    }

    _replaceHTML(result, content, options) {
        for (const [partId, htmlElement] of Object.entries(result)) {
            const priorElement = content.querySelector(`[data-application-part="${partId}"]`);
            const state = {};
            if (priorElement) {
                this._preSyncPartState(partId, htmlElement, priorElement, state);
                priorElement.replaceWith(htmlElement);
                this._syncPartState(partId, htmlElement, priorElement, state);
            } else content.appendChild(htmlElement);
            this._attachPartListeners(partId, htmlElement, options);
            this.#parts[partId] = htmlElement;
        }
    }

    _preSyncPartState(partId, newElement, priorElement, state) {
        const part = this.PARTS[partId];

        // Focused element or field
        const focus = priorElement.querySelector(":focus");
        if (focus?.id) state.focus = `#${focus.id}`;
        else if (focus?.name) state.focus = `${focus.tagName}[name="${focus.name}"]`;
        else state.focus = undefined;

        // Scroll positions
        state.scrollPositions = [];
        for (const selector of (part.scrollable || [])) {
            const el0 = selector === "" ? priorElement : priorElement.querySelector(selector);
            if (el0) {
                const el1 = selector === "" ? newElement : newElement.querySelector(selector);
                if (el1) state.scrollPositions.push([el1, el0.scrollTop, el0.scrollLeft]);
            }
        }
    }

    _syncPartState(partId, newElement, priorElement, state) {
        if (state.focus) {
            const newFocus = newElement.querySelector(state.focus);
            if (newFocus) newFocus.focus();
        }
        for (const [el, scrollTop, scrollLeft] of state.scrollPositions) Object.assign(el, {scrollTop, scrollLeft});
    }

    _attachPartListeners(partId, htmlElement, options) {
        const part = this.PARTS[partId];

        // Attach form submission handlers
        if (part.forms) {
            for (const [selector, formConfig] of Object.entries(part.forms)) {
                const form = htmlElement.matches(selector) ? htmlElement : htmlElement.querySelector(selector);
                form.addEventListener("submit", this._onSubmitForm.bind(this, formConfig));
                form.addEventListener("change", this._onChangeForm.bind(this, formConfig));
            }
        }
    }
}