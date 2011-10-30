/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.snake.geometry;

import javax.vecmath.Vector2d;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 *  A snake edge (line geometry) is modeled as a spring to simulate muscles.
 *
 *  @author Barry Becker
 */
public class Edge {

    // the 2 endpoints defining the edge endpoints
    private Particle firstParticle_;
    private Particle secondParticle_;

    private Line2D.Double segment_ = null;

    /** constants related the the spring for this edge segment  */
    private static final double K = 0.8; // default  .6

    /** the damping coefficient */
    private static final double D = 1.2; // default

    private static final double EPS = 0.00000017287893433;

    /** the spring constant K (large K = stiffer) */
    private double k_;
    /** damping constant  */
    private double damping;

    /** the resting magnitude of the spring  */
    private double restingLength_;

    /** usually the effectiveLength_ is the same as restingLength_ except when muscular contraction are happening  */
    private double effectiveLength_;

    /** the current magnitude of the spring */
    private double length_;

    /** these act like temporary variables for some calculations avoiding many object constructions */
    private final Vector2d direction_ = new Vector2d();
    private final Vector2d force_ = new Vector2d();
    private final Vector2d damping_ = new Vector2d();

    /**
     * Constructor - assumes defaults for the spring constant and damping
     * @param p1 particle that anchors one end of the
     * @param p2 particle that anchors the other end of the edge
     */
    Edge( Particle p1, Particle p2 ) {
        commonInit( p1, p2, K, D );
    }

    public Particle getFirstParticle() {
        return firstParticle_;
    }

    public Particle getSecondParticle() {
        return secondParticle_;
    }

    public double getRestingLength() {
        return restingLength_;
    }

    public double getLength() {
        return length_;
    }


    private void commonInit( Particle p1, Particle p2, double k, double d ) {
        segment_ = new Line2D.Double( p1.x, p1.y, p2.x, p2.y );
        firstParticle_ = p1;
        secondParticle_ = p2;

        k_ = k;
        damping = d;
        restingLength_ = firstParticle_.distance( secondParticle_ );
        effectiveLength_ = restingLength_;
        length_ = restingLength_; // current magnitude
    }

    /**
     *  This method simulates the contraction or expansion of a muscle
     *  the rest magnitude restingLength_ is effectively changed by the contraction factor.
     *  @param contraction the amount that the spring model for the edge is contracting
     */
    public void setContraction( double contraction )  {
        if (contraction <= 0) {
            throw new IllegalArgumentException( "Error contraction <=0 = "+contraction );
            //contraction = EPS;
        }
        effectiveLength_ = contraction * restingLength_;
    }

    /**
     * The force that the spring edge exerts is k_ times the vector (L-l)p2-p1
     * where L is the resting magnitude of the edge and l is the current magnitude
     * The official formula in proceedings of Siggraph 1988 p169 is
     *   k(L-l) - D* dl/dt
     * @return the computed force exerted on the particle.
     */
    public Vector2d getForce() {
        force_.set( secondParticle_ );
        force_.sub( firstParticle_ );
        direction_.set( force_ );
        direction_.normalize();

        // adjust the force by the damping term
        damping_.set( secondParticle_.velocity );
        damping_.sub( firstParticle_.velocity );
        double d = damping * damping_.dot( direction_ );

        double halfEffectiveL = effectiveLength_ / 2.0;

        length_ = force_.length();
        // never let the force get too great or too small
        if ( length_ > 2.0 * effectiveLength_)
            force_.scale( (-k_ * (effectiveLength_ - length_) * (effectiveLength_ - length_) / effectiveLength_ - d) );
        else if ( length_ < halfEffectiveL ) {
            // prevent the springs from getting too compressed
            force_.scale( (k_ * (restingLength_ - length_) + k_ * 100000.0 * (halfEffectiveL - length_) / halfEffectiveL - d) );
        }
        else {
            //if (d>1.0)
            //   System.out.println("f="+k_*(effectiveLength_-length_)+" - d="+d);
            force_.scale( (k_ * (effectiveLength_ - length_) - d) );
        }

        return force_;
    }

    /**
     * A unit vector in the direction p2-p1
     */
    public Vector2d getDirection() {
        direction_.set( secondParticle_ );
        direction_.sub( firstParticle_ );
        direction_.normalize();
        return direction_;
    }

    public boolean intersects( Rectangle2D.Double rect ) {
        return segment_.intersects( rect );
    }

    /**
     * find the result of taking the dot product of this edge iwth another
     * @param edge to dot this edge with
     * @return the dot product
     */
    public double dot( Edge edge ) {
        return getDirection().dot( edge.getDirection() );
    }
}
