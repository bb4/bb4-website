import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';

const NUM_ANIM_FRAMES = 10;

export default class PickHelper {

    constructor(container) {
        this.container = container;
        this.raycaster = new THREE.Raycaster();
        this.pickedObject = null;
        this.pickedObjectSavedColor = null;
        this.pickPosition = null;
        this.oldMousePosition = null;



        // Need to use pointerup/down because OrbitControls call preventDefault on mouseup/down.
        window.addEventListener('pointerdown', evt => {
            this.oldMousePosition = { x: evt.clientX, y: evt.clientY };
        });
        window.addEventListener('pointerup', evt => {
            if (evt.clientX === this.oldMousePosition.x && evt.clientY === this.oldMousePosition.y) {
                this.pickedPosition(evt);
            }
        });
    }

    pick(sceneRoot, camera, controls) {
        if (!this.pickPosition) return;

        // restore the color if there is a picked object
        if (this.pickedObject) {
            this.pickedObject.material.color.setHex(this.pickedObjectSavedColor);
            this.pickedObject = undefined;
        }

        // cast a ray through the frustum
        this.raycaster.setFromCamera(this.pickPosition, camera);
        // get the list of objects the ray intersected
        const intersectedObjects = this.raycaster.intersectObjects(sceneRoot.children, true);

        if (intersectedObjects.length) {
            // pick the first object. It's the closest one
            this.pickedObject = intersectedObjects[0].object;

            const material = this.pickedObject.material;
            this.pickedObjectSavedColor = material.color.getHex();

            this.navigateToSelected(this.pickedObject.position, camera, controls);

            material.color.setHex(0xFFFF77);
        }
        else {
            this.navigateToSelected(new THREE.Vector3( 0, 0, 0 ), camera, controls);
        }
        this.pickPosition = undefined;
    }

    setPickLayer(layer) {
        this.raycaster.layers.set( layer );
    }

    navigateToSelected(position, camera, controls) {

        var startQ = camera.quaternion.clone();
        camera.lookAt(position);
        var endQ = camera.quaternion.clone();

        // Pause between two consecutive animation frames
        var deltaT = 1;
        function interpolate(acc) {
              if (acc >= 1) return;

              THREE.Quaternion.slerp(startQ, endQ, camera.quaternion, acc);
              setTimeout(function() {
                  interpolate(acc + (1 / NUM_ANIM_FRAMES));
              }, deltaT);
        }
        interpolate(1 / NUM_ANIM_FRAMES);

        controls.target = position;
    }

    static getCanvasRelativePosition(event, container) {
        const rect = container.getBoundingClientRect();
        return {
            x: (event.clientX - rect.left) * container.width  / rect.width,
            y: (event.clientY - rect.top ) * container.height / rect.height,
        };
    }

    pickedPosition(event) {
        const pos = PickHelper.getCanvasRelativePosition(event, this.container);
        this.pickPosition = {
            x: (pos.x / this.container.width ) *  2 - 1,
            y: (pos.y / this.container.height) * -2 + 1,  // flipped y
        }
    }

}


