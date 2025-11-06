/**
 * Initializes a set of cascading filter dropdowns and checkboxes based on a configuration object.
 *
 * @param {object} config - The configuration object for the filters.
 * @param {Array<object>} config.selects - Configuration for the dropdowns.
 * @param {string} config.selects[].key - The key used to identify the select.
 * @param {string} config.selects[].elementId - The DOM ID of the select element.
 * @param {string} config.selects[].data - The key used to identify data.
 * @param {Array<object>} [config.bannerVariables] - Configuration for banner variable checkboxes.
 * @param {string} config.bannerVariables[].elementId - The DOM ID of the checkbox element.
 * @param {string} config.bannerVariables[].paramName - The parameter name to use in the search request.
 * @param {string} config.bannerVariables[].paramValue - The parameter value to use if the checkbox is checked.
 * @param {object} processedData - An object where keys match the 'key' from the config and values are the data arrays for populating the dropdowns.
 * @param {function} searchCallback - A function to call with the selected filter values when the search button is clicked.
 */
function initializeFilters(config, processedData, searchCallback) {
    if (!config || !processedData) {
        console.error("Configuration or processed data is null or undefined.");
        return;
    }

    const selects = {};
    const selectOrder = [];

    // Dynamically build selects and selectOrder based on the provided config and available data
    config.selects.forEach(selectConfig => {
        const element = document.getElementById(selectConfig.elementId);
        const data = processedData[selectConfig.data];

        // Only add the filter if its DOM element and data exist
        if (element && data) {
            selects[selectConfig.key] = element;
            selectOrder.push({
                key: selectConfig.key,
                data: data,
                paramName: selectConfig.paramName // Pass along the paramName
            });
        }
    });

    function resetAndDisable(...selectElements) {
        selectElements.forEach((select) => {
            if (select) {
                select.options.length = 1; // Keep only the first option (e.g., "선택")
                select.disabled = true;
                select.value = "";
            }
        });
    }

    function populateSelect(select, items) {
        if (!select || !items || items.length === 0) {
            if (select) select.disabled = true;
            return;
        }
        items.forEach((item) => {
            const opt = document.createElement("option");
            opt.value = item.id;
            opt.textContent = item.name;
            // Use a consistent property for the request value, e.g., 'value'
            if (item.value) opt.dataset.value = item.value;
            select.appendChild(opt);
        });
        select.disabled = false;
    }

    function getChildren(parentId, data) {
        // The root items have a null parentId
        return data.filter((item) => item.parentId === parentId);
    }

    // Add event listeners to all configured select boxes
    selectOrder.forEach(({ key }, index) => {
        const currentSelect = selects[key];

        if (currentSelect) {
            currentSelect.addEventListener("change", () => {
                const selectedId = currentSelect.value;

                // Disable all subsequent selects
                const selectsToReset = selectOrder
                    .slice(index + 1)
                    .map((item) => selects[item.key]);
                resetAndDisable(...selectsToReset);

                // Populate the next select in the order
                const nextSelectInfo = selectOrder[index + 1];
                if (selectedId && nextSelectInfo) {
                    const children = getChildren(selectedId, nextSelectInfo.data);
                    populateSelect(selects[nextSelectInfo.key], children);
                }
            });
        }
    });

    // Initial population of the first select box in the order
    if (selectOrder.length > 0) {
        const firstSelect = selectOrder[0];
        populateSelect(selects[firstSelect.key], getChildren(null, firstSelect.data));
    }


    // Add event listener to reset button
    const btnReset = document.querySelector(".btn-reset");
    if (btnReset) {
        btnReset.addEventListener("click", () => {
            const allSelects = selectOrder.map((item) => selects[item.key]);
            resetAndDisable(...allSelects);
            // Re-populate the first select
            if (selectOrder.length > 0) {
                const firstSelect = selectOrder[0];
                populateSelect(selects[firstSelect.key], getChildren(null, firstSelect.data));
            }
        });
    }

    // Add event listener to search button
    const btnSearch = document.querySelector(".btn-search");
    if (btnSearch) {
        btnSearch.addEventListener("click", () => {
            const selectedValues = {};

            // Collect values from select dropdowns
            selectOrder.forEach(selectInfo => {
                // If a paramName is defined, use it to collect the value
                if (!selectInfo.paramName) return;

                const select = selects[selectInfo.key];
                if (!select || !select.value) return;

                const selectedOption = select.options[select.selectedIndex];

                const valueToUse = selectedOption.dataset.value !== undefined ? selectedOption.dataset.value : select.value;
                selectedValues[selectInfo.paramName] = valueToUse;
            });

            // Collect values from banner variable checkboxes
            if (config.bannerVariables) {
                config.bannerVariables.forEach(bannerConfig => {
                    const checkbox = document.getElementById(bannerConfig.elementId);
                    if (checkbox && checkbox.checked) {
                        selectedValues[bannerConfig.paramName] = bannerConfig.paramValue;
                    }
                });
            }

            if (searchCallback) {
                searchCallback(selectedValues);
            }
        });
    }
}
