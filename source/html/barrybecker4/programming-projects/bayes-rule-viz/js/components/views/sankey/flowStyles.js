import diseaseConsts from '../../diseaseConsts.js'

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
 * Single source of truth for Sankey flow ribbon paint + legend dots.
 * @type {Record<string, { stroke: string, opacity: number, dashedOutline?: { color: string, dasharray: string } }>}
 */
export const FLOW_STYLES = {
    [diseaseConsts.DISEASED_TEST_POS]: {
        stroke: diseaseConsts.TRUE_POSITIVE_COLOR,
        opacity: 0.85,
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
 * @returns {{ stroke: string, opacity: number, dashedOutline?: { color: string, dasharray: string } }}
 */
export function flowStyleFor(linkId) {
    return FLOW_STYLES[linkId];
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
