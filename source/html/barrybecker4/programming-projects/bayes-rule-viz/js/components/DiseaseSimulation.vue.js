import diseaseConsts from './diseaseConsts.js'
import { computeStats, buildGraphLinks, cloneNodes } from './simulationStats.js'
import BayesRuleView from './views/BayesRuleView.vue.js'
import SankeyView from './views/SankeyView.vue.js'
import VennDiagramView from './views/VennDiagramView.vue.js'
import NotesContent from './NotesContent.vue.js'


/**
 * Interactively visualize disease testing using Bayes' rule.
 * https://www.mathsisfun.com/data/probability-false-negatives-positives.html
 */
export default {

    components: {
        BayesRuleView,
        SankeyView,
        VennDiagramView,
        NotesContent,
    },

    template: `
        <div class="disease-simulation">
            <div class="inputs">
                <div class="input-line">
                    <div class="input-header">
                        <label for="probability-diseased-slider">
                            Disease prevalence in population —
                            <span class="prob-term"
                                  :class="{ highlighted: highlightedProbId === probIds.PROB_D }"
                                  @mouseenter="onHighlightProb(probIds.PROB_D)"
                                  @mouseleave="onUnhighlightProb">
                                p(<span class="diseased">D</span>)
                            </span>
                        </label>
                        <span id="probability-diseased" class="slider-value">{{initialPctDiseased}}%</span>
                    </div>
                    <div id="probability-diseased-slider" class="slider"></div>
                </div>
                <div class="input-line">
                    <div class="input-header">
                        <label for="test-accuracy-slider">
                            Test accuracy (sensitivity &amp; specificity) —
                            <span class="prob-term"
                                  :class="{ highlighted: highlightedProbId === probIds.PROB_POS_GIVEN_D }"
                                  @mouseenter="onHighlightProb(probIds.PROB_POS_GIVEN_D)"
                                  @mouseleave="onUnhighlightProb">
                                p(<span class="positive">positive</span>|<span class="diseased">D</span>)
                            </span>
                        </label>
                        <span id="test-accuracy" class="slider-value">{{initialTestAccuracy}}%</span>
                    </div>
                    <div id="test-accuracy-slider" class="slider"></div>
                </div>
            </div>
            
            <bayes-rule-view
                :stats="stats"
                :total-population="totalPopulation"
                :highlighted-prob-id="highlightedProbId"
                @highlight-prob="onHighlightProb"
                @unhighlight-prob="onUnhighlightProb">
            </bayes-rule-view>
            
            <div class="visualization-container">
                <sankey-view
                    :graph="graph"
                    :stats="stats"
                    :highlighted-id="highlightedId"
                    @highlight="onHighlight"
                    @unhighlight="onUnhighlight">
                </sankey-view>
                <venn-diagram-view
                    :stats="stats"
                    :total-population="totalPopulation"
                    :highlighted-id="highlightedId"
                    @highlight="onHighlight"
                    @unhighlight="onUnhighlight">
                </venn-diagram-view>
            </div>
            
            <notes-content
                :test-positive="stats.testPositive"
                :total-population="totalPopulation"
                :test-accuracy="testAccuracy"
                :highlighted-prob-id="highlightedProbId"
                @highlight-prob="onHighlightProb"
                @unhighlight-prob="onUnhighlightProb">
            </notes-content>
        </div>`,

    props: {
        totalPopulation: { type: Number, required: true },
        initialPctDiseased: { type: Number, required: true },
        initialTestAccuracy: { type: Number, required: true },
    },

    data() {
        const probDiseased = this.initialPctDiseased / 100.0;
        const testAccuracy = this.initialTestAccuracy / 100.0;
        const stats = computeStats({
            probDiseased,
            testAccuracy,
            totalPopulation: this.totalPopulation,
        });
        return {
            graph: {
                nodes: cloneNodes(diseaseConsts.NODES),
                links: buildGraphLinks(stats),
            },
            probDiseased,
            testAccuracy,
            highlightedId: null,
            highlightedProbId: null,
            probIds: {
                PROB_D: diseaseConsts.PROB_D,
                PROB_POS_GIVEN_D: diseaseConsts.PROB_POS_GIVEN_D,
            },
        };
    },

    computed: {
        stats() {
            return computeStats({
                probDiseased: this.probDiseased,
                testAccuracy: this.testAccuracy,
                totalPopulation: this.totalPopulation,
            });
        },
    },

    mounted() {
        this.init();
    },

    methods: {
        init() {
            this.initializeInputSection(this.initialPctDiseased, this.initialTestAccuracy);
            this.syncGraphLinks();
        },

        /**
         * Show two sliders that allow changing the incidence and accuracy.
         */
        initializeInputSection(initialPctDiseased, initialTestAccuracy) {
            const probDiseasedSlider = $("#probability-diseased-slider");
            const testAccuracySlider = $("#test-accuracy-slider");

            // Using integer values to avoid rounding problems at the max value
            probDiseasedSlider.slider({
                value: Math.log10(initialPctDiseased),
                min: -2,
                max: 1.0,
                step: 0.1,
                height: "10px",
                slide: this.getSliderChangedHandler(
                    "#probability-diseased",
                    this.pctDiseasedConverter,
                    (uiValue) => { this.probDiseased = Math.pow(10, uiValue) / 100.0; }
                ),
                stop: this.clearThumbTip,
            });

            testAccuracySlider.slider({
                value: initialTestAccuracy * 10,
                min: 800,
                max: 999,
                step: 1,
                slide: this.getSliderChangedHandler(
                    "#test-accuracy",
                    this.testAccuracyConverter,
                    (uiValue) => { this.testAccuracy = (uiValue / 10) / 100.0; }
                ),
                stop: this.clearThumbTip,
            });
        },

        pctDiseasedConverter(sliderValue) {
            return diseaseConsts.format(Math.pow(10, sliderValue), 2) + "%";
        },

        testAccuracyConverter(sliderValue) {
            return sliderValue / 10 + "%";
        },

        /**
         * @param sliderEl jquery selector for slider value label
         * @param convert function used to map slider value to display text
         * @param applyValue function that updates reactive state from raw slider value
         * @returns {Function} slider changed callback
         */
        getSliderChangedHandler(sliderEl, convert, applyValue) {
            const vm = this;
            return function (event, ui) {
                const value = convert(ui.value);
                $(sliderEl).text(value);

                const tooltip = '<div class="tooltip"><div class="tooltip-inner">' + value
                    + '</div><div class="tooltip-arrow"></div></div>';
                $(sliderEl + "-slider").find('.ui-slider-handle').html(tooltip);

                applyValue(ui.value);
                vm.syncGraphLinks();
            };
        },

        syncGraphLinks() {
            Vue.set(this.graph, 'links', buildGraphLinks(this.stats));
        },

        clearThumbTip() {
            $("#probability-diseased-slider").find('.ui-slider-handle').empty();
            $("#test-accuracy-slider").find('.ui-slider-handle').empty();
        },

        onHighlight(id) {
            this.highlightedId = id;
        },

        onUnhighlight() {
            this.highlightedId = null;
        },

        onHighlightProb(id) {
            this.highlightedProbId = id;
        },

        onUnhighlightProb() {
            this.highlightedProbId = null;
        },
    },
}
