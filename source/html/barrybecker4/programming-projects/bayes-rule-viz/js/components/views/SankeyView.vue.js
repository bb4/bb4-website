import { addLinks } from './sankey/links.js'
import { addNodes } from './sankey/nodes.js'
import { FLOW_STYLES, LEGEND_ENTRIES, HIGHLIGHT_DIM_OPACITY } from './sankey/flowStyles.js'

const DURATION = 300;
const margin = { top: 10, right: 10, bottom: 10, left: 10 };

/** All four outcome flows use dim-siblings emphasis on hover. */
const EMPHASIS_LINK_IDS = new Set(Object.keys(FLOW_STYLES));

export default {

    template: `
        <div id="sankey-view">
            <h3 class="chart-panel-title">Sankey: where do the 100,000 people end up?</h3>
            <div class="chart-svg-wrap" ref="svgWrap"></div>
            <div class="sankey-legend">
                <div
                    v-for="entry in legendRows"
                    :key="entry.id"
                    class="sankey-legend-row"
                    :class="{ alert: entry.alert, highlighted: highlightedId === entry.id }"
                    @mouseenter="$emit('highlight', entry.id)"
                    @mouseleave="$emit('unhighlight', entry.id)">
                    <span
                        class="sankey-legend-dot"
                        :style="{ background: entry.dotColor, opacity: entry.dotOpacity }">
                    </span>
                    <span>
                        <strong>{{ entry.label }}: {{ entry.count }}</strong>
                        — {{ entry.describe }}
                    </span>
                </div>
            </div>
        </div>`,

    props: {
        graph: { type: Object, required: true },
        stats: { type: Object, required: true },
        highlightedId: { type: String, default: null },
    },

    computed: {
        legendRows() {
            return LEGEND_ENTRIES.map((entry) => {
                const style = FLOW_STYLES[entry.id];
                return {
                    id: entry.id,
                    label: entry.label,
                    describe: entry.describe,
                    alert: entry.alert,
                    count: Math.round(this.stats[entry.countKey]).toLocaleString(),
                    // Solid swatches so legend matches the shared hex (ignore ribbon opacity)
                    dotColor: style.stroke,
                    dotOpacity: 1,
                };
            });
        },
    },

    mounted() {
        this.init();
        this.render();
    },

    beforeDestroy() {
        window.removeEventListener('resize', this.render);
    },

    watch: {
        graph: {
            handler() {
                this.render();
            },
            deep: true,
        },
        highlightedId(newId, oldId) {
            if (oldId) {
                this.doUnhighlight(oldId);
            }
            if (newId) {
                this.doHighlight(newId);
            }
        },
    },

    methods: {
        init() {
            const wrap = this.$refs.svgWrap;
            const svg = d3.select(wrap).append("svg")
                .append("g")
                .attr("transform",
                    "translate(" + margin.left + "," + margin.top + ")");

            this._sankey = d3.sankey()
                .nodeWidth(15)
                .nodePadding(30);
            this._linksEl = svg.append("g").attr("class", "links");
            this._nodesEl = svg.append("g").attr("class", "nodes");

            window.addEventListener('resize', this.render);
        },

        render() {
            if (!this._sankey) {
                return;
            }
            const wrap = this.$refs.svgWrap;
            const chartWidth = wrap.clientWidth;
            const chartHeight = wrap.clientHeight;
            const width = chartWidth - margin.left - margin.right;
            const height = chartHeight - margin.top - margin.bottom;

            d3.select(wrap).select("svg")
                .attr("width", chartWidth)
                .attr("height", chartHeight);

            this._sankey
                .size([width, height])
                .nodes(this.graph.nodes)
                .links(this.graph.links)
                .layout(0);

            const handlers = {
                emitHighlight: (id) => this.$emit('highlight', id),
                emitUnhighlight: (id) => this.$emit('unhighlight', id),
            };

            addLinks(this._linksEl, this._sankey, this.graph.links, handlers);
            addNodes(this._nodesEl, this._sankey, this.graph.nodes, width);
        },

        doHighlight(id) {
            if (!EMPHASIS_LINK_IDS.has(id)) {
                return;
            }
            // Dim siblings so the focused flow reads clearly.
            d3.selectAll("#sankey-view path.link").transition().duration(DURATION)
                .style("stroke-opacity", HIGHLIGHT_DIM_OPACITY);
            d3.selectAll("#sankey-view path.link-outline").transition().duration(DURATION)
                .style("stroke-opacity", HIGHLIGHT_DIM_OPACITY);
            d3.select("#sankey-view path.link." + id).transition().duration(DURATION)
                .style("stroke-opacity", 1);
            d3.select("#sankey-view path.link-outline." + id).transition().duration(DURATION)
                .style("stroke-opacity", 1);
        },

        doUnhighlight(id) {
            if (!EMPHASIS_LINK_IDS.has(id)) {
                return;
            }
            d3.selectAll("#sankey-view path.link").each(function () {
                const el = d3.select(this);
                const classes = (el.attr("class") || "").split(/\s+/);
                const linkId = classes.find((c) => FLOW_STYLES[c]);
                const base = linkId && FLOW_STYLES[linkId] ? FLOW_STYLES[linkId].opacity : 0.7;
                el.transition().duration(DURATION).style("stroke-opacity", base);
            });
            d3.selectAll("#sankey-view path.link-outline").transition().duration(DURATION)
                .style("stroke-opacity", 0.9);
        },
    },
}
