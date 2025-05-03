// Smooth scroll behavior
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener("click", function (e) {
        e.preventDefault();
        document.querySelector(this.getAttribute("href")).scrollIntoView({  // ✅ fixed typo: SrollIntoView -> scrollIntoView
            behavior: "smooth"
        });
    });
});

// Scroll to top
document.getElementById("top").addEventListener("click", () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
});


