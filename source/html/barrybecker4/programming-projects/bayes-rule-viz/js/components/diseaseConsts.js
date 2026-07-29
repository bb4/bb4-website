const DISEASED_COLOR = "#d32f2f";
const HEALTHY_COLOR = "#388e3c";
const POSITIVE_COLOR = "#1976d2";
const TEST_NEG_COLOR = "#9e9e9e";
/** Shared palette for the four test outcomes (legend, Sankey, Venn). */
const TRUE_POSITIVE_COLOR = "#1976d2";
const TRUE_POSITIVE_OUTLINE = "#1976d2";
const FALSE_POSITIVE_COLOR = "#90caf9";
const FALSE_NEGATIVE_COLOR = "#d32f2f";
const FALSE_NEGATIVE_OUTLINE = "#d32f2f";
/** Same light green as the Venn whole-population / true-negative area. */
const WHOLE_POPULATION_COLOR = "#a5d6a7";
const TRUE_NEGATIVE_COLOR = WHOLE_POPULATION_COLOR;

const DISEASED = "diseased";
const HEALTHY = "healthy";
const TEST_POS = "test-positive";
const TEST_NEG = "test-negative";
const WHOLE_POPULATION = "whole-population";

const DISEASED_TEST_POS = DISEASED + "--" + TEST_POS;
const HEALTHY_TEST_POS = HEALTHY + "--" + TEST_POS;
const DISEASED_TEST_NEG = DISEASED + "--" + TEST_NEG;
const HEALTHY_TEST_NEG = HEALTHY + "--" + TEST_NEG;

/** Probability-term highlight IDs (labels / formula only — not diagram outcomes). */
const PROB_D = "prob-d";
const PROB_D_GIVEN_POS = "prob-d-given-pos";
const PROB_POS_GIVEN_D = "prob-pos-given-d";
const PROB_POS = "prob-pos";
const PROB_NEG_GIVEN_H = "prob-neg-given-h";

export default {
    DISEASED, HEALTHY,
    TEST_POS, TEST_NEG, WHOLE_POPULATION,
    DISEASED_TEST_POS, HEALTHY_TEST_POS, DISEASED_TEST_NEG, HEALTHY_TEST_NEG,
    PROB_D, PROB_D_GIVEN_POS, PROB_POS_GIVEN_D, PROB_POS, PROB_NEG_GIVEN_H,
    NODES: [
        { "node": 0, "id": DISEASED, "name": "Diseased" },
        { "node": 1, "id": HEALTHY, "name": "Healthy" },
        { "node": 2, "id": TEST_POS, "name": "Test positive" },
        { "node": 3, "id": TEST_NEG, "name": "Test negative" },
    ],
    POSITIVE_COLOR,
    HEALTHY_COLOR,
    DISEASED_COLOR,
    TEST_NEG_COLOR,
    TRUE_POSITIVE_COLOR,
    TRUE_POSITIVE_OUTLINE,
    FALSE_POSITIVE_COLOR,
    TRUE_NEGATIVE_COLOR,
    FALSE_NEGATIVE_COLOR,
    FALSE_NEGATIVE_OUTLINE,
    WHOLE_POPULATION_COLOR,

    format,
}

function format(value, decimals) {
    return value.toLocaleString(undefined, { maximumFractionDigits: decimals });
}
