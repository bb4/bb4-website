import circleUtils from '../circleUtils.js'
import diseaseConsts from '../../diseaseConsts.js'
import { pathFunc } from './pathFunc.js'

/** all circles will be relative to the test positive circle */
export const TEST_POS_CIRCLE_RADIUS = 250;
export const POP_LABEL_X = 68;
export const POP_LABEL_Y = 95;
/** Mild tilt along the left crescent — mostly horizontal, not vertical */
const POP_LABEL_ROT = 22;
export const MIN_DISEASED_RADIUS = 12;

/** Nudge the whole assembly so top/right labels stay inside the clipped panel */
const SHIFT_LEFT = 48;
const SHIFT_DOWN = 40;
const PANEL_PAD = 14;
const CALLOUT_CHAR_WIDTH = 6.4;

/**
 * Visual center of the purple overlap region (must lie inside both circles).
 * When the smaller circle is mostly inside the larger, the overlap looks like
 * that smaller disk — use its center, not the chord midpoint on the rim.
 * @returns {{ x: number, y: number }}
 */
function overlapAnchor(cx1, cy1, r1, cx2, cy2, r2) {
    const dx = cx2 - cx1;
    const dy = cy2 - cy1;
    const dist = Math.sqrt(dx * dx + dy * dy);
    if (dist < 1e-6) {
        return { x: cx1, y: cy1 };
    }

    const smallIs2 = r2 <= r1;
    const scx = smallIs2 ? cx2 : cx1;
    const scy = smallIs2 ? cy2 : cy1;
    const sr = smallIs2 ? r2 : r1;
    const lr = smallIs2 ? r1 : r2;

    // Mostly contained: purple ≈ smaller circle → aim at its center
    if (dist + sr * 0.4 < lr) {
        return { x: scx, y: scy };
    }

    // Partial overlap: midpoint of the overlap segment on the line of centers
    const tMin = Math.max(0, dist - r2);
    const tMax = Math.min(dist, r1);
    const t = (tMin + tMax) / 2;
    return { x: cx1 + dx * (t / dist), y: cy1 + dy * (t / dist) };
}

/**
 * Place the permanent overlap callout so it stays on-screen and clear of the Diseased label.
 */
function placeOverlapCallout({
    chartWidth, anchor, centerX, chartHeightD2, diseasedCenterY,
    diseasedRad, diseasedTop, line1,
}) {
    const textWidth = Math.max(line1.length * CALLOUT_CHAR_WIDTH, 120);
    const maxLabelX = chartWidth - PANEL_PAD - textWidth;
    const diseasedLabelX = centerX + 30;
    const diseasedLabelY = Math.max(PANEL_PAD + 12, diseasedTop - 5);

    let labelX = Math.max(anchor.x + 18, centerX + Math.min(diseasedRad, 80) + 20);
    let labelY = Math.min(diseasedCenterY, chartHeightD2) - 28;

    // Prefer sitting below the Diseased label when they would collide
    if (labelY < diseasedLabelY + 22 && labelX < diseasedLabelX + 90) {
        labelY = diseasedLabelY + 26;
    }

    labelY = Math.max(PANEL_PAD + 18, Math.min(labelY, chartHeightD2 - 24));
    labelX = Math.min(labelX, maxLabelX);

    // If still no room on the right, park the callout to the left of the diseased circle
    if (labelX < PANEL_PAD + 8 || labelX + textWidth > chartWidth - PANEL_PAD) {
        labelX = Math.max(PANEL_PAD, Math.min(centerX - textWidth - 12, maxLabelX));
        labelY = Math.max(PANEL_PAD + 18, diseasedTop + 8);
    }

    labelX = Math.max(PANEL_PAD, Math.min(labelX, maxLabelX));

    return { labelX, labelY, diseasedLabelX, diseasedLabelY };
}

/**
 * @param {Object} stats - SimulationStats
 * @param {number} totalPopulation
 * @param {number} chartWidth
 * @param {number} chartHeight
 */
export function computeVennLayout(stats, totalPopulation, chartWidth, chartHeight) {
    const chartWidthD2 = chartWidth / 2;
    const chartHeightD2 = chartHeight / 2 + SHIFT_DOWN;

    const numPositiveAndDiseased = stats.testPositiveAndDiseased;
    const numPositiveAndHealthy = stats.testPositiveButHealthy;
    const testNegButDiseased = Math.round(stats.testNegButDiseased);
    const numDiseased = testNegButDiseased + numPositiveAndDiseased;
    const numPositive = numPositiveAndDiseased + numPositiveAndHealthy;

    const testPositiveRad = TEST_POS_CIRCLE_RADIUS;
    const scaleFactor = Math.sqrt(totalPopulation / numPositive);
    const trueDiseasedRad = testPositiveRad * Math.sqrt(numDiseased / numPositive);
    const diseasedEnlarged = trueDiseasedRad < MIN_DISEASED_RADIUS;
    const diseasedRad = diseasedEnlarged ? MIN_DISEASED_RADIUS : trueDiseasedRad;
    const popRad = testPositiveRad * scaleFactor;
    const diseaseArea = Math.PI * diseasedRad * diseasedRad;
    const overlap = (numPositiveAndDiseased / Math.max(numDiseased, 1e-9)) * diseaseArea;

    const distance = circleUtils.findCircleSeparation({
        radiusA: testPositiveRad,
        radiusB: diseasedRad,
        overlap: overlap,
    });

    const centerX = chartWidthD2 + testPositiveRad - 180 - SHIFT_LEFT;
    const diseasedCenterY = chartHeightD2 - distance;
    const popCircleCenterX = Math.max(chartWidthD2 - popRad, 0) + popRad + 40 - SHIFT_LEFT;

    const anchor = overlapAnchor(
        centerX, chartHeightD2, testPositiveRad,
        centerX, diseasedCenterY, diseasedRad
    );

    return {
        chartWidth,
        chartWidthD2,
        chartHeightD2,
        centerX,
        diseasedCenterY,
        popCircleCenterX,
        testPositiveRad,
        diseasedRad,
        trueDiseasedRad,
        diseasedEnlarged,
        popRad,
        numPositiveAndDiseased,
        numPositiveAndHealthy,
        testNegButDiseased,
        numDiseased,
        numPositive,
        totalPopulation,
        overlapAnchor: anchor,
    };
}

/**
 * Apply layout values to the venn SVG.
 * @param {Object} svg - d3 selection of root svg
 * @param {ReturnType<typeof computeVennLayout>} layout
 * @param {number} chartHeight
 */
export function applyVennLayout(svg, layout, chartHeight) {
    const {
        chartWidth, chartHeightD2, centerX, diseasedCenterY, popCircleCenterX,
        testPositiveRad, diseasedRad, popRad,
        numPositiveAndDiseased, numPositiveAndHealthy,
        testNegButDiseased, numDiseased, numPositive, totalPopulation,
        overlapAnchor: anchor,
    } = layout;

    const g = svg.select("g");

    g.selectAll("circle." + diseaseConsts.WHOLE_POPULATION)
        .attr("cx", popCircleCenterX)
        .attr("cy", chartHeightD2)
        .attr("r", popRad);

    // Pin to the upper-left crescent, clear of the Tested Positive circle
    const popLabelX = POP_LABEL_X;
    const popLabelY = Math.min(POP_LABEL_Y, Math.max(70, chartHeightD2 - testPositiveRad - 24));
    const diseasedTop = diseasedCenterY - diseasedRad;

    g.selectAll("text.venn-label.positive")
        .attr("x", centerX - 30)
        .attr("y", Math.min(0.72 * chartHeight, chartHeightD2 + testPositiveRad * 0.55));

    const pctDiseasedGivenPositive = diseaseConsts.format(100 * numPositiveAndDiseased / numPositive, 2);
    const n = Math.round(numPositiveAndDiseased).toLocaleString();
    const m = Math.round(numPositive).toLocaleString();
    const line1 = n + " of " + m + " positives (" + pctDiseasedGivenPositive + "%)";
    const line2 = "truly have the disease";

    const {
        labelX, labelY, diseasedLabelX, diseasedLabelY,
    } = placeOverlapCallout({
        chartWidth, anchor, centerX, chartHeightD2, diseasedCenterY,
        diseasedRad, diseasedTop, line1,
    });

    g.selectAll("text.venn-label.diseased")
        .attr("x", diseasedLabelX)
        .attr("y", diseasedLabelY);

    g.selectAll("line.venn-line.diseased")
        .attr("x1", centerX)
        .attr("y1", Math.min(diseasedTop + 1, diseasedLabelY + 8))
        .attr("x2", diseasedLabelX - 1)
        .attr("y2", diseasedLabelY + 2);

    g.selectAll("text.venn-label.population")
        .attr("x", popLabelX)
        .attr("y", popLabelY)
        .attr("transform", "rotate(" + -POP_LABEL_ROT + " " + popLabelX + " " + popLabelY + ")");

    g.selectAll("circle.test-positive-circle")
        .attr("cx", centerX)
        .attr("cy", chartHeightD2)
        .attr("r", testPositiveRad);

    g.selectAll("circle.diseased-circle")
        .attr("cx", centerX)
        .attr("cy", diseasedCenterY)
        .attr("r", diseasedRad);

    const pdPath = g.selectAll("path." + diseaseConsts.DISEASED_TEST_POS);
    pdPath.attr("d", pathFunc(centerX, chartHeightD2, testPositiveRad,
        centerX, diseasedCenterY, diseasedRad, 0, 1, 1, 1));

    const ndPath = g.selectAll("path." + diseaseConsts.DISEASED_TEST_NEG);
    ndPath.attr("d", pathFunc(centerX, chartHeightD2, testPositiveRad,
        centerX, diseasedCenterY, diseasedRad, 0, 1, 0, 0));

    const phPath = g.selectAll("path." + diseaseConsts.HEALTHY_TEST_POS);
    phPath.attr("d", pathFunc(centerX, chartHeightD2, testPositiveRad,
        centerX, diseasedCenterY, diseasedRad, 1, 0, 1, 1));

    g.select("line.venn-line.overlap-callout")
        .attr("x1", anchor.x)
        .attr("y1", anchor.y)
        .attr("x2", labelX - 2)
        .attr("y2", labelY + 6);

    const callout = g.select("text.venn-label.overlap-callout")
        .attr("x", labelX)
        .attr("y", labelY);
    callout.selectAll("tspan").remove();
    callout.append("tspan")
        .attr("x", labelX)
        .attr("dy", 0)
        .text(line1);
    callout.append("tspan")
        .attr("x", labelX)
        .attr("dy", "1.2em")
        .text(line2);

    layout._tooltipCounts = {
        totalPopulation,
        numDiseased: Math.round(numDiseased),
        numPositive: Math.round(numPositive),
        numPositiveAndDiseased: Math.round(numPositiveAndDiseased),
        numPositiveAndHealthy: Math.round(numPositiveAndHealthy),
        testNegButDiseased,
        pctDiseasedGivenPositive,
    };
}
