
// abstract base class
export default class ParticleGeom {

    createPointCloud(sceneParams, particlesData) {
    }

    renderPointCloud(sceneParams, particlesData) {
        const scale = sceneParams.particleSize;
        const thickness = sceneParams.atmosphereThickness;
        const rad = sceneParams.globeRadius;

        const childObjects = this.pointCloud.children;

        for ( let i = 0; i < childObjects.length; i ++ ) {
            const object = childObjects[i];

            object.visible = i < sceneParams.particleCount;
            if (object.visible) {
                const pt = particlesData.getPoint(i).getSpherePosition(rad, thickness);

                object.position.x = pt.x;
                object.position.y = pt.y;
                object.position.z = pt.z;

                object.scale.x = scale;
                object.scale.y = scale;
                object.scale.z = scale;

                object.layers.enable( 1 );
            }
        }
    }
}