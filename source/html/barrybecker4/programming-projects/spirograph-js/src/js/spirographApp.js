import createControls from './controls.js';
import spirographRenderer from './spirographRenderer.js';

const sketch = p => {
  let MAX_SINES = 50  ; // how many of these things can we do at once?
  let sines = new Array(MAX_SINES); // an array to hold all the current angles
  let oldTrace = false;

  let rad; // an initial radius value for the central sine
  const controls = createControls(p);

  let oldFrameRate = 0;

  p.setup = function() {
    p.createCanvas(100, 100);
    p.windowResized();
    p.background(204); // clear the screen

    p.textSize(14);
    p.noStroke();

    for (let i = 0; i < sines.length; i++) {
      sines[i] = p.PI; // start EVERYBODY facing NORTH
    }
  }

  p.windowResized = function() {
    p.resizeCanvas(p.windowWidth - 30, p.windowHeight - 40);
    rad = p.height / 4; // compute radius for central circle
  }

  p.draw = function() {

    // add slider for radMin, radMax, radNumPoints
    // slider for color
    // add toggle for show lines - draw lines as arc?
    // toggle for middle circle
    const params = {
      rad,
      speed: controls.speedSlider.value() / 20000,
      numSines: controls.numSinesSlider.value(),
      ratio:  1.0 + controls.speedRatioSlider.value() / 10,
      radScale: controls.radScaleSlider.value() / 100,
      radRatio: controls.radRatioSlider.value() / 100,

      radOffsetMin: controls.radOffsetMinSlider.value() / 100,
      radOffsetMax: controls.radOffsetMaxSlider.value() / 100,
      radOffsetSamples: controls.radOffsetSamplesSlider.value(),
      trace: controls.traceModeCB.checked(),
      showBaseCircle: controls.showBaseCircleCB.checked(),
    }

    if (!params.trace) {
      p.stroke(0, 255); // black pen
      p.noFill(); // don't fill
      p.background(204); // clear screen if showing geometry
    }
    if (params.trace != oldTrace) {
      //p.stroke(2, 155);
      p.background(50);
      oldTrace = params.trace;
    }
    controls.drawSliderLabels();

    const fr = controls.frameRateSlider.value();
    if (fr != oldFrameRate) {
      p.frameRate(fr);
      oldFrameRate = fr;
    }

    spirographRenderer.render(p, sines, params);
  }

}


new p5(sketch);
