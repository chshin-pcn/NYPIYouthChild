function handleResize() {
    const sideNav = document.querySelector('.side-nav-wrap');
    const dim = document.querySelector('.dim');

    if (window.innerWidth > 860) {
        if (sideNav) sideNav.style.left = '0px';
        if (dim) dim.style.display = 'none';
    }
}

window.addEventListener('resize', handleResize);
handleResize(); // Initial call

const btnMenu = document.querySelector('.btn-menu');
if (btnMenu) {
    btnMenu.addEventListener('click', () => {
        const dim = document.querySelector('.dim');
        const sideNav = document.querySelector('.side-nav-wrap');

        if (dim) dim.style.display = 'block';
        if (sideNav) {
            const animation = sideNav.animate(
                [
                    { left: sideNav.style.left || '-360px' },
                    { left: '0px' }
                ],
                {
                    duration: 300
                }
            );
            // Set the final style when the animation is done
            animation.onfinish = () => {
                sideNav.style.left = '0px';
            }
        }
    });
}

const btnMenuClose = document.querySelector('.btn-menu-close');
if (btnMenuClose) {
    btnMenuClose.addEventListener('click', () => {
        const dim = document.querySelector('.dim');
        const sideNav = document.querySelector('.side-nav-wrap');

        if (dim) dim.style.display = 'none';
        if (sideNav) {
            const animation = sideNav.animate(
                [
                    { left: sideNav.style.left || '0px' },
                    { left: '-360px' }
                ],
                {
                    duration: 300
                }
            );
            // Set the final style when the animation is done
            animation.onfinish = () => {
                sideNav.style.left = '-360px';
            }
        }
    });
}
