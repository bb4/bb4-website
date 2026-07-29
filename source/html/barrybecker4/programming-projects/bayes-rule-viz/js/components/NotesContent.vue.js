import diseaseConsts from './diseaseConsts.js'

export default {
    template: `
         <div id="notes">
              <h3>Understanding Bayesian Reasoning</h3>
              <div class="notes-content">
                  <div class="note-section">
                      <h4>Terminology</h4>
                      <ul>
                          <li>
                               <strong>
                                   <span class="prob-term"
                                         :class="{ highlighted: highlightedProbId === ids.PROB_D }"
                                         @mouseenter="emitHighlight(ids.PROB_D)"
                                         @mouseleave="emitUnhighlight">
                                       p(<span class="diseased">D</span>)
                                   </span>
                               </strong>:
                               prevalence, the base rate (prior).
                           </li>
                           <li>
                               <strong>
                                   <span class="prob-term"
                                         :class="{ highlighted: highlightedProbId === ids.PROB_D_GIVEN_POS }"
                                         @mouseenter="emitHighlight(ids.PROB_D_GIVEN_POS)"
                                         @mouseleave="emitUnhighlight">
                                       p(<span class="diseased">D</span>|<span class="positive">positive</span>)
                                   </span>
                               </strong>:
                               probability of having the disease given a positive test — what we want.
                           </li>
                           <li>
                               <strong>
                                   <span class="prob-term"
                                         :class="{ highlighted: highlightedProbId === ids.PROB_POS_GIVEN_D }"
                                         @mouseenter="emitHighlight(ids.PROB_POS_GIVEN_D)"
                                         @mouseleave="emitUnhighlight">
                                       p(<span class="positive">positive</span>|<span class="diseased">D</span>)
                                   </span>
                               </strong>:
                               test sensitivity = <span class="formula">{{testAccuracyPct}}</span>
                           </li>
                           <li>
                               <strong>
                                   <span class="prob-term"
                                         :class="{ highlighted: highlightedProbId === ids.PROB_NEG_GIVEN_H }"
                                         @mouseenter="emitHighlight(ids.PROB_NEG_GIVEN_H)"
                                         @mouseleave="emitUnhighlight">
                                       p(<span class="negative">negative</span>|<span class="healthy">H</span>)
                                   </span>
                               </strong>:
                               test specificity = <span class="formula">{{testAccuracyPct}}</span>
                           </li>
                           <li>
                               <strong>
                                   <span class="prob-term"
                                         :class="{ highlighted: highlightedProbId === ids.PROB_POS }"
                                         @mouseenter="emitHighlight(ids.PROB_POS)"
                                         @mouseleave="emitUnhighlight">
                                       p(<span class="positive">positive</span>)
                                   </span>
                               </strong>:
                               total probability of testing positive,
                               <span class="formula">{{testPositive.toLocaleString()}} / {{totalPopulation.toLocaleString()}} = {{probPositive}}</span>
                           </li>
                      </ul>
                  </div>

                  <div class="note-section">
                      <h4>Key Insights</h4>
                      <ul>
                           <li>For rare diseases with imperfect tests, a positive result often doesn't mean you likely have the disease.</li>
                           <li>The base rate (prevalence) of the disease significantly affects how we should interpret test results.</li>
                           <li>Test accuracy sets both sensitivity and specificity (assumed equal here), so it influences false positives and false negatives and changes the overlap region in the Venn diagram.</li>
                      </ul>
                  </div>
              </div>
          </div>`,

    props: {
        testPositive: { type: Number, required: true },
        totalPopulation: { type: Number, required: true },
        testAccuracy: { type: Number, required: true },
        highlightedProbId: { default: null },
    },

    data() {
        return {
            ids: {
                PROB_D: diseaseConsts.PROB_D,
                PROB_D_GIVEN_POS: diseaseConsts.PROB_D_GIVEN_POS,
                PROB_POS_GIVEN_D: diseaseConsts.PROB_POS_GIVEN_D,
                PROB_POS: diseaseConsts.PROB_POS,
                PROB_NEG_GIVEN_H: diseaseConsts.PROB_NEG_GIVEN_H,
            },
        };
    },

    methods: {
        emitHighlight(id) {
            this.$emit('highlight-prob', id);
        },
        emitUnhighlight() {
            this.$emit('unhighlight-prob');
        },
    },

    computed: {
        probPositive() {
            return diseaseConsts.format(this.testPositive / this.totalPopulation, 4);
        },
        testAccuracyPct() {
            return diseaseConsts.format(this.testAccuracy * 100, 1) + "%";
        },
    },
}
