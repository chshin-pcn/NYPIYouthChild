/**
 * 페이지 로드 시 필터 기능을 초기화하고 필요한 이벤트 리스너를 설정합니다.
 */
function initializeFilters(data) {
    if (!data || !Array.isArray(data)) {
        console.warn("Data is not an array or is null/undefined. Cannot initialize filters.");
        return;
    }

    const filterIds = ['survey', 'order', 'year1', 'year2', 'respondent'];

    /**
     * 데이터 배열에서 특정 키에 해당하는 고유한 값들을 추출하고 정렬하여 반환합니다.
     */
    function getUniqueValues(dataArray, key) {
        const uniqueSet = new Set();
        if (dataArray) {
            dataArray.forEach(item => {
                if (item && item[key]) {
                    uniqueSet.add(item[key]);
                }
            });
        }
        return Array.from(uniqueSet).sort((a, b) => a.localeCompare(b, undefined, { numeric: true }));
    }

    /**
     * 데이터 배열에서 고유한 조사(이름과 ID) 목록을 추출하여 반환합니다.
     */
    function getUniqueSurveys(dataArray) {
        const uniqueMap = new Map();
        if (dataArray) {
            dataArray.forEach(item => {
                if (item && item.otptDataSeNm && !uniqueMap.has(item.otptDataSeNm)) {
                    uniqueMap.set(item.otptDataSeNm, {
                        id: item.opnDataCd,
                        name: item.otptDataSeNm
                    });
                }
            });
        }
        return Array.from(uniqueMap.values()).sort((a, b) => a.name.localeCompare(b.name));
    }

    // 페이지 로드 시 각 필터에 대한 모든 가능한 옵션을 미리 계산합니다.
    const allPossibleOptions = {
        survey: getUniqueSurveys(data),
        order: getUniqueValues(data, 'ornuNm'),
        respondent: getUniqueValues(data, 'rspnsMnbdNm'),
        year: getUniqueValues(data, 'srvyYr')
    };

    /**
     * select 요소를 새로운 옵션으로 채웁니다. 전체 옵션 중 유효한 옵션은 활성화, 나머지는 비활성화 처리합니다.
     */
    function populateSelectWithOptions(selectElementId, allValues, validValuesSet) {
        const select = document.getElementById(selectElementId);
        if (!select) return;

        const currentValue = select.value;
        while (select.options.length > 1) {
            select.remove(1);
        }

        allValues.forEach(valueOrObject => {
            const option = document.createElement('option');
            let value, text, isValid;

            if (selectElementId === 'survey') {
                value = valueOrObject.name;
                text = valueOrObject.name;
                option.dataset.id = valueOrObject.id; // data-id 속성 추가
                isValid = validValuesSet.has(valueOrObject.name);
            } else {
                value = valueOrObject;
                text = valueOrObject;
                isValid = validValuesSet.has(valueOrObject);
            }

            option.value = value;
            option.textContent = text;
            if (!isValid) {
                option.disabled = true;
            }
            select.appendChild(option);
        });

        if (currentValue && !validValuesSet.has(currentValue)) {
            select.value = "";
        } else {
            select.value = currentValue;
        }
    }
    
    /**
     * 필터의 선택값이 변경될 때마다 호출되어, 모든 필터의 옵션을 연쇄적으로 업데이트합니다.
    */
    function updateDependentFilters() {
        removeFilterEventListeners();

        const selections = {
            survey: document.getElementById('survey').value,
            order: document.getElementById('order').value,
            year1: document.getElementById('year1').value,
            year2: document.getElementById('year2').value,
            respondent: document.getElementById('respondent').value
        };

        const keyMap = {
            survey: 'otptDataSeNm',
            order: 'ornuNm',
            respondent: 'rspnsMnbdNm'
        };

        ['survey', 'order', 'respondent'].forEach(filterIdToUpdate => {
            let filteredData = [...data];
            for (const selectionKey in selections) {
                if (selectionKey === filterIdToUpdate || !selections[selectionKey] || selectionKey === 'year1' || selectionKey === 'year2') {
                    continue;
                }
                const dataKey = keyMap[selectionKey];
                filteredData = filteredData.filter(item => item[dataKey] === selections[selectionKey]);
            }

            const startYear = selections.year1 ? parseInt(selections.year1, 10) : -Infinity;
            const endYear = selections.year2 ? parseInt(selections.year2, 10) : Infinity;
            if (startYear !== -Infinity || endYear !== Infinity) {
                filteredData = filteredData.filter(item => {
                    const itemYear = parseInt(item.srvyYr, 10);
                    return itemYear >= startYear && itemYear <= endYear;
                });
            }

            const validOptions = new Set(getUniqueValues(filteredData, keyMap[filterIdToUpdate]));
            populateSelectWithOptions(filterIdToUpdate, allPossibleOptions[filterIdToUpdate], validOptions);
        });

        let baseYearFilteredData = [...data];
        if (selections.survey) baseYearFilteredData = baseYearFilteredData.filter(item => item.otptDataSeNm === selections.survey);
        if (selections.order) baseYearFilteredData = baseYearFilteredData.filter(item => item.ornuNm === selections.order);
        if (selections.respondent) baseYearFilteredData = baseYearFilteredData.filter(item => item.rspnsMnbdNm === selections.respondent);

        const baseValidYears = getUniqueValues(baseYearFilteredData, 'srvyYr');

        const selectedYear2 = selections.year2 ? parseInt(selections.year2, 10) : Infinity;
        const validOptionsForYear1 = baseValidYears.filter(year => parseInt(year, 10) <= selectedYear2);
        populateSelectWithOptions('year1', allPossibleOptions.year, new Set(validOptionsForYear1));

        const selectedYear1 = selections.year1 ? parseInt(selections.year1, 10) : -Infinity;
        const validOptionsForYear2 = baseValidYears.filter(year => parseInt(year, 10) >= selectedYear1);
        populateSelectWithOptions('year2', allPossibleOptions.year, new Set(validOptionsForYear2));

        for (const id in selections) {
            document.getElementById(id).value = selections[id];
        }

        addFilterEventListeners();
    }

    /**
     * 모든 필터(select)에 change 이벤트 리스너를 추가합니다.
     */
    function addFilterEventListeners() {
        filterIds.forEach(id => {
            const select = document.getElementById(id);
            if (select) {
                select.addEventListener('change', updateDependentFilters);
            }
        });
    }

    /**
     * 모든 필터(select)에서 change 이벤트 리스너를 제거합니다. (옵션 업데이트 시 무한 루프 방지용)
     */
    function removeFilterEventListeners() {
        filterIds.forEach(id => {
            const select = document.getElementById(id);
            if (select) {
                select.removeEventListener('change', updateDependentFilters);
            }
        });
    }
    
    function addResetButtonEventListener() {
        const resetButton = document.querySelector('.btn-reset');
        if (resetButton) {
            resetButton.addEventListener('click', () => {
                filterIds.forEach(id => {
                    const select = document.getElementById(id);
                    if (select) {
                        select.value = "";
                    }
                });
                updateDependentFilters();
            });
        }        
    }

    function addSearchButtonEventListener() {
        const searchButton = document.querySelector('.btn-search');
        if (!searchButton) return;

        searchButton.addEventListener('click', () => {
            const keyword = document.getElementById('keyword').value;
            const surveySelect = document.getElementById('survey');
            const selectedSurveyName = surveySelect.value;
            const selectedSurveyId = surveySelect.options[surveySelect.selectedIndex].dataset.id || '';

            const order = document.getElementById('order').value;
            const year1 = document.getElementById('year1').value;
            const year2 = document.getElementById('year2').value;
            const respondent = document.getElementById('respondent').value;

            const searchParams = {
                searchKeyword: keyword,
                otptDataCd: selectedSurveyId,
                srvyYr01: year1,
                srvyYr02: year2,
                rspnsMnbdNm: respondent
            };

            if (order && order != "없음") {
                searchParams["ornuNm"] = order
            }

            console.log(searchParams);
        });
    }

    addFilterEventListeners();
    addResetButtonEventListener();
    addSearchButtonEventListener();
    updateDependentFilters();
}