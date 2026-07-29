import { nodeColor } from './colorScale.js'
import floatingTooltip from '../floatingTooltip.js'

const DURATION = 300;

/**
 * @param {{ name: string, value: number, id: string }} d
 * @returns {string}
 */
function nodeTooltipText(d) {
    const count = Math.round(d.value).toLocaleString();
    switch (d.id) {
        case "diseased":
            return "Diseased: " + count + " — people who have the disease";
        case "healthy":
            return "Healthy: " + count + " — people who do not have the disease";
        case "test-positive":
            return "Test positive: " + count + " — people who tested positive";
        case "test-negative":
            return "Test negative: " + count + " — people who tested negative";
        default:
            return d.name + ": " + count;
    }
}

/**
 * Bind and draw sankey nodes.
 * @param {Object} nodesEl - d3 selection for nodes group
 * @param {Object} sankey - d3 sankey layout
 * @param {Array} graphNodes - graph nodes
 * @param {number} chartWidth - usable chart width (for label anchoring)
 */
export function addNodes(nodesEl, sankey, graphNodes, chartWidth) {
    const nodes = nodesEl.selectAll(".node").data(graphNodes);
    const nodeEnter = nodes.enter();

    const nodeG = nodeEnter.append("g")
        .attr("class", "node");

    nodes.attr("transform", function (d) {
        return "translate(" + d.x + "," + d.y + ")";
    });

    nodeG.append("rect")
        .attr("width", sankey.nodeWidth())
        .style("fill", nodeColor)
        .style("fill-opacity", 0.5)
        .style("stroke", function (d) {
            return d3.rgb(d.color).darker(1);
        })
        .on("mouseover", function (d) {
            d3.select(this).transition("tooltip").duration(DURATION)
                .style("fill-opacity", 0.9);
            floatingTooltip.show(nodeTooltipText(d), d3.event.clientX, d3.event.clientY);
        })
        .on("mousemove", function () {
            floatingTooltip.move(d3.event.clientX, d3.event.clientY);
        })
        .on("mouseout", function () {
            d3.select(this).transition("tooltip").duration(DURATION)
                .style("fill-opacity", 0.5);
            floatingTooltip.hide();
        });

    nodes.select("rect")
        .attr("height", function (d) {
            return Math.max(0, d.dy);
        });

    // Update fill if colors changed
    nodes.select("rect")
        .style("fill", nodeColor)
        .style("stroke", function (d) {
            return d3.rgb(d.color).darker(1);
        });

    nodeG.append("text")
        .attr("x", -6)
        .attr("dy", ".35em")
        .attr("text-anchor", "end")
        .attr("transform", null)
        .text(function (d) {
            return d.name;
        })
        .filter(function (d) {
            return d.x < chartWidth / 2;
        })
        .attr("x", 6 + sankey.nodeWidth())
        .attr("text-anchor", "start");

    nodes.select("text")
        .attr("y", function (d) {
            return d.dy / 2;
        })
        .text(function (d) {
            return d.name;
        });
}
