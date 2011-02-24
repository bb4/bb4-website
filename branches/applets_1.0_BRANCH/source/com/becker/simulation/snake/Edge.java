package com.becker.simulation.snake;

import com.becker.common.ColorMap;

import javax.vecmath.Vector2d;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 *  A snake edge (line geometry) is modeled as a spring to simulate muscles.
 *
 *  @author Barry Becker
 */
final class Edge
{

    // the 2 endpoints defining the edge endpoints
    private Particle firstParticle_;
    private Particle secondParticle_;

    private Line2D.Double segment_ = null;

    // constants related the the spring for this edge segment
    private static final double K = 0.8; // default  .6
    // the damping coefficient
    private static final double D = 1.2; // default

    private static final double EPS = 0.00000017287893433;
    private static final double EDGE_SCALE = 3.0;

    // show the edge different colors depending on percentage stretched  ( one being 100% stretched)
    private static final double stretchVals_[] = {0.3, 0.9, 1.0, 1.1, 3.0};
    private static final Color stretchColors_[] = {
        new Color( 255, 0, 0, 200 ), new Color( 230, 120, 57, 250 ), new Color( 50, 90, 60, 250 ), new Color( 70, 120, 210, 200 ), new Color( 10, 10, 255, 100 )
    };
    private static final ColorMap stretchColorMap_ =
            new ColorMap( stretchVals_, stretchColors_ );

    // the spring constant K (large K = stiffer)
    private double k_;
    // damping constant
    private double d_;

    // the resting length of the spring
    private double L_;
    // usually the effectiveL_ is the same as L_ excepte when muscular contraction are happenning
    private double effectiveL_;

    // the current length of the spring
    private double l_;

    // these act like temporary variables for some calculations avoiding many object constructions
    private final Vector2d direction_ = new Vector2d();
    private final Vector2d force_ = new Vector2d();
    private final Vector2d damping_ = new Vector2d();

    /**
     *  Constructor   - assumes defaults for the spring constant and damping
     *  @param p1 particle that anchors one end of the
     *  @param p2 particle that anchors the other end of the edge
     */
    protected Edge( Particle p1, Particle p2 )
    {
        commonInit( p1, p2, K, D );
    }

    /**
     *  Constructor - everything defined explicitly
     */
    protected Edge( Particle p1, Particle p2, double k, double d )
    {
        commonInit( p1, p2, k, d );
    }

    private void commonInit( Particle p1, Particle p2, double k, double d )
    {
        segment_ = new Line2D.Double( p1.x, p1.y, p2.x, p2.y );
        firstParticle_ = p1;
        secondParticle_ = p2;

        k_ = k;
        d_ = d;
        L_ = firstParticle_.distance( secondParticle_ );
        effectiveL_ = L_;
        l_ = L_; // current length
    }

    /**
     *  This method simulates the contraction or expansion of a muscle
     *  the rest length L_ is effectively changed by the contraction factor.
     *  @param contraction the amount that the spring model for the edge is contracting
     */
    public void setContraction( double contraction )
    {
        if (contraction <= 0) {
            System.out.println( "Error contraction<=0 = "+contraction );
            contraction = EPS;
        }
        effectiveL_ = contraction * L_;
    }

    /**
     * The force that the spring edge exerts is k_ times the vector (L-l)p2-p1
     * where L is the resting length of the edge and l is the current length
     * The official formula in proceedings of Siggraph 1988 p169 is
     *   k(L-l) - D* dl/dt
     */
    public Vector2d getForce()
    {
        force_.set( secondParticle_ );
        force_.sub( firstParticle_ );
        direction_.set( force_ );
        direction_.normalize();

        // adjust the force by the damping term
        damping_.set( secondParticle_.velocity );
        damping_.sub( firstParticle_.velocity );
        double d = d_ * damping_.dot( direction_ );
        //if (d>1.0)
        //  System.out.println("vel("+damping_.length()+")="+damping_+"   dir="+direction_);
        double halfEffectiveL = effectiveL_ / 2.0;

        l_ = force_.length();
        // never let the force get too great or too small
        if ( l_ > 2.0 * effectiveL_ )
            force_.scale( (-k_ * (effectiveL_ - l_) * (effectiveL_ - l_) / effectiveL_ - d) );
        else if ( l_ < halfEffectiveL ) {
            // prevent the springs from getting too compressed
            force_.scale( (k_ * (L_ - l_) + k_ * 100000.0 * (halfEffectiveL - l_) / halfEffectiveL - d) );
            //System.out.println("! force="+force_);
        }
        else {
            //if (d>1.0)
            //   System.out.println("f="+k_*(effectiveL_-l_)+" - d="+d);
            force_.scale( (k_ * (effectiveL_ - l_) - d) );
        }

        return force_;
    }

    /**
     * A unit vector in the direction p2-p1
     */
    public Vector2d getDirection()
    {
        direction_.set( secondParticle_ );
        direction_.sub( firstParticle_ );
        direction_.normalize();
        return direction_;
    }

    public boolean intersects( Rectangle2D.Double rect )
    {
        return segment_.intersects( rect );
    }

    /**
     * find the result of taking the dot product of this edge iwth another
     * @param edge to dot this edge with
     * @return the dotproduce
     */
    public double dot( Edge edge )
    {
        return getDirection().dot( edge.getDirection() );
    }

    /**
     *  @return true if the point lies on the wall
     */
    public boolean intersects( double i, double j, double eps )
    {
        return segment_.intersects( i, j, eps, eps );
    }

    public void render( Graphics2D g )
    {
        //g.setColor(EDGE_COLOR);
        g.setColor( stretchColorMap_.getColorForValue( l_ / L_ ) );

        BasicStroke stroke = new BasicStroke( (float) (EDGE_SCALE * (0.1 + L_) / (l_ + EPS)) );
        g.setStroke( stroke );
        try {
            g.drawLine( (int) firstParticle_.x, (int) firstParticle_.y, (int) secondParticle_.x, (int) secondParticle_.y );
        }
        catch (Exception e) {
            System.out.println( "PRException!" );
            e.printStackTrace();
        }
    }

}
