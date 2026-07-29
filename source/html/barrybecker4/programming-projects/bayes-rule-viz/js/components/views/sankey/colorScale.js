import diseaseConsts from '../../diseaseConsts.js'

/** @type {Function} */
const colorScale = d3.scale.ordinal()
    .range([
        diseaseConsts.DISEASED_COLOR,
        diseaseConsts.HEALTHY_COLOR,
        diseaseConsts.POSITIVE_COLOR,
        diseaseConsts.TEST_NEG_COLOR,
    ])
    .domain([
        diseaseConsts.DISEASED,
        diseaseConsts.HEALTHY,
        diseaseConsts.TEST_POS,
        diseaseConsts.TEST_NEG,
    ]);

/**
 * @param {{ id: string, color?: string }} d
 * @returns {string}
 */
export function nodeColor(d) {
    return d.color = colorScale(d.id);
}

export function getLinkId(d) {
    return d.source.id + "--" + d.target.id;
}
