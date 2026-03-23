/* 
 * File: animations.js
 * Description: JavaScript animations cho UI
 */

// Fade in effect cho các element
function fadeIn(element, duration = 300) {
    if (!element) return;
    
    element.style.opacity = '0';
    element.style.display = 'block';
    
    let start = null;
    function animate(timestamp) {
        if (!start) start = timestamp;
        const progress = timestamp - start;
        const opacity = Math.min(progress / duration, 1);
        element.style.opacity = opacity;
        
        if (progress < duration) {
            requestAnimationFrame(animate);
        }
    }
    requestAnimationFrame(animate);
}

// Fade out effect cho các element
function fadeOut(element, duration = 300) {
    if (!element) return;
    
    let start = null;
    function animate(timestamp) {
        if (!start) start = timestamp;
        const progress = timestamp - start;
        const opacity = Math.max(1 - (progress / duration), 0);
        element.style.opacity = opacity;
        
        if (progress < duration) {
            requestAnimationFrame(animate);
        } else {
            element.style.display = 'none';
        }
    }
    requestAnimationFrame(animate);
}

// Slide in effect
function slideIn(element, duration = 300) {
    if (!element) return;
    
    element.style.transform = 'translateY(-10px)';
    element.style.opacity = '0';
    element.style.display = 'block';
    
    let start = null;
    function animate(timestamp) {
        if (!start) start = timestamp;
        const progress = timestamp - start;
        const ratio = Math.min(progress / duration, 1);
        element.style.transform = `translateY(${-10 * (1 - ratio)}px)`;
        element.style.opacity = ratio;
        
        if (progress < duration) {
            requestAnimationFrame(animate);
        }
    }
    requestAnimationFrame(animate);
}

// Auto fade in cho các element có class fade-in khi load trang
document.addEventListener('DOMContentLoaded', function() {
    const fadeElements = document.querySelectorAll('.fade-in');
    fadeElements.forEach(function(el) {
        fadeIn(el);
    });
    
    const slideElements = document.querySelectorAll('.slide-in');
    slideElements.forEach(function(el) {
        slideIn(el);
    });
});
