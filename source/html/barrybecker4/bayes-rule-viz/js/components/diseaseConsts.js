
const DISEASED_COLOR = "#d32f2f";        // More accessible red
const HEALTHY_COLOR = "#388e3c";         // More accessible green
const POSITIVE_COLOR = "#f57c00";        // Amber for positive
const TEST_NEG_HEALTHY_COLOR = "#4caf50"; // Lighter green
const TEST_NEG_DISEASED_COLOR = "#e57373"; // Lighter red

const DISEASED = "diseased";
const HEALTHY = "healthy";
const TEST_NEG_DISEASED = "test-negative-diseased";
const TEST_NEG_HEALTHY = "test-negative-healthy";
const TEST_POS = "test-positive";

// The 4 interesting intersection regions
const HEALTHY_TEST_NEG = HEALTHY + "--" + TEST_NEG_HEALTHY;
const DISEASED_TEST_POS = DISEASED + "--" + TEST_POS;
const DISEASED_TEST_NEG = DISEASED + "--" + TEST_NEG_DISEASED;
const HEALTHY_TEST_POS = HEALTHY + "--" + TEST_POS;

export default  {
    DISEASED, HEALTHY,
    TEST_NEG_DISEASED, TEST_NEG_HEALTHY, TEST_POS,
    HEALTHY_TEST_NEG, DISEASED_TEST_POS, DISEASED_TEST_NEG, HEALTHY_TEST_POS,
    NODES: [
        { "node": 0, "id": DISEASED, "name": "Diseased" },
        { "node": 1, "id": HEALTHY, "name": "Healthy" },
        { "node": 2, "id": TEST_NEG_DISEASED, "name": "Test negative, but infected!" },
        { "node": 3, "id": TEST_POS, "name": "Test positive for the Disease" },
        { "node": 4, "id": TEST_NEG_HEALTHY, "name": "Test negative and Healthy" }
    ],
    POSITIVE_COLOR,
    HEALTHY_COLOR,
    DISEASED_COLOR,
    TEST_NEG_HEALTHY_COLOR,
    TEST_NEG_DISEASED_COLOR,

    format,
}

function format(value, decimals) {
    return value.toLocaleString(undefined, { maximumFractionDigits: decimals });
}
