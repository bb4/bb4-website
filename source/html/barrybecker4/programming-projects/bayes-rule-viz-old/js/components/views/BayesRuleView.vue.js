import diseaseConsts from '../diseaseConsts.js'

export default {

   template: `<div>
       <table class="bayes-rule-exp">
           <tr>
               <td rowspan="2" class="cell-nowrap">
                   p(<span class="diseased">D</span> | <span class="positive">positive</span>)&nbsp; = &nbsp;
               </td>
               <td class="numerator">
                   p(<span class="diseased">D</span>) &nbsp; p(<span class="positive">positive</span> | <span class="diseased">D</span>)
               </td>
               <td rowspan="2" class="cell-nowrap"> &nbsp; = &nbsp;</td>
               <td class="numerator"><span class="prob-diseased">
                   {{this.probDiseasedTxt}}</span> * <span class="prob-pos-given-diseased">{{this.probPositiveGivenDiseasedTxt}}</span>
               </td>
               <td rowspan="2" class="cell-nowrap"> &nbsp; = &nbsp;</td>
               <td rowspan="2" class="cell-grow">
                   <span class="prob-diseased-result">{{this.probDiseasedGivenPositiveTxt}}</span>&nbsp; chance that you are infected.
                   <span class="prob-diseased-worry" :class="worryAttrs.level">{{this.worryAttrs.howMuch}}</span>
               </td>
           </tr>
           <tr>
               <td class="upper_line">p(<span class="positive">positive</span>)</td>
               <td class="upper_line"><span class="prob-positive">{{this.probPositiveTxt}}</span></td>
           </tr>
       </table>
   </div>`,

   props: {
     graph: {},
     totalPopulation: 0,
   },

   computed: {
       numPositiveAndDiseased: function() {
           return this.graph.links[1].value;
       },
       numPositiveAndHealthy: function() {
           return this.graph.links[2].value;
       },
       numDiseased: function() {
           return this.graph.links[0].value + this.numPositiveAndDiseased;
       },
       numPositive: function() {
           return this.numPositiveAndDiseased + this.numPositiveAndHealthy;
       },
       probPositiveGivenDiseased: function() {
           return this.numPositiveAndDiseased / this.numDiseased;
       },
       probDiseasedTxt: function() {
           return diseaseConsts.format(this. numDiseased / this.totalPopulation, 5)
       },
       probPositiveTxt: function() {
           return diseaseConsts.format(this.numPositive / this.totalPopulation, 4);
       },
       probPositiveGivenDiseasedTxt: function() {
           return diseaseConsts.format(this.probPositiveGivenDiseased, 4);
       },
       probDiseasedGivenPositive: function() {
          return (100 * this.numDiseased * this.probPositiveGivenDiseased) / this.numPositive;
      },
       probDiseasedGivenPositiveTxt: function() {
           return diseaseConsts.format(this.probDiseasedGivenPositive, 2) + "%  ";
       },
       worryAttrs: function() {
           let howMuch = "Panic!!!";
           let level = "worry-high";
           let prob = this.probDiseasedGivenPositive;

           if (prob <= 5) {
               howMuch = "Don't worry at all!";
               level = "worry-low";
           }
           else if (prob <= 10) {
               howMuch = "Don't worry.";
               level = "worry-low";
           }
           else if (prob <= 15) {
               howMuch = "Perhaps you should worry a little...";
               level = "worry-medium";
           }
           else if (prob <= 20) {
               howMuch = "Be concerned, but don't panic.";
               level = "worry-medium";
           }
           else if (prob <= 50) {
               howMuch = "You should worry.";
               level = "worry-medium";
           }
           else if (prob <= 80) {
               howMuch = "Yes, you should be very worried.";
               level = "worry-high";
           }
           return { howMuch, level };
       }
   }
}
