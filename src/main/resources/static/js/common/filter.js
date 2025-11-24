function initializeFilters(config, processedData, performSearch) {
    if (!config || !processedData) {
        console.error("데이터를 불러오지 못했습니다.");
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
    function addFilterEventListeners() {
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

                    // Start of new logic for auto-select
                    let currentParentId = selectedId;
                    let currentIndex = index;

                    while (currentParentId && selectOrder[currentIndex + 1]) {
                        const nextSelectInfo = selectOrder[currentIndex + 1];
                        const nextSelect = selects[nextSelectInfo.key];
                        const children = getChildren(currentParentId, nextSelectInfo.data);

                        if (children.length === 1) {
                            // If there's only one option, auto-select it, disable the dropdown, and move to the next.
                            populateSelect(nextSelect, children);
                            nextSelect.value = children[0].id;
                            if (children[0].name === "없음") nextSelect.disabled = true;

                            // Prepare for the next iteration
                            currentParentId = children[0].id;
                            currentIndex++;
                        } else {
                            // If there are multiple options or no options, populate normally and stop.
                            populateSelect(nextSelect, children);
                            break;
                        };
                    }
                });
            }
        });
    }

    // Add event listener to reset button
    function addResetButtonEventListener() {
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
    }

    // Add event listener to search button
    function addSearchButtonEventListener() {
        const btnSearch = document.querySelector(".btn-search");
        if (btnSearch) {
            btnSearch.addEventListener("click", () => {
                const searchParams = {};

                // Collect values from select dropdowns
                selectOrder.forEach(selectInfo => {
                    // If a paramName is defined, use it to collect the value
                    if (!selectInfo.paramName) return;

                    const select = selects[selectInfo.key];
                    if (!select || !select.value) return;

                    const selectedOption = select.options[select.selectedIndex];

                    const valueToUse = !!selectedOption.dataset.value ? selectedOption.dataset.value : select.value;

                    if (selectInfo.key === "year") {
                        let parts = valueToUse.split('-');
                        searchParams["aiCrtYn"] = parts[1];
                        if (valueToUse.includes(' / ')) {
                            parts = parts[0].split(' / ');
                            searchParams["ornuNm"] = parts[0];
                            searchParams["srvyYr"] = parts[1];
                        } else {
                            searchParams["srvyYr"] = parts[0];
                        }
                    } else {
                        searchParams[selectInfo.paramName] = valueToUse;
                    }
                });

                // Collect values from banner variable checkboxes
                if (config.bannerVariables) {
                    config.bannerVariables.forEach(bannerConfig => {
                        const checkbox = document.getElementById(bannerConfig.elementId);
                        if (checkbox && checkbox.checked) {
                            searchParams[bannerConfig.paramName] = bannerConfig.paramValue;
                        }
                    });
                }

                // Get pagination values
                const numOfRows = document.getElementById('page-size-select').value;
                
                performSearch(searchParams, 1, numOfRows);
            });
        }
    }

    // Initial population of the first select box in the order
    if (selectOrder.length > 0) {
        const firstSelect = selectOrder[0];
        populateSelect(selects[firstSelect.key], getChildren(null, firstSelect.data));
    }

    addFilterEventListeners();
    addResetButtonEventListener();
    addSearchButtonEventListener();
}
