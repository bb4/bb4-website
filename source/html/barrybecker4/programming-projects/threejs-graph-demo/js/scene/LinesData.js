import Point from './Point.js';

export default class LinesData {

    constructor(maxParticleCount) {
        // this allocates a lot of memory
        this.maxLines = maxParticleCount * maxParticleCount;

        this.positions = new Float32Array( this.maxLines * 3 );
        this.colors = new Float32Array( this.maxLines * 3 );
    }

    updatePositionsAndColors(idx, pti, ptj, alpha) {
        const positions = this.positions;
        positions[ idx ] = pti.x;
        positions[ idx + 1 ] = pti.y;
        positions[ idx + 2 ] = pti.z;

        positions[ idx + 3 ] = ptj.x;
        positions[ idx + 4 ] = ptj.y;
        positions[ idx + 5 ] = ptj.z;

        for (let i = 0; i < 6; i++) {
            this.colors[ idx + i ] = alpha;
        }

        return idx + 6;
    }

    getPoint(idx) {
        const i = 3 * idx;
        return new Point(this.positions[i], this.positions[i + 1], this.positions[i + 2]);
    }
}