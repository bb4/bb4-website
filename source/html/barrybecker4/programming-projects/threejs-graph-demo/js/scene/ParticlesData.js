import * as THREE from 'https://unpkg.com/three@0.123.0/build/three.module.js';
import Point from './Point.js'

export default class ParticlesData {

    constructor(maxParticleCount, r) {
        const data = [];
        const positions = new Float32Array( maxParticleCount * 3 );
        const randomSpeed = () => - 1 + Math.random() * 2;
        const randomPos = () => Math.random() * r - r / 2;

        // randomly positioned points
        for ( let i = 0; i < maxParticleCount; i ++ ) {

            const ii = 3 * i;
            positions[ ii ] = randomPos();
            positions[ ii + 1 ] = randomPos();
            positions[ ii + 2 ] = randomPos();

            // add it to the geometry
            data.push({
                velocity: new THREE.Vector3( randomSpeed(), randomSpeed(), randomSpeed()),
                numConnections: 0,
            });
        }

        this.r = r;
        this.rHalf = r / 2.0;
        this.data = data;
        this.positions = positions;
    }

    getPoint(i) {
        const positions = this.positions;
        const ii = 3 * i;
        return new Point(positions[ii], positions[ii + 1], positions[ii + 2]);
    }

    updatePositionAndVelocity(i, speedFactor) {
        const positions = this.positions;
        const velocity = this.data[i].velocity;
        const ii = 3 * i;
        const rHalf = this.rHalf;

        positions[ ii ] += velocity.x * speedFactor;
        positions[ ii + 1 ] += velocity.y * speedFactor;
        positions[ ii + 2 ] += velocity.z * speedFactor;

        if ( positions[ ii + 1 ] < -rHalf || positions[ ii + 1 ] > rHalf )
            velocity.y = -velocity.y;

        if ( positions[ ii ] < -rHalf || positions[ ii ] > rHalf )
            velocity.x = -velocity.x;

        if ( positions[ ii + 2 ] < -rHalf || positions[ ii + 2 ] > rHalf )
            velocity.z = -velocity.z;
    }

    // Note: this is N^2 in number of particles
    connectPoints(linesData, sceneParams) {
        let idx = 0;
        let numConnected = 0;
        const particleCount = sceneParams.particleCount;
        const limitConnections = sceneParams.limitConnections;
        const maxConnections = sceneParams.maxConnections;
        const globeRadius = sceneParams.globeRadius;
        const atmThickness = sceneParams.atmosphereThickness;

        for ( let i = 0; i < particleCount; i ++ ) {
            this.data[i].numConnections = 0;
        }

        const speedFactor = sceneParams.particleSpeed / 10;

        for ( let i = 0; i < particleCount; i ++ ) {

            // get the particle
            const particleData = this.data[i];
            this.updatePositionAndVelocity(i, speedFactor);

            if (limitConnections && particleData.numConnections >= maxConnections)
                continue;

            const positions = linesData.positions;
            const colors = linesData.colors;
            for ( let j = i + 1; j < particleCount; j ++ ) {

                const particleDataB = this.data[j];
                if (limitConnections && particleDataB.numConnections >= maxConnections )
                    continue;

                const pti = this.getPoint(i).getSpherePosition(globeRadius, atmThickness);
                const ptj = this.getPoint(j).getSpherePosition(globeRadius, atmThickness);

                const dist = pti.distanceTo(ptj);

                if ( dist < sceneParams.minDistance ) {

                    particleData.numConnections++;
                    particleDataB.numConnections++;

                    const alpha = 1.0 - dist / sceneParams.minDistance;
                    idx = linesData.updatePositionsAndColors(idx, pti, ptj, alpha);

                    numConnected++;
                }
            }
        }
        return numConnected;
    }
}