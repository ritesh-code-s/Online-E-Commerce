
document.addEventListener("DOMContentLoaded", function () {
    // Get HTML elements
    const searchInput = document.getElementById("product-search");
    const clearSearch = document.getElementById("clear-search");
    const productList = document.getElementById("product-list");
    const products = Array.from(document.querySelectorAll(".product-card"));
    const categoryButtons = document.querySelectorAll(".category-btn");
    const resultCount = document.getElementById("result-count");
    const unavailableMessage = document.getElementById("unavailable-message");
    const showAllButton = document.getElementById("show-all-products");

    // Currently selected category
    let selectedCategory = "all";

    // Search and filter products
    function filterProducts() {
        const searchText = searchInput.value.trim().toLowerCase();
        let visibleProducts = 0;

        products.forEach(function (product) {
            const title = product.querySelector("h3")
                .textContent.trim().toLowerCase();

            const descriptionElement = product.querySelector(".product-description")
                || product.querySelector("p");

            const description = descriptionElement
                ? descriptionElement.textContent.trim().toLowerCase()
                : "";

            const categoryElement = product.querySelector(".product-category");

            const category = categoryElement
                ? categoryElement.textContent.trim().toLowerCase()
                : "";

            // Check search text
            const matchesSearch =
                title.includes(searchText) ||
                description.includes(searchText) ||
                category.includes(searchText);

            // Check selected category
            const matchesCategory =
                selectedCategory === "all" ||
                category === selectedCategory;

            // Show or hide product
            if (matchesSearch && matchesCategory) {
                product.style.display = "";
                visibleProducts++;
            } else {
                product.style.display = "none";
            }
        });

        // Update result count
        if (resultCount) {
            resultCount.innerHTML =
                `Showing <strong>${visibleProducts}</strong> product${visibleProducts !== 1 ? "s" : ""}`;
        }

        // Show empty-state message when no products match
        if (productList && unavailableMessage) {
            if (visibleProducts === 0) {
                productList.style.display = "none";
                unavailableMessage.style.display = "block";
            } else {
                productList.style.display = "";
                unavailableMessage.style.display = "none";
            }
        }

        // Show or hide clear-search button
        if (clearSearch) {
            clearSearch.style.display =
                searchText.length > 0 ? "flex" : "none";
        }
    }

    // Search as the user types
    if (searchInput) {
        searchInput.addEventListener("input", filterProducts);
    }

    // Filter by category
    categoryButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            categoryButtons.forEach(function (btn) {
                btn.classList.remove("active");
            });

            button.classList.add("active");

            selectedCategory = (
                button.dataset.category || "all"
            ).trim().toLowerCase();

            filterProducts();
        });
    });

    // Reset search and category filters
    function resetFilters() {
        if (searchInput) {
            searchInput.value = "";
        }

        selectedCategory = "all";

        categoryButtons.forEach(function (button) {
            button.classList.remove("active");
        });

        const allButton = Array.from(categoryButtons).find(function (button) {
            return (button.dataset.category || "").trim().toLowerCase() === "all";
        });

        if (allButton) {
            allButton.classList.add("active");
        }

        filterProducts();
    }

    // Clear search button
    if (clearSearch) {
        clearSearch.addEventListener("click", function () {
            resetFilters();

            if (searchInput) {
                searchInput.focus();
            }
        });
    }

    // Show all products button
    if (showAllButton) {
        showAllButton.addEventListener("click", function () {
            resetFilters();
        });
    }

    // Display all products when the page first loads
    filterProducts();
});
