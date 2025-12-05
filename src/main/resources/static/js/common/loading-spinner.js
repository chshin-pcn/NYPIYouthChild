export function showLoadingSpinner() {
    const loadingSpinner = document.querySelector('.loading');
    if (loadingSpinner) {
        loadingSpinner.style.display = 'flex';
    }
}

export function hideLoadingSpinner() {
    const loadingSpinner = document.querySelector('.loading');
    if (loadingSpinner) {
        loadingSpinner.style.display = 'none';
    }
}
