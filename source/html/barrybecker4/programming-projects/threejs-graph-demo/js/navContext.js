import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';
import { OrbitControls } from 'https://unpkg.com/three@0.123.0/examples/jsm/controls/OrbitControls.js';
import { VRButton } from 'https://unpkg.com/three@0.123.0/examples/jsm/webxr/VRButton.js';
import Stats from 'https://cdnjs.cloudflare.com/ajax/libs/stats.js/r17/Stats.js';
import PickHelper from './PickHelper.js';
import createCamera from './createCamera.js';
import createScene from './createScene.js';

const ENABLE_VR = true;

// Camera frustum far clipping plane.
const FAR_CLIP = 4000;


export default async function(containerId) {

    const container = document.getElementById(containerId);
    const camera = createCamera();
    const scene = createScene();
    let sceneRoot = null;
    scene.add(camera); // needed if camera has light source
    const renderer = createRenderer(container);

    const pickHelper = new PickHelper(container);
    pickHelper.setPickLayer(1);
    const controls = createOrbitControls(camera, container);

    const stats = addPerformanceStatistics();

    if (ENABLE_VR) {
        addVRButton(renderer);
    }

    window.addEventListener('resize', onWindowResize, false );

    /* Gives error:
       'requestSession' on 'XRSystem': The requested session requires user activation
    const xrSession = await navigator.xr.requestSession('immersive-vr', {
        // 3DoF
        requiredFeatures: ['local-floor'],
        // 6 DoF
        optionalFeatures: ['bounded-floor']
    });
    console.log("xrSession: " + xrSession);

    let inputSources = xrSession.getInputSources();
    for (let inputSource of inputSources) {
        let inputPose = xrFrame.getInputPose(inputSource, this._referenceSpace);
        console.log("inputSrc: " + JSON.stringify(inputSource) + "\npose: " + JSON.stringify(inputPos));
    }*/

    const my = {};

    my.render = function() {
        stats.update();
        pickHelper.pick(sceneRoot, camera, controls);

        if (ENABLE_VR) {
            renderer.setAnimationLoop(function () {
                renderer.render(scene, camera);
            });
        } else {
            renderer.render(scene, camera);
        }
    }

    my.setSceneRoot = function(newSceneRoot) {
        if (sceneRoot) {
            scene.remove(sceneRoot)
        }
        sceneRoot = newSceneRoot;
        scene.add(newSceneRoot);
    }

    my.getScene = function() {
        return scene;
    }

    function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
    }

    return my;
};

// Show performance stats (like FPS). See https://github.com/mrdoob/stats.js/
function addPerformanceStatistics() {
    const stats = new Stats();
    stats.showPanel(0); // FPS
    document.body.appendChild( stats.dom );
    return stats;
}

// Add VR Button if enabled
function addVRButton(renderer) {
    document.body.appendChild( VRButton.createButton( renderer ) );
    renderer.xr.enabled = true;
}

function createOrbitControls(camera, container) {
    const controls = new OrbitControls(camera, container);
    controls.minDistance = 500;
    controls.maxDistance = FAR_CLIP - 500;
    return controls;
}

function createRenderer(canvas) {
    const renderer = new THREE.WebGLRenderer( { canvas, antialias: true } );
    renderer.setPixelRatio( window.devicePixelRatio );
    renderer.setSize( window.innerWidth, window.innerHeight );
    renderer.outputEncoding = THREE.sRGBEncoding;
    return renderer;
}
