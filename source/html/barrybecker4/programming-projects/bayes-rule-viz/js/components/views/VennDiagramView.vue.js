import { initVennSvg } from './venn/initSvg.js'
import { computeVennLayout, applyVennLayout } from './venn/layout.js'
import { doHighlight, doUnhighlight } from './venn/highlight.js'
import diseaseConsts from '../diseaseConsts.js'

export default {

    template: `
        <div id="venn-diagram-view">
            <h3 class="chart-panel-title">Venn: how much overlap is there really?</h3>
            <div class="chart-svg-wrap" ref="svgWrap"></div>
            <div v-if="enlargeNote" class="venn-enlarge-note">{{ enlargeNote }}</div>
        </div>`,

    props: {
        stats: { type: Object, required: true },
        totalPopulation: { type: Number, required: true },
        highlightedId: { type: String, default: null },
    },

    data() {
        return {
            enlargeNote: null,
        };
    },

    mounted() {
        this._tooltipCounts = {
            totalPopulation: this.totalPopulation,
            numDiseased: 0,
            numPositive: 0,
            numPositiveAndDiseased: 0,
            numPositiveAndHealthy: 0,
            testNegButDiseased: 0,
            pctDiseasedGivenPositive: "0",
        };
        this.init();
        this.render();
    },

    beforeDestroy() {
        window.removeEventListener('resize', this.render);
    },

    watch: {
        stats: {
            handler() {
                this.render();
            },
            deep: true,
        },
        highlightedId(newId, oldId) {
            if (oldId) {
                doUnhighlight(oldId);
            }
            if (newId) {
                doHighlight(newId);
            }
        },
    },

    methods: {
        init() {
            const handlers = {
                emitHighlight: (id) => this.$emit('highlight', id),
                emitUnhighlight: (id) => this.$emit('unhighlight', id),
                getTooltipCounts: () => this._tooltipCounts,
            };
            initVennSvg(this.$refs.svgWrap, this.totalPopulation, handlers);
            window.addEventListener('resize', this.render);
        },

        render() {
            const wrap = this.$refs.svgWrap;
            if (!wrap) {
                return;
            }
            const chartWidth = wrap.clientWidth;
            const chartHeight = wrap.clientHeight;

            const svg = d3.select(wrap).select("svg")
                .attr("width", chartWidth)
                .attr("height", chartHeight);

            const layout = computeVennLayout(this.stats, this.totalPopulation, chartWidth, chartHeight);
            applyVennLayout(svg, layout, chartHeight);

            this._tooltipCounts = layout._tooltipCounts;

            if (layout.diseasedEnlarged) {
                const n = Math.round(layout.numDiseased).toLocaleString();
                const pct = diseaseConsts.format(100 * layout.numDiseased / this.totalPopulation, 2);
                this.enlargeNote = "Diseased circle shown enlarged — actual share is only " +
                    n + " people (" + pct + "%).";
            } else {
                this.enlargeNote = null;
            }
        },
    },
}
