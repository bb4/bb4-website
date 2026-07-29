import diseaseConsts from '../../diseaseConsts.js'
import { HIGHLIGHT_DIM_OPACITY_VENN, TRUE_NEGATIVE_OPACITY } from '../sankey/flowStyles.js'

const DURATION = 300;

/** Resting fill-opacity for outcome paths (opaque so green underlay does not tint hue). */
const OUTCOME_FILL_OPACITY = 1;

const OUTCOME_PATH_SELECTOR = [
    "." + diseaseConsts.DISEASED_TEST_POS,
    "." + diseaseConsts.DISEASED_TEST_NEG,
    "." + diseaseConsts.HEALTHY_TEST_POS,
].join(", ");

/** Background circles that show through dimmed wedges if left at full strength. */
const UNDERLAY_SELECTOR = [
    "." + diseaseConsts.WHOLE_POPULATION,
    "circle.test-positive-circle",
    "circle.diseased-circle",
].join(", ");

function getSvg() {
    return d3.select("#venn-diagram-view svg");
}

function dimUnderlays(selector) {
    getSvg().selectAll(selector)
        .transition().duration(DURATION)
        .style("opacity", HIGHLIGHT_DIM_OPACITY_VENN);
}

function clearUnderlayDim() {
    getSvg().selectAll(UNDERLAY_SELECTOR)
        .transition().duration(DURATION)
        .style("opacity", 1);
}

/**
 * Dim sibling wedges and underlay circles; emphasize one outcome path.
 * @param {string} id
 * @param {string} strokeColor
 */
function emphasizeOutcome(id, strokeColor) {
    const svg = getSvg();
    svg.selectAll(OUTCOME_PATH_SELECTOR)
        .transition().duration(DURATION)
        .style("opacity", HIGHLIGHT_DIM_OPACITY_VENN);
    dimUnderlays(UNDERLAY_SELECTOR);
    svg.select("." + id)
        .transition().duration(DURATION)
        .style("opacity", 1)
        .style("stroke", strokeColor)
        .style("stroke-width", 2.5)
        .style("stroke-opacity", 1)
        .style("fill-opacity", OUTCOME_FILL_OPACITY);
}

/** Partial white wash on inner circles — toward white, not fully opaque. */
const TN_INNER_WHITE_OPACITY = 0.5;

/**
 * True-negative has no dedicated path — keep population green strong, but wash
 * diseased / test-positive toward white (dimming alone still shows color over green).
 */
function emphasizeTrueNegative() {
    const svg = getSvg();
    svg.selectAll(OUTCOME_PATH_SELECTOR)
        .transition().duration(DURATION)
        .style("opacity", HIGHLIGHT_DIM_OPACITY_VENN);
    svg.selectAll("circle.test-positive-circle, circle.diseased-circle")
        .transition().duration(DURATION)
        .style("opacity", 1)
        .attr("fill", "#ffffff")
        .attr("fill-opacity", TN_INNER_WHITE_OPACITY)
        .style("stroke", "none")
        .style("stroke-width", 0)
        .style("stroke-opacity", 0);
    svg.select("." + diseaseConsts.WHOLE_POPULATION)
        .transition().duration(DURATION)
        .style("opacity", 1)
        .style("stroke", "black")
        .style("fill-opacity", 1)
        .style("stroke-width", 3)
        .style("stroke-opacity", 0.6);
}

function clearOutcomeEmphasis() {
    clearUnderlayDim();
    restoreInnerCircleFills();
    getSvg().selectAll(OUTCOME_PATH_SELECTOR)
        .transition().duration(DURATION)
        .style("opacity", 1)
        .style("stroke", "none")
        .style("stroke-width", 0)
        .style("stroke-opacity", 0)
        .style("fill-opacity", OUTCOME_FILL_OPACITY)
        .style("filter", null);
}

function restoreInnerCircleFills() {
    const svg = getSvg();
    svg.select("circle.test-positive-circle")
        .attr("fill", diseaseConsts.POSITIVE_COLOR)
        .attr("fill-opacity", 0.25);
    svg.select("circle.diseased-circle")
        .attr("fill", diseaseConsts.DISEASED_COLOR)
        .attr("fill-opacity", 0.15);
}

function clearTrueNegativeEmphasis() {
    clearOutcomeEmphasis();
    getSvg().select("." + diseaseConsts.WHOLE_POPULATION)
        .transition().duration(DURATION)
        .style("stroke", "none")
        .style("fill-opacity", TRUE_NEGATIVE_OPACITY)
        .style("stroke-width", 0)
        .style("stroke-opacity", 0);
}

/**
 * @param {string} id - region id from diseaseConsts
 */
export function doHighlight(id) {
    switch (id) {
        case diseaseConsts.WHOLE_POPULATION:
        case diseaseConsts.HEALTHY_TEST_NEG:
            emphasizeTrueNegative();
            return;
        case diseaseConsts.DISEASED_TEST_POS:
            emphasizeOutcome(id, diseaseConsts.TRUE_POSITIVE_COLOR);
            return;
        case diseaseConsts.HEALTHY_TEST_POS:
            emphasizeOutcome(id, diseaseConsts.FALSE_POSITIVE_COLOR);
            return;
        case diseaseConsts.DISEASED_TEST_NEG:
            emphasizeOutcome(id, diseaseConsts.FALSE_NEGATIVE_COLOR);
            return;
        default:
            return;
    }
}

/**
 * @param {string} id - region id from diseaseConsts
 */
export function doUnhighlight(id) {
    switch (id) {
        case diseaseConsts.WHOLE_POPULATION:
        case diseaseConsts.HEALTHY_TEST_NEG:
            clearTrueNegativeEmphasis();
            return;
        case diseaseConsts.DISEASED_TEST_POS:
        case diseaseConsts.HEALTHY_TEST_POS:
        case diseaseConsts.DISEASED_TEST_NEG:
            clearOutcomeEmphasis();
            return;
        default:
            return;
    }
}
