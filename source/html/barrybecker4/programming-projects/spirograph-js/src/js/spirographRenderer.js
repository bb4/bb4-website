
const alpha = 70; // how opaque is the tracing system

export default {
  render,
}

function render(p, sines, params) {

  p.push(); // start a transformation matrix
  p.translate(p.width / 2, p.height / 2); // move to middle of screen

  for (let i = 0; i < params.numSines; i++) {
    let erad = 0; // radius for small "point" within circle... this is the 'pen' when tracing
    // setup for tracing
    if (params.trace) {
      const colorNum = 250 * (p.float(i) / params.numSines);
      p.stroke(250 - colorNum, (120 + colorNum) % 255, colorNum, alpha);
      //p.fill(0, 0, 255, alpha / 2);
      erad = 5.0 * (1.0 - p.float(i) / params.numSines); // pen width will be related to which sine
      p.strokeWeight(erad); // Make the points 10 pixels in
    }
    let radius = params.radScale * params.rad / (i * params.radRatio + 1); // radius for circle itself
    p.rotate(sines[i]); // rotate circle
    if (!params.trace) p.ellipse(0, 0, radius * 2, radius * 2); // if we're simulating, draw the sine

    if (!(!params.showBaseCircle && i === 0 ))
      drawDot(p, radius, erad, params);

    p.translate(0, radius); // move into position for next sine
    // update angle based on fundamental
    sines[i] = (sines[i] + (params.speed + (params.speed * i * params.ratio))) % p.TWO_PI;
  }

  p.pop(); // pop down final transformation
}

function drawDot(p, radius, erad, params) {
  p.push(); // go up one level

  if (params.trace) {
    const startRad = radius + params.radOffsetMin * radius;
    const stopRad = radius + params.radOffsetMax * radius;
    const delta = stopRad - startRad;
    const numSamples = params.radOffsetSamples;
    const inc = numSamples == 1 ? startRad : delta / (numSamples - 1);
    for (let i = 0; i < numSamples; i++) {
      p.translate(0, inc); // move to sine edge
      //p.ellipse(0, 0, erad, erad);
      p.point(0, 0);
    }
  } // draw with rad if tracing
  else {
    p.translate(0, radius); // move to sine edge
    p.ellipse(0, 0, 5, 5);
  } // draw a little circle

  p.pop(); // go down one level
}

function drawArc(p, radius, sinValue, erad) {
  const v = sinValue; // + p.PI / 2;
  p.arc(0, 0, radius, radius, v - 0.2, v + 0.2);
}