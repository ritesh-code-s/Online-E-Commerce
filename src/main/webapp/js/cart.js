
document.addEventListener("DOMContentLoaded", () => {
    const CART_KEY = "shopsphere_cart";

    const $ = (id) => document.getElementById(id);

    const cartContainer = $("cart-container");
    const emptyCart = $("empty-cart");
    const cartSummary = $("cart-summary");
    const cartCount = $("cart-count");
    const summaryItems = $("summary-items");
    const summarySubtotal = $("summary-subtotal");
    const summaryShipping = $("summary-shipping");
    const summaryTotal = $("summary-total");
    const clearCartButton = $("clear-cart");

    const formatPrice = (amount) =>
        new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 2
        }).format(amount);

    function getCart() {
        try {
            const saved = JSON.parse(
                localStorage.getItem(CART_KEY) || "[]"
            );

            if (!Array.isArray(saved)) return [];

            return saved.map((item) => ({
                ...item,
                quantity: Math.max(
                    1,
                    Math.floor(Number(item.quantity) || 1)
                )
            }));
        } catch {
            return [];
        }
    }

    function saveCart(cart) {
        localStorage.setItem(CART_KEY, JSON.stringify(cart));

        // Keep other pages in sync when they listen for storage changes.
        window.dispatchEvent(new Event("shopsphere-cart-updated"));
    }

    function escapeHTML(value) {
        return String(value ?? "").replace(/[&<>"']/g, (char) => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        })[char]);
    }

    function getPrice(item) {
        // Accept common price field names.
        const raw = item.price ?? item.productPrice ?? item.cost;

        if (raw === null || raw === undefined || raw === "") {
            return null;
        }

        const price = Number(raw);

        return Number.isFinite(price) && price >= 0
            ? price
            : null;
    }

    function updateSummary(cart) {
        const items = cart.reduce(
            (sum, item) => sum + item.quantity, 0
        );

        const subtotal = cart.reduce((sum, item) => {
            const price = getPrice(item);
            return sum + (price ?? 0) * item.quantity;
        }, 0);

        // Free shipping for orders of ₹999 or more.
        const shipping = subtotal === 0 || subtotal >= 999
            ? 0
            : 49;

        if (summaryItems) {
            summaryItems.textContent = items;
        }

        if (summarySubtotal) {
            summarySubtotal.textContent = formatPrice(subtotal);
        }

        if (summaryShipping) {
            summaryShipping.textContent = shipping === 0
                ? "FREE"
                : formatPrice(shipping);
        }

        if (summaryTotal) {
            summaryTotal.textContent = formatPrice(
                subtotal + shipping
            );
        }
    }

    function renderCart() {
        const cart = getCart();

        if (cartCount) {
            cartCount.textContent = cart.reduce(
                (sum, item) => sum + item.quantity, 0
            );
        }

        const isEmpty = cart.length === 0;

        if (emptyCart) {
            emptyCart.style.display = isEmpty ? "block" : "none";
        }

        if (cartSummary) {
            cartSummary.style.display = isEmpty ? "none" : "block";
        }

        if (!cartContainer) return;

        if (isEmpty) {
            cartContainer.innerHTML = "";
            updateSummary([]);
            return;
        }

        cartContainer.innerHTML = cart.map((item) => {
            const id = escapeHTML(item.id);
            const name = escapeHTML(item.name || "Product");
            const category = escapeHTML(item.category || "Product");
            const description = escapeHTML(item.description || "");
            const image = escapeHTML(item.image || "");
            const price = getPrice(item);
            const quantity = item.quantity;

            const priceText = price === null
                ? "Price unavailable"
                : formatPrice(price);

            const itemSubtotal = price === null
                ? "—"
                : formatPrice(price * quantity);

            return `
                <article class="cart-item" data-id="${id}">
                    <div class="cart-product-image">
                        ${image
                    ? `<img src="${image}" alt="${name}"
                                     onerror="this.style.display='none'">`
                    : `<div class="cart-image-placeholder">🛍️</div>`
                }
                    </div>

                    <div class="cart-product-info">
                        <span class="cart-category">${category}</span>
                        <h3>${name}</h3>
                        <p>${description}</p>
                        <div class="cart-price">${priceText}</div>
                    </div>

                    <div class="cart-product-actions">
                        <div class="quantity-control">
                            <button type="button"
                                class="quantity-btn decrease"
                                data-id="${id}"
                                aria-label="Decrease quantity">−</button>

                            <span class="quantity">${quantity}</span>

                            <button type="button"
                                class="quantity-btn increase"
                                data-id="${id}"
                                aria-label="Increase quantity">+</button>
                        </div>

                        <strong class="item-subtotal">
                            ${itemSubtotal}
                        </strong>

                        <button type="button"
                            class="remove-item"
                            data-id="${id}">Remove</button>
                    </div>
                </article>
            `;
        }).join("");

        updateSummary(cart);
    }

    function changeQuantity(id, change) {
        const cart = getCart();
        const item = cart.find(
            (product) => String(product.id) === String(id)
        );

        if (!item) return;

        item.quantity += change;

        const updated = cart.filter(
            (product) => product.quantity > 0
        );

        saveCart(updated);
        renderCart();
    }

    function removeItem(id) {
        const updated = getCart().filter(
            (item) => String(item.id) !== String(id)
        );

        saveCart(updated);
        renderCart();
        showToast("Product removed from cart");
    }

    if (cartContainer) {
        cartContainer.addEventListener("click", (event) => {
            const button = event.target.closest("button");
            if (!button) return;

            const id = button.dataset.id;
            if (id === undefined) return;

            if (button.classList.contains("increase")) {
                changeQuantity(id, 1);
            } else if (button.classList.contains("decrease")) {
                changeQuantity(id, -1);
            } else if (button.classList.contains("remove-item")) {
                removeItem(id);
            }
        });
    }

    if (clearCartButton) {
        clearCartButton.addEventListener("click", () => {
            if (getCart().length === 0) return;

            if (confirm("Are you sure you want to clear your cart?")) {
                saveCart([]);
                renderCart();
                showToast("Cart cleared");
            }
        });
    }

    function showToast(message) {
        let toast = $("cart-toast");

        if (!toast) {
            toast = document.createElement("div");
            toast.id = "cart-toast";
            document.body.appendChild(toast);
        }

        toast.textContent = message;
        toast.classList.add("show");

        setTimeout(() => toast.classList.remove("show"), 2000);
    }

    renderCart();
});
