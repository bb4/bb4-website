import { getLinkId } from './colorScale.js'
import { flowStyleFor, shouldShowDashedOutline } from './flowStyles.js'
import floatingTooltip from '../floatingTooltip.js'

/** Minimum stroke width for pointer hit targets on thin flows (e.g. false negatives). */
const MIN_HIT_WIDTH = 12;

/**
 * @param {{ source: { name: string }, target: { name: string }, value: number }} d
 * @returns {string}
 */
function linkTooltipText(d) {
    const id = getLinkId(d);
    const count = Math.round(d.value).toLocaleString();
    switch (id) {
        case "diseased--test-positive":
            return "True positive: " + count + " — has the disease, tests positive";
        case "diseased--test-negative":
            return "False negative: " + count + " — has the disease, tests negative (missed!)";
        case "healthy--test-positive":
            return "False positive: " + count + " — healthy, tests positive";
        case "healthy--test-negative":
            return "True negative: " + count + " — healthy, tests negative";
        default:
            return d.source.name + " → " + d.target.name + ": " + count + " people";
    }
}

/**
 * @param {{ emitHighlight: Function, emitUnhighlight: Function }} handlers
 * @returns {{ mouseover: Function, mousemove: Function, mouseout: Function }}
 */
function linkHoverHandlers(handlers) {
    return {
        mouseover: function (d) {
            handlers.emitHighlight(getLinkId(d));
            floatingTooltip.show(linkTooltipText(d), d3.event.clientX, d3.event.clientY);
        },
        mousemove: function () {
            floatingTooltip.move(d3.event.clientX, d3.event.clientY);
        },
        mouseout: function (d) {
            handlers.emitUnhighlight(getLinkId(d));
            floatingTooltip.hide();
        },
    };
}

/**
 * Bind and draw sankey links colored by test outcome.
 * @param {Object} linksEl - d3 selection for links group
 * @param {Object} sankey - d3 sankey layout
 * @param {Array} graphLinks - graph links
 * @param {{ emitHighlight: Function, emitUnhighlight: Function }} handlers
 */
export function addLinks(linksEl, sankey, graphLinks, handlers) {
    const path = sankey.link();
    const hover = linkHoverHandlers(handlers);

    const links = linksEl.selectAll(".link").data(graphLinks, getLinkId);

    links.enter()
        .append("path")
        .attr("class", function (d) { return "link " + getLinkId(d); })
        .style("fill", "none")
        .style("pointer-events", "none");

    links.exit().remove();

    links
        .attr("d", path)
        .style("stroke", function (d) {
            return flowStyleFor(getLinkId(d)).stroke;
        })
        .style("stroke-opacity", function (d) {
            return flowStyleFor(getLinkId(d)).opacity;
        })
        .style("stroke-width", function (d) {
            return Math.max(0, d.dy);
        })
        .sort(function (a, b) {
            return b.dy - a.dy;
        });

    // Dashed outline after solids so thin flows stay visible on top
    // (always for false negatives; for true positives only when too thin)
    const outlines = linksEl.selectAll(".link-outline").data(
        graphLinks.filter(shouldShowDashedOutline),
        getLinkId
    );

    outlines.enter()
        .append("path")
        .attr("class", function (d) { return "link-outline " + getLinkId(d); })
        .style("fill", "none")
        .style("pointer-events", "none");

    outlines.exit().remove();

    outlines
        .attr("d", path)
        .style("stroke", function (d) {
            return flowStyleFor(getLinkId(d)).dashedOutline.color;
        })
        .style("stroke-width", function (d) {
            return Math.max(0, d.dy) + 2.5;
        })
        .style("stroke-dasharray", function (d) {
            return flowStyleFor(getLinkId(d)).dashedOutline.dasharray;
        })
        .style("stroke-opacity", 0.9)
        // Raise outlines above solid ribbons in paint order
        .each(function () {
            this.parentNode.appendChild(this);
        });

    // Wider transparent stroke so thin flows (esp. false negatives) are hoverable
    const hits = linksEl.selectAll(".link-hit").data(graphLinks, getLinkId);

    hits.enter()
        .append("path")
        .attr("class", function (d) { return "link-hit " + getLinkId(d); })
        .style("fill", "none")
        .style("stroke", "transparent")
        .on("mouseover", hover.mouseover)
        .on("mousemove", hover.mousemove)
        .on("mouseout", hover.mouseout);

    hits.exit().remove();

    hits
        .attr("d", path)
        .style("stroke-width", function (d) {
            return Math.max(MIN_HIT_WIDTH, d.dy);
        })
        // Thin flows on top so their hit area is not stolen by neighbors
        .sort(function (a, b) {
            return a.dy - b.dy;
        })
        .each(function () {
            this.parentNode.appendChild(this);
        });
}
