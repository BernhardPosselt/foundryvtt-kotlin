((exports) => {
    function isObject(x) {
        return typeof x === 'object' && !Array.isArray(x) && x !== null;
    }

    /**
     * Fucking hell, Foundry's expandObject does not properly parse arrays
     * into an array, but rather creates an object indexed with a Number.
     *
     * This function recursively fixes this, with one caveat: the
     * object needs have an index 0 to be considered as an array
     * @param obj
     */
    function normalizeArrays(obj) {
        if (Object.hasOwn(obj, '0')) {
            return Array.from(Object.keys(obj))
                .map(a => parseInt(a, 10))
                .sort((a, b) => a - b)
                .map(a => {
                    const value = obj[a];
                    return isObject(value) ? normalizeArrays(value) : value;
                })
        } else {
            return Object.fromEntries(Object.entries(obj).map(([key, value]) => {
                const val = isObject(value) ? normalizeArrays(value) : value;
                return [key, val];
            }));
        }
    }

    exports.normalizeArrays = normalizeArrays;
})(globalThis);