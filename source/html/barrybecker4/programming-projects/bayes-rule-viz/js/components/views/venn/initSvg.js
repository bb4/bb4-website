import diseaseConsts from '../../diseaseConsts.js'
import { POP_LABEL_X, POP_LABEL_Y } from './layout.js'
import floatingTooltip from '../floatingTooltip.js'
import { TRUE_NEGATIVE_OPACITY } from '../sankey/flowStyles.js'

const margin = { top: 10, bottom: 10, left: 10 };

/**
 * @param {HTMLElement} el - svg wrap element
 * @param {number} totalPopulation
 * @param {{ emitHighlight: Function, emitUnhighlight: Function, getTooltipCounts: Function }} handlers
 */
export function initVennSvg(el, totalPopulation, handlers) {
    const rootSvg = d3.select(el).append("svg");

    const svg = rootSvg.append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    function tipHandlers(getText, highlightId) {
        return {
            mouseover: function () {
                if (highlightId) {
                    handlers.emitHighlight(highlightId);
                }
                floatingTooltip.show(getText(), d3.event.clientX, d3.event.clientY);
            },
            mousemove: function () {
                floatingTooltip.move(d3.event.clientX, d3.event.clientY);
            },
            mouseout: function () {
                if (highlightId) {
                    handlers.emitUnhighlight(highlightId);
                }
                floatingTooltip.hide();
            },
        };
    }

    const popTips = tipHandlers(function () {
        const c = handlers.getTooltipCounts();
        return "Whole population: " + c.totalPopulation.toLocaleString() +
            " people. Those outside the red circle are healthy.";
    }, diseaseConsts.HEALTHY_TEST_NEG);

    svg.append("circle")
        .attr("class", diseaseConsts.WHOLE_POPULATION)
        .attr("fill-opacity", TRUE_NEGATIVE_OPACITY)
        .attr("fill", diseaseConsts.WHOLE_POPULATION_COLOR)
        .on("mouseover", popTips.mouseover)
        .on("mousemove", popTips.mousemove)
        .on("mouseout", popTips.mouseout);

    svg.append("text")
        .attr("class", "venn-label population")
        .attr("x", POP_LABEL_X)
        .attr("y", POP_LABEL_Y)
        .text("Whole Population");

    const posTips = tipHandlers(function () {
        const c = handlers.getTooltipCounts();
        return c.numPositive.toLocaleString() + " people test positive in total.";
    }, null);

    svg.append("circle")
        .attr("class", "test-positive-circle")
        .attr("fill-opacity", 0.25)
        .attr("fill", diseaseConsts.POSITIVE_COLOR)
        .style("cursor", "pointer")
        .on("mouseover", posTips.mouseover)
        .on("mousemove", posTips.mousemove)
        .on("mouseout", posTips.mouseout);

    svg.append("text")
        .attr("class", "venn-label diseased")
        .text("Diseased");
    svg.append("line")
        .attr("class", "venn-line diseased");

    const disTips = tipHandlers(function () {
        const c = handlers.getTooltipCounts();
        return "Diseased: " + c.numDiseased.toLocaleString() +
            " people have the disease (" +
            diseaseConsts.format(100 * c.numDiseased / c.totalPopulation, 2) + "%).";
    }, null);

    svg.append("circle")
        .attr("class", "diseased-circle")
        .attr("fill-opacity", 0.15)
        .attr("fill", diseaseConsts.DISEASED_COLOR)
        .style("cursor", "pointer")
        .on("mouseover", disTips.mouseover)
        .on("mousemove", disTips.mousemove)
        .on("mouseout", disTips.mouseout);

    const tpTips = tipHandlers(function () {
        const c = handlers.getTooltipCounts();
        return c.numPositiveAndDiseased.toLocaleString() + " of " +
            c.numPositive.toLocaleString() + " positives (" +
            c.pctDiseasedGivenPositive + "%) truly have the disease.";
    }, diseaseConsts.DISEASED_TEST_POS);

    svg.append("path")
        .attr("class", diseaseConsts.DISEASED_TEST_POS)
        .attr("fill-opacity", 1)
        .attr("fill", diseaseConsts.TRUE_POSITIVE_COLOR)
        .style("cursor", "pointer")
        .on("mouseover", tpTips.mouseover)
        .on("mousemove", tpTips.mousemove)
        .on("mouseout", tpTips.mouseout);

    const fnTips = tipHandlers(function () {
        const c = handlers.getTooltipCounts();
        return "False negative: " + c.testNegButDiseased.toLocaleString() +
            " of " + c.numDiseased.toLocaleString() +
            " with the disease test negative (missed!).";
    }, diseaseConsts.DISEASED_TEST_NEG);

    svg.append("path")
        .attr("class", diseaseConsts.DISEASED_TEST_NEG)
        .attr("fill-opacity", 1)
        .attr("fill", diseaseConsts.FALSE_NEGATIVE_COLOR)
        .style("cursor", "pointer")
        .on("mouseover", fnTips.mouseover)
        .on("mousemove", fnTips.mousemove)
        .on("mouseout", fnTips.mouseout);

    const fpTips = tipHandlers(function () {
        const c = handlers.getTooltipCounts();
        return "False positive: " + c.numPositiveAndHealthy.toLocaleString() +
            " are healthy out of the " + c.numPositive.toLocaleString() +
            " that tested positive.";
    }, diseaseConsts.HEALTHY_TEST_POS);

    svg.append("path")
        .attr("class", diseaseConsts.HEALTHY_TEST_POS)
        .attr("fill-opacity", 1)
        .attr("fill", diseaseConsts.FALSE_POSITIVE_COLOR)
        .style("cursor", "pointer")
        .on("mouseover", fpTips.mouseover)
        .on("mousemove", fpTips.mousemove)
        .on("mouseout", fpTips.mouseout);

    svg.append("text")
        .attr("class", "venn-label positive")
        .text("Tested Positive");

    svg.append("line")
        .attr("class", "venn-line overlap-callout");

    svg.append("text")
        .attr("class", "venn-label overlap-callout")
        .attr("text-anchor", "start");
}
