import diseaseConsts from '../../diseaseConsts.js'
import { getLinkId } from './colorScale.js'

/**
 * Opacity for non-focused Sankey flows while one outcome is highlighted.
 * Lower = stronger emphasis on the hovered outcome.
 */
export const HIGHLIGHT_DIM_OPACITY = 0.18;

/**
 * Opacity for non-focused Venn wedges / underlay circles while highlighting.
 * Lower than the Sankey value because translucent stacked circles still show through.
 */
export const HIGHLIGHT_DIM_OPACITY_VENN = 0.08;

/**
 * Shared resting opacity for true-negative green (Sankey ribbon + Venn population).
 * Highlighted state uses 1 so it matches the solid legend swatch.
 */
export const TRUE_NEGATIVE_OPACITY = 0.85;

/**
 * Below this ribbon thickness (px), solid strokes read as gone — use dashed outline.
 * ~156 true positives ≈ 0.6px on a typical chart; ~36 ≈ 0.15px (invisible).
 */
export const MIN_SOLID_VISIBLE_DY = 1.5;

/**
 * Single source of truth for Sankey flow ribbon paint + legend dots.
 * @type {Record<string, { stroke: string, opacity: number, dashedOutline?: { color: string, dasharray: string, onlyWhenThin?: boolean } }>}
 */
export const FLOW_STYLES = {
    [diseaseConsts.DISEASED_TEST_POS]: {
        stroke: diseaseConsts.TRUE_POSITIVE_COLOR,
        opacity: 0.85,
        // Hair-thin true positives otherwise vanish; dash like false negatives.
        dashedOutline: {
            color: diseaseConsts.TRUE_POSITIVE_OUTLINE,
            dasharray: "6 4",
            onlyWhenThin: true,
        },
    },
    [diseaseConsts.HEALTHY_TEST_POS]: {
        stroke: diseaseConsts.FALSE_POSITIVE_COLOR,
        opacity: 0.75,
    },
    [diseaseConsts.DISEASED_TEST_NEG]: {
        stroke: diseaseConsts.FALSE_NEGATIVE_COLOR,
        opacity: 0.8,
        dashedOutline: {
            color: diseaseConsts.FALSE_NEGATIVE_OUTLINE,
            dasharray: "6 4",
        },
    },
    [diseaseConsts.HEALTHY_TEST_NEG]: {
        stroke: diseaseConsts.TRUE_NEGATIVE_COLOR,
        opacity: TRUE_NEGATIVE_OPACITY,
    },
};

/**
 * @param {string} linkId
 * @returns {{ stroke: string, opacity: number, dashedOutline?: { color: string, dasharray: string, onlyWhenThin?: boolean } }}
 */
export function flowStyleFor(linkId) {
    return FLOW_STYLES[linkId];
}

/**
 * Whether this link should draw a dashed outline at its current thickness.
 * @param {{ dy?: number }} d - sankey link (dy set after layout)
 * @returns {boolean}
 */
export function shouldShowDashedOutline(d) {
    const outline = flowStyleFor(getLinkId(d)).dashedOutline;
    if (!outline) {
        return false;
    }
    if (outline.onlyWhenThin) {
        return (d.dy || 0) < MIN_SOLID_VISIBLE_DY;
    }
    return true;
}

/** Legend row order matching the design mock. */
export const LEGEND_ENTRIES = [
    {
        id: diseaseConsts.DISEASED_TEST_POS,
        label: "True positive",
        describe: "has the disease, tests positive",
        alert: false,
        countKey: "testPositiveAndDiseased",
    },
    {
        id: diseaseConsts.DISEASED_TEST_NEG,
        label: "False negative",
        describe: "has the disease, tests negative (missed!)",
        alert: true,
        countKey: "testNegButDiseased",
    },
    {
        id: diseaseConsts.HEALTHY_TEST_POS,
        label: "False positive",
        describe: "healthy, tests positive",
        alert: false,
        countKey: "testPositiveButHealthy",
    },
    {
        id: diseaseConsts.HEALTHY_TEST_NEG,
        label: "True negative",
        describe: "healthy, tests negative",
        alert: false,
        countKey: "testNegAndHealthy",
    },
];
