import diseaseConsts from '../diseaseConsts.js'

export default {

    template: `<div id="bayes-rule-view">
       <table class="bayes-rule-exp" align="center" cellpadding="0" cellspacing="0">
           <tr>
               <td rowspan="2" nowrap="nowrap">
                   <span class="prob-term"
                         :class="{ highlighted: highlightedProbId === ids.PROB_D_GIVEN_POS }"
                         @mouseenter="emitHighlight(ids.PROB_D_GIVEN_POS)"
                         @mouseleave="emitUnhighlight">
                       p(<span class="diseased">D</span>|<span class="positive">positive</span>)
                   </span>&nbsp; = &nbsp;
               </td>
               <td class="numerator">
                   <span class="prob-term"
                         :class="{ highlighted: highlightedProbId === ids.PROB_D }"
                         @mouseenter="emitHighlight(ids.PROB_D)"
                         @mouseleave="emitUnhighlight">
                       p(<span class="diseased">D</span>)
                   </span>
                   &nbsp;
                   <span class="prob-term"
                         :class="{ highlighted: highlightedProbId === ids.PROB_POS_GIVEN_D }"
                         @mouseenter="emitHighlight(ids.PROB_POS_GIVEN_D)"
                         @mouseleave="emitUnhighlight">
                       p(<span class="positive">positive</span>|<span class="diseased">D</span>)
                   </span>
               </td>
               <td rowspan="2" nowrap="nowrap"> &nbsp; = &nbsp;</td>
               <td class="numerator">
                   <span class="prob-diseased prob-term"
                         :class="{ highlighted: highlightedProbId === ids.PROB_D }"
                         @mouseenter="emitHighlight(ids.PROB_D)"
                         @mouseleave="emitUnhighlight">
                       {{probDiseasedTxt}}
                   </span>
                   *
                   <span class="prob-pos-given-diseased prob-term"
                         :class="{ highlighted: highlightedProbId === ids.PROB_POS_GIVEN_D }"
                         @mouseenter="emitHighlight(ids.PROB_POS_GIVEN_D)"
                         @mouseleave="emitUnhighlight">
                       {{probPositiveGivenDiseasedTxt}}
                   </span>
               </td>
               <td rowspan="2" nowrap="nowrap"> &nbsp; = &nbsp;</td>
               <td rowspan="2" width="100%">
                   <span class="prob-diseased-result prob-term"
                         :class="{ highlighted: highlightedProbId === ids.PROB_D_GIVEN_POS }"
                         :style='{color: worryAttrs.color}'
                         @mouseenter="emitHighlight(ids.PROB_D_GIVEN_POS)"
                         @mouseleave="emitUnhighlight">
                       {{probDiseasedGivenPositiveTxt}}
                   </span>
                   chance you're actually infected.
                   <span class="prob-diseased-worry" :style='{color: worryAttrs.color}'>{{worryAttrs.howMuch}}</span>
               </td>
           </tr>
           <tr>
               <td class="upper_line">
                   <span class="prob-term"
                         :class="{ highlighted: highlightedProbId === ids.PROB_POS }"
                         @mouseenter="emitHighlight(ids.PROB_POS)"
                         @mouseleave="emitUnhighlight">
                       p(<span class="positive">positive</span>)
                   </span>
               </td>
               <td class="upper_line">
                   <span class="prob-positive prob-term"
                         :class="{ highlighted: highlightedProbId === ids.PROB_POS }"
                         @mouseenter="emitHighlight(ids.PROB_POS)"
                         @mouseleave="emitUnhighlight">
                       {{probPositiveTxt}}
                   </span>
               </td>
           </tr>
       </table>
   </div>`,

    props: {
        stats: { type: Object, required: true },
        totalPopulation: { type: Number, required: true },
        highlightedProbId: { default: null },
    },

    data() {
        return {
            ids: {
                PROB_D: diseaseConsts.PROB_D,
                PROB_D_GIVEN_POS: diseaseConsts.PROB_D_GIVEN_POS,
                PROB_POS_GIVEN_D: diseaseConsts.PROB_POS_GIVEN_D,
                PROB_POS: diseaseConsts.PROB_POS,
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
        numPositiveAndDiseased() {
            return this.stats.testPositiveAndDiseased;
        },
        numPositiveAndHealthy() {
            return this.stats.testPositiveButHealthy;
        },
        numDiseased() {
            return this.stats.testNegButDiseased + this.numPositiveAndDiseased;
        },
        numPositive() {
            return this.numPositiveAndDiseased + this.numPositiveAndHealthy;
        },
        probPositiveGivenDiseased() {
            return this.numPositiveAndDiseased / this.numDiseased;
        },
        probDiseasedTxt() {
            return diseaseConsts.format(this.numDiseased / this.totalPopulation, 5);
        },
        probPositiveTxt() {
            return diseaseConsts.format(this.numPositive / this.totalPopulation, 4);
        },
        probPositiveGivenDiseasedTxt() {
            return diseaseConsts.format(this.probPositiveGivenDiseased, 4);
        },
        probDiseasedGivenPositive() {
            return (100 * this.numDiseased * this.probPositiveGivenDiseased) / this.numPositive;
        },
        probDiseasedGivenPositiveTxt() {
            return diseaseConsts.format(this.probDiseasedGivenPositive, 2) + "%";
        },
        worryAttrs() {
            const prob = this.probDiseasedGivenPositive;
            if (prob < 5) {
                return {
                    howMuch: "Don't worry at all — very unlikely you have it.",
                    color: "#2e7d32",
                };
            }
            if (prob < 30) {
                return {
                    howMuch: "Worth taking seriously — ask your doctor about a confirmatory test.",
                    color: "#f9a825",
                };
            }
            return {
                howMuch: "High concern — follow up with your doctor promptly.",
                color: "#c62828",
            };
        },
    },
}
