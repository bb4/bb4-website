package com.becker.simulation.stock;

import com.becker.common.format.INumberFormatter;
import com.becker.common.format.ScaledFormatter;
import com.becker.simulation.common.DistributionSimulator;
import com.becker.simulation.common.SimulatorOptionsDialog;
import com.becker.ui.HistogramRenderer;


/**
 * Simluates the N stocks over M time periods.
 * There are other options that can be set before graphing the result of the simulation.
 *
 * @author Barry Becker
 */
public class StockSimulator extends DistributionSimulator {

    static final int DEFAULT_NUM_STOCKS = 1;
    static final int DEFAULT_NUM_TIME_PERIODS = 10;
    static final double DEFAULT_PERCENT_INCREASE = 0.6;
    static final double DEFAULT_PERCENT_DECREASE = 0.4;
    static final double DEFAULT_STARTING_VALUE = 10000;
    static final int DEFAULT_X_RESOLUTION = 2;
    static final boolean DEFAULT_USE_LOG_SCALE = false;
    static final boolean DEFAULT_USE_RANDOM_CHANGE = false;

    private static final int LABEL_WIDTH = 70;

    private int numStocks_ = DEFAULT_NUM_STOCKS;
    private int numTimePeriods_ = DEFAULT_NUM_TIME_PERIODS;
    private double percentIncrease_ = DEFAULT_PERCENT_INCREASE;
    private double percentDecrease_ = DEFAULT_PERCENT_DECREASE;
    private double startingValue_ = DEFAULT_STARTING_VALUE;
    private int xResolution_ = DEFAULT_X_RESOLUTION;
    private boolean useLogScale_ = DEFAULT_USE_LOG_SCALE;
    private boolean useRandomChange_ = DEFAULT_USE_RANDOM_CHANGE;

    private double xScale_;
    private double xLogScale_;


    public StockSimulator() {
        super("Stock Market Simulation");
        initHistogram();
    }

    // @@ make all these into StockOptions object. same for dice.
    public void setNumStocks(int numStocks) {
        numStocks_ = numStocks;
    }

    public void setNumTimePeriods(int numTimePeriods) {
        numTimePeriods_ = numTimePeriods;
        initHistogram();
    }

    public void setPercentIncrease(double percentIncrease) {
        percentIncrease_ = percentIncrease;
        initHistogram();
    }

    public void setPercentDecrease(double percentDecrease) {
        percentDecrease_ = percentDecrease;
    }

    public void setStartingValue(double startingValue) {
        startingValue_ = startingValue;
    }

    public void setXResolution(int xResolution) {
        xResolution_ = xResolution;
        initHistogram();
    }

    public void setLogScale(boolean useLogScale) {
        useLogScale_ = useLogScale;
        initHistogram();
    }

    public void setRandomChange(boolean useRandomChange) {
        useRandomChange_ = useRandomChange;
    }

    @Override
    protected void initHistogram() {
        double theoreticalMaximum = startingValue_ * Math.pow(1.0 + percentIncrease_, numTimePeriods_);
        xScale_ = Math.pow(10, Math.max(0, Math.log10(theoreticalMaximum) - xResolution_));
        xLogScale_ = 3 * xResolution_ * xResolution_;
        int maxX = normalizeSample(theoreticalMaximum);
        data_ = new int[maxX + 1];
        histogram_ = new HistogramRenderer(data_, 0);
        INumberFormatter fmtr =  useLogScale_ ?
                new ScaledFormatter(1.0/xLogScale_, true, true) :
                new ScaledFormatter(xScale_, false, true);
        histogram_.setFormatter(fmtr);
        histogram_.setMaxLabelWidth(LABEL_WIDTH);
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new StockOptionsDialog( frame_, this );
    }

    @Override
    protected int getXPositionToIncrement() {
        double sample = createSample();
        return normalizeSample(sample);
    }

    /**
     * @return value of a set of numStocks after numTimePeriods.
     */
    private double createSample() {

        double total = 0;
        for (int j = 0; j < numStocks_; j++) {
            total += calculateFinalStockPrice();
        }
        return total / numStocks_;
    }

    /**
     * @return final stock price for a single stock after numTimePeriods.
     */
    private double calculateFinalStockPrice() {

        double stockPrice = startingValue_;
        for (int i = 0; i < numTimePeriods_; i++) {
            double percentChange = Math.random() > 0.5 ? percentIncrease_ : -percentDecrease_;
            if (useRandomChange_)
                stockPrice *= (1.0 + Math.random() * percentChange);
            else
                stockPrice *= (1.0 + percentChange);
        }
        return stockPrice;
    }
    /**
     * Make the sample fix on the x axis.
     * @return x axis position
     */
    private int normalizeSample(double sample)  {

        int bucketX;
        if (useLogScale_) {
            bucketX = (int) (xLogScale_ * Math.max(0, Math.log10(sample)));
        } else {
            bucketX = (int) (sample / xScale_);  
        }
        return bucketX;
    }


    @Override
    protected String getFileNameBase()
    {
        return "stock";
    }

    public static void main( String[] args )
    {
        final StockSimulator sim = new StockSimulator();
        runSimulation(sim);
    }
}