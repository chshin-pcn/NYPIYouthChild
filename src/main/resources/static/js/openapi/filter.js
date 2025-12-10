/**
 * 필터 선택 상자를 저장하는 객체입니다. 각 선택 상자는 고유한 키로 식별됩니다.
 * @type {Object.<string, HTMLSelectElement>}
 */
const selects = {};
/**
 * 필터 선택 상자의 순서와 관련 데이터를 저장하는 배열입니다.
 * 각 요소는 { key: string, data: Array, paramName: string } 형태입니다.
 * @type {Array<Object>}
 */
const selectOrder = [];

/**
 * 처리된 필터 데이터를 저장하는 전역 변수입니다.
 * @type {Object|null}
 */
let processedData = null;

/**
 * 지정된 URL에서 필터 데이터를 가져오는 비동기 함수입니다.
 * @param {string} url - 필터 데이터를 가져올 API 엔드포인트 URL.
 * @returns {Promise<Object>} - 필터 데이터를 포함하는 JSON 객체를 반환하는 Promise.
 * @throws {Error} - 데이터 조회 실패 시 오류를 발생시킵니다.
 */
export async function fetchFilter(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error("필터 데이터 조회에 실패했습니다.\n잠시 후 다시 시도해주세요.");
    }
    return response.json();
}

/**
 * 여러 개의 선택 상자를 초기화하고 비활성화합니다.
 * 첫 번째 옵션("선택" 등)을 제외한 모든 옵션을 제거하고 선택 상자를 비활성화합니다.
 * @param {...HTMLSelectElement} selectElements - 초기화할 HTMLSelectElement 객체들.
 */
function resetAndDisable(...selectElements) {
    selectElements.forEach((select) => {
        if (select) {
            select.options.length = 1; // 첫 번째 옵션만 유지 (예: "선택")
            select.disabled = true; // 선택 상자 비활성화
            select.value = ""; // 선택 값 초기화
        }
    });
}

/**
 * 주어진 데이터 항목들로 선택 상자를 채웁니다.
 * 데이터가 없거나 유효하지 않으면 선택 상자를 비활성화합니다.
 * @param {HTMLSelectElement} select - 옵션을 채울 HTMLSelectElement 객체.
 * @param {Array<Object>} items - 선택 상자에 추가할 { id: string, text: string, value?: string } 형태의 항목 배열.
 */
function populateSelect(select, items) {
    if (!select || !items || items.length === 0) {
        if (select) select.disabled = true; // 선택 상자 비활성화
        return;
    }
    items.forEach((item) => {
        const opt = document.createElement("option");
        opt.value = item.value ? item.value : item.text; // 옵션의 value 속성
        opt.textContent = item.text; // 옵션에 표시될 텍스트
        opt.dataset.id = item.id;
        select.appendChild(opt); // 옵션을 선택 상자에 추가
    });
    select.disabled = false; // 선택 상자 활성화
}

/**
 * 주어진 부모 ID를 가진 자식 항목들을 데이터 배열에서 필터링하여 반환합니다.
 * @param {string|null} parentId - 부모 항목의 ID. null인 경우 최상위 항목을 찾습니다.
 * @param {Array<Object>} data - 검색할 데이터 항목들의 배열. 각 항목은 `parentId` 속성을 가집니다.
 * @returns {Array<Object>} - 부모 ID와 일치하는 자식 항목들의 배열.
 */
function getChildren(parentId, data) {
    return data.filter((item) => item.parentId === parentId);
}

/**
 * 구성된 모든 필터 선택 상자에 변경 이벤트 리스너를 추가합니다.
 * 선택 상자의 값이 변경되면 다음 종속 선택 상자를 업데이트합니다.
 * 다음 선택 상자에 하나의 옵션만 있을 경우 자동으로 선택하고 비활성화합니다.
 * @returns {void}
 * @export
 */
export function addFilterEventListeners() {
    selectOrder.forEach(({ key }, index) => {
        const currentSelect = selects[key];
        if (!currentSelect) return;

        currentSelect.addEventListener("change", () => {
            const selectedId = currentSelect.options[currentSelect.selectedIndex].dataset.id;

            // 현재 선택 상자 이후의 모든 선택 상자를 초기화하고 비활성화합니다.
            const selectsToReset = selectOrder.slice(index + 1).map((item) => selects[item.key]);
            resetAndDisable(...selectsToReset);

            // 자동 선택 로직 시작
            let currentParentId = selectedId;
            let currentIndex = index;

            while (currentParentId && selectOrder[currentIndex + 1]) {
                const nextSelectInfo = selectOrder[currentIndex + 1];
                const nextSelect = selects[nextSelectInfo.key];
                const children = getChildren(currentParentId, nextSelectInfo.data);

                if (children.length === 1) {
                    // 옵션이 하나뿐인 경우, 자동으로 선택하고 드롭다운을 비활성화한 다음 다음으로 이동합니다.
                    populateSelect(nextSelect, children);
                    nextSelect.selectedIndex = 1; // 자동 선택
                    if (children[0].text === "없음") nextSelect.disabled = true;

                    // 다음 반복을 위한 준비
                    currentParentId = children[0].id;
                    currentIndex++;
                } else {
                    // 옵션이 여러 개이거나 없는 경우, 정상적으로 채우고 중지합니다.
                    populateSelect(nextSelect, children);
                    break;
                }
            }
        });
    });
}

/**
 * 리셋 버튼에 이벤트 리스너를 추가합니다.
 * 리셋 버튼 클릭 시 모든 필터 선택 상자를 초기화하고 첫 번째 선택 상자를 다시 채웁니다.
 * @returns {void}
 * @export
 */
export function addResetButtonEventListener() {
    const btnReset = document.querySelector(".btn-reset"); // 리셋 버튼 요소 가져오기
    if (!btnReset) return;
    
    btnReset.addEventListener("click", () => {
        const allSelects = selectOrder.map((item) => selects[item.key]);
        resetAndDisable(...allSelects); // 모든 선택 상자 초기화 및 비활성화
        // 첫 번째 선택 상자를 다시 채웁니다.
        if (selectOrder.length > 0) {
            const firstSelect = selectOrder[0];
            populateSelect(selects[firstSelect.key], getChildren(null, firstSelect.data));
        }
    });
}

/**
 * 검색 버튼에 이벤트 리스너를 추가합니다.
 * 검색 버튼 클릭 시 선택된 필터 값들을 수집하여 검색을 수행합니다.
 * @param {Function} performSearch - 검색을 실제로 수행하는 함수. (searchParams, currentPage, numOfRows, tableConfig, url) 인자를 받습니다.
 * @param {Object} pageFilterConfig - 페이지 필터 설정 객체.
 * @param {Object} tableConfig - 테이블 설정 객체.
 * @param {Object} url - URL 설정 객체.
 * @returns {void}
 * @export
 */
export function addSearchButtonEventListener(performSearch, pageFilterConfig, tableConfig, url) {
    const btnSearch = document.querySelector(".btn-search"); // 검색 버튼 요소 가져오기
    if (!btnSearch) return;

    btnSearch.addEventListener("click", () => {
        const searchParams = {}; // 검색 파라미터를 저장할 객체

        // 선택 드롭다운에서 값 수집
        selectOrder.forEach(selectInfo => {
            // paramName이 정의되지 않은 경우 건너뜀
            if (!selectInfo.paramName) return;

            const select = selects[selectInfo.key];
            if (!select || !select.value) return;

            const valueToUse = select.value;
            if (valueToUse === "" || valueToUse === "없음") return;

            // 'year' 키에 대한 특별 처리 로직
            if (selectInfo.key === "year") {
                let parts = valueToUse.split('-');
                if (parts[1] === "Y") searchParams["aiCrtYn"] = "Y"; // 'aiCrtYn' 파라미터 설정
                if (valueToUse.includes(' / ')) {
                    parts = parts[0].split(' / ');
                    searchParams["ornuNm"] = parts[0]; // 'ornuNm' 파라미터 설정
                    searchParams["srvyYr"] = parts[1]; // 'srvyYr' 파라미터 설정
                } else {
                    searchParams["srvyYr"] = parts[0]; // 'srvyYr' 파라미터 설정
                }
            } else {
                searchParams[selectInfo.paramName] = valueToUse; // 일반 파라미터 설정
            }
        });

        // 배너 변수 체크박스에서 값 수집
        if (pageFilterConfig.bannerVariables) {
            pageFilterConfig.bannerVariables.forEach(bannerConfig => {
                const checkbox = document.getElementById(bannerConfig.elementId);
                if (checkbox && checkbox.checked) {
                    searchParams[bannerConfig.paramName] = bannerConfig.paramValue; // 배너 변수 파라미터 설정
                }
            });
        }

        // 페이지네이션 값 가져오기
        const numOfRows = document.getElementById('page-size-select').value;
        
        // 검색 수행 함수 호출
        performSearch(searchParams, 1, numOfRows, tableConfig, url);
    });
}

/**
 * 필터를 초기화하고 설정합니다.
 * API에서 필터 데이터를 가져오고, 구성에 따라 선택 상자(selects)와 선택 순서(selectOrder)를 동적으로 빌드합니다.
 * @param {Object} config - 필터 구성 객체.
 * @param {Object} url - URL 설정 객체. `url.filter` 속성을 포함해야 합니다.
 * @returns {Promise<void>}
 * @export
 */
export async function initializeFilters(config, url) {
    try {
        // 필터 데이터를 비동기적으로 가져와 모듈 수준 변수에 할당합니다.
        processedData = await fetchFilter(url.filter);
    } catch (error) {
        console.error("필터 데이터 조회 중 오류 발생:", error);
    }

    // 제공된 구성 및 사용 가능한 데이터에 따라 선택 상자와 selectOrder를 동적으로 빌드합니다.
    config.selects.forEach(selectConfig => {
        const element = document.getElementById(selectConfig.elementId); // DOM 요소 가져오기
        const data = processedData[selectConfig.data]; // 관련 데이터 가져오기

        // DOM 요소와 데이터가 모두 존재하는 경우에만 필터를 추가합니다.
        if (element && data) {
            selects[selectConfig.key] = element; // selects 객체에 요소 저장
            selectOrder.push({
                key: selectConfig.key,
                data: data,
                paramName: selectConfig.paramName // paramName도 함께 전달
            });
        }
    });

    // 순서의 첫 번째 선택 상자를 초기 채웁니다.
    if (selectOrder.length > 0) {
        const firstSelect = selectOrder[0];
        populateSelect(selects[firstSelect.key], getChildren(null, firstSelect.data));
    }
}
