/**
 * @typedef {Object} SimulationStats
 * @property {number} diseasedPop
 * @property {number} healthyPop
 * @property {number} testNegAndHealthy
 * @property {number} testNegButDiseased
 * @property {number} testPositiveAndDiseased
 * @property {number} testPositiveButHealthy
 * @property {number} testPositive
 */

/**
 * @typedef {Object} GraphLink
 * @property {number} source
 * @property {number} target
 * @property {number} value
 */

/**
 * Compute population counts from disease prevalence and test accuracy (fractions).
 * @param {{ probDiseased: number, testAccuracy: number, totalPopulation: number }} params
 * @returns {SimulationStats}
 */
export function computeStats({ probDiseased, testAccuracy, totalPopulation }) {
    const diseasedPop = probDiseased * totalPopulation;
    const healthyPop = totalPopulation - diseasedPop;
    const testNegAndHealthy = testAccuracy * healthyPop;
    const testNegButDiseased = (1.0 - testAccuracy) * diseasedPop;
    const testPositiveAndDiseased = diseasedPop - testNegButDiseased;
    const testPositiveButHealthy = healthyPop - testNegAndHealthy;
    const testPositive = testPositiveAndDiseased + testPositiveButHealthy;

    return {
        diseasedPop,
        healthyPop,
        testNegAndHealthy,
        testNegButDiseased,
        testPositiveAndDiseased,
        testPositiveButHealthy,
        testPositive,
    };
}

/**
 * Build sankey link objects from simulation stats (4 nodes: D, H, Pos, Neg).
 * @param {SimulationStats} stats
 * @returns {GraphLink[]}
 */
export function buildGraphLinks(stats) {
    return [
        { source: 0, target: 2, value: stats.testPositiveAndDiseased },
        { source: 0, target: 3, value: stats.testNegButDiseased },
        { source: 1, target: 2, value: stats.testPositiveButHealthy },
        { source: 1, target: 3, value: stats.testNegAndHealthy },
    ];
}

/**
 * Clone node definitions so D3 can mutate them safely.
 * @param {Array<{node: number, id: string, name: string}>} nodes
 * @returns {Array<{node: number, id: string, name: string}>}
 */
export function cloneNodes(nodes) {
    return nodes.map(n => ({ ...n }));
}
