const OFFSET_X = 12;
const OFFSET_Y = 16;

let tipEl = null;

function ensureTip() {
    if (tipEl) {
        return tipEl;
    }
    tipEl = document.createElement("div");
    tipEl.className = "viz-tooltip";
    tipEl.style.display = "none";
    document.body.appendChild(tipEl);
    return tipEl;
}

function position(el, x, y) {
    el.style.left = (x + OFFSET_X) + "px";
    el.style.top = (y + OFFSET_Y) + "px";
}

/**
 * Instant cursor-following tooltip (replaces native title attributes).
 */
export default {
    show(text, x, y) {
        const el = ensureTip();
        el.textContent = text;
        el.style.display = "block";
        position(el, x, y);
    },

    move(x, y) {
        if (!tipEl || tipEl.style.display === "none") {
            return;
        }
        position(tipEl, x, y);
    },

    hide() {
        if (!tipEl) {
            return;
        }
        tipEl.style.display = "none";
    },
};
