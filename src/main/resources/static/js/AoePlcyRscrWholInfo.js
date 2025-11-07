/**
 * 페이지 로드 시 필터 기능을 초기화하고 필요한 이벤트 리스너를 설정합니다.
 */
function initializeFilters(data) {
    if (!data || !Array.isArray(data)) {
        console.error("데이터를 불러오지 못했습니다.");
        return;
    }

    const filterElementIds = ['survey', 'order', 'year1', 'year2', 'respondent'];
    let selectedParams = {};

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
        filterElementIds.forEach(id => {
            const select = document.getElementById(id);
            if (select) {
                select.addEventListener('change', updateDependentFilters);
            }
        });
    }

    /**
     * 모든 필터(select)에서 change 이벤트 리스너를 제거합니다.
     */
    function removeFilterEventListeners() {
        filterElementIds.forEach(id => {
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
                filterElementIds.forEach(id => {
                    const select = document.getElementById(id);
                    if (select) {
                        select.value = "";
                    }
                });
                updateDependentFilters();
            });
        }        
    }

    function addPageSizeChangeEventListener() {
        const pageSizeSelect = document.getElementById('page-size-select');
        if (!pageSizeSelect) return;

        pageSizeSelect.addEventListener('change', () => {
            if (Object.keys(selectedParams).length > 0) {
                const numOfRows = pageSizeSelect.value
                performSearch(selectedParams, 1, numOfRows);
            }
        });
    }

    function addSearchButtonEventListener() {
        const searchButton = document.querySelector('.btn-search');
        if (!searchButton) return;

        searchButton.addEventListener('click', () => {
            const keyword = document.getElementById('keyword').value;
            const surveySelect = document.getElementById('survey');
            const selectedSurveyId = surveySelect.options[surveySelect.selectedIndex].dataset.id || '';
            const order = document.getElementById('order').value;
            const year1 = document.getElementById('year1').value;
            const year2 = document.getElementById('year2').value;
            const respondent = document.getElementById('respondent').value;
            const numOfRows = document.getElementById('page-size-select').value;

            const searchParams = {};

            searchParams.searchKeyword = keyword || "";
            if (selectedSurveyId) searchParams.otptDataCd = selectedSurveyId;
            if (year1) searchParams.srvyYr01 = year1;
            if (year2) searchParams.srvyYr02 = year2;
            if (respondent) searchParams.rspnsMnbdNm = respondent;
            if (order && order !== "없음") searchParams.ornuNm = order;

            performSearch(searchParams, 1, numOfRows);
        });
    }

    function performSearch(searchParams, pageNo = 1, numOfRows = 10) {
        const queryString = new URLSearchParams({
            ...searchParams,
            pageNo,
            numOfRows
        }).toString();
        const url = `/api/aoePlcyRscrWholInfo?${queryString}`;
            
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error("데이터 조회에 실패했습니다.\n잠시 후 다시 시도해주세요.");
                }
                return response.json();
            })
            .then(data => {
                console.log("검색 결과 데이터:", data);
                selectedParams = {...searchParams};
                updateTable(data.items);
                updateTotalCount(data.totalCount);
                displayPagination(data.totalCount, data.numOfRows, data.pageNo);
            })
            .catch(error => {
                console.error("검색 중 오류 발생:", error);
                updateTable(null);
                updateTotalCount(0);
            });
    }

    function updateTable(items) {
        const tableBody = document.querySelector("#results-table tbody");
        if (!tableBody) return;

        // Clear existing rows
        while (tableBody.firstChild) {
            tableBody.removeChild(tableBody.firstChild);
        }

        if (!items || items.length === 0) {
            const noResultsRow = document.createElement('tr');
            const noResultsCell = document.createElement('td');
            noResultsCell.colSpan = 14; // Adjusted colspan to 14 based on HTML header
            noResultsCell.style.textAlign = 'center';
            noResultsCell.textContent = '검색 결과가 없습니다.';
            noResultsRow.appendChild(noResultsCell);
            tableBody.appendChild(noResultsRow);
            return;
        }

        items.forEach(item => {
            const row = document.createElement('tr');

            const createCell = (text) => {
                const cell = document.createElement('td');
                cell.textContent = text || '';
                return cell;
            };

            row.appendChild(createCell(item.opnSn));
            row.appendChild(createCell(item.opnDataCd));
            row.appendChild(createCell(item.otptDataSeNm));
            row.appendChild(createCell(item.cohortNm));
            row.appendChild(createCell(item.ornuNm));
            row.appendChild(createCell(item.srvyYr));
            row.appendChild(createCell(item.srvyExmnCycl));
            row.appendChild(createCell(item.rspnsMnbdNm));
            row.appendChild(createCell(item.otptCtgryNm));
            row.appendChild(createCell(item.srvyQitemId));
            row.appendChild(createCell(item.cbookQitemCn));

            const downloadCell = document.createElement('td');
            if (item.cbookAtchFileNm) {
                const codebookLink = document.createElement('a');
                codebookLink.href = item.cbookAtchFileNm;
                codebookLink.textContent = '코드북';
                codebookLink.classList.add('btn-download');
                downloadCell.appendChild(codebookLink);
            }
            if (item.rspvlAtchFileNm) {
                const responseValueLink = document.createElement('a');
                responseValueLink.href = item.rspvlAtchFileNm;
                responseValueLink.textContent = '응답값';
                responseValueLink.classList.add('btn-download');
                downloadCell.appendChild(responseValueLink);
            }
            row.appendChild(downloadCell);

            tableBody.appendChild(row);
        });
    }

    function updateTotalCount(totalCount) {
        const countElement = document.getElementById('total-count');
        if (countElement) {
            countElement.textContent = totalCount || 0;
        }
    }

    function displayPagination(totalCount, numOfRows, pageNo) {
        const totalPages = Math.ceil(totalCount / numOfRows);
        const paginationContainer = document.querySelector(".pagination");
        paginationContainer.textContent = "";

        if (totalPages > 0) {
            const startPage = Math.floor((pageNo - 1) / 10) * 10 + 1;
            const endPage = Math.min(startPage + 9, totalPages);

            const createButton = (text, classNames = [], disabled = false, dataPage = null) => {
                const button = document.createElement("button");
                button.classList.add("page-btn", ...classNames);
                if (disabled) button.disabled = true;

                const anchor = document.createElement("a");
                anchor.textContent = text;
                button.appendChild(anchor);

                if (dataPage) button.dataset.page = dataPage;

                return button;
            };

            // 처음 버튼
            const firstButton = createButton("처음", ["arrow", "first"], pageNo === 1);
            paginationContainer.appendChild(firstButton);

            // 이전 버튼
            const prevButton = createButton("이전", ["arrow", "prev"], pageNo === 1);
            paginationContainer.appendChild(prevButton);

            // 페이지 번호 버튼
            for (let i = startPage; i <= endPage; i++) {
                const pageButton = createButton(i, [], false, i);
                if (i === pageNo) pageButton.classList.add("active");
                paginationContainer.appendChild(pageButton);
            }

            // 다음 버튼
            const nextButton = createButton("다음", ["arrow", "next"], pageNo === totalPages);
            paginationContainer.appendChild(nextButton);

            // 마지막 버튼
            const lastButton = createButton("마지막", ["arrow", "last"], pageNo === totalPages);
            paginationContainer.appendChild(lastButton);

            // 페이지 버튼 클릭 이벤트
            const buttons = paginationContainer.querySelectorAll(".page-btn");
            buttons.forEach(button => {
                button.addEventListener("click", () => {
                    let clickedPage = parseInt(button.dataset.page, 10);
                    if (!isNaN(clickedPage)) {
                        pageNo = clickedPage;
                    } else if (button.classList.contains("first")) {
                        pageNo = 1;
                    } else if (button.classList.contains("prev")) {
                        pageNo = Math.max(1, pageNo - 10);
                    } else if (button.classList.contains("next")) {
                        pageNo = Math.min(totalPages, pageNo + 10);
                    } else if (button.classList.contains("last")) {
                        pageNo = totalPages;
                    }
                    numOfRows = document.getElementById('page-size-select').value;
                    performSearch(selectedParams, pageNo, numOfRows);
                });
            });

            paginationContainer.style.display = "block";
        }
    }

    addFilterEventListeners();
    addResetButtonEventListener();
    addSearchButtonEventListener();
    addPageSizeChangeEventListener();
    updateDependentFilters();
}
