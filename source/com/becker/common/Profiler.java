package com.becker.common;

import com.becker.common.util.Util;

import java.util.*;

/**
 * Use this class to get performance numbers for your application
 * in order to eliminate bottlenecks.
 *
 * @author Barry Becker
 */
public class Profiler
{
    private static final String INDENT = "    ";

    private final Map<String,ProfilerEntry> hmEntries_ = new HashMap<String,ProfilerEntry>();
    private final List<ProfilerEntry> topLevelEntries_ = new LinkedList<ProfilerEntry>();
    private boolean enabled_ = true;
    private static ILog logger_ = null;

    /**
     * Default constructor.
     */
    public Profiler() {}

    /**
     * add a top level entry.
     * @param name of the top level entry
     */
    public void add(String name)  {
        ProfilerEntry e = new ProfilerEntry(name);
        topLevelEntries_.add(e);
        hmEntries_.put(name, e);
    }

    /**
     * add an entry into the profiler entry hierarchy.
     * @param name
     * @param parent entry above us.
     */
    public void add(String name, String parent)  {
        ProfilerEntry par = hmEntries_.get(parent);
        assert par!=null : "invalid parent: "+parent;
        ProfilerEntry e = new ProfilerEntry(name);
        par.addChild(e);
        hmEntries_.put(name, e);
    }

    /**
     * @param name the entry for whom we are to start the timing.
     */
    public void start(String name)  {
         if (!enabled_) return;
         ProfilerEntry p = hmEntries_.get(name);
         p.start();
     }

    /**
     * @param name the entry for which we are to stop the timing and increment the total time.
     */
     public void stop(String name) {
         if (!enabled_) return;
         ProfilerEntry p = hmEntries_.get(name);
         p.stop();
     }

    /**
     * reset all the timing numbers to 0
     */
    public void resetAll()  {
        for (ProfilerEntry entry : topLevelEntries_) {
            entry.resetAll();
        }
    }

    /**
     * pretty print all the performance statistics.
     */
    public void print() {
        if (!enabled_) return;
        for (ProfilerEntry entry : topLevelEntries_) {
            entry.print("");
        }
    }

    public String toString(ILog log) {
        StringBuilder bldr = new StringBuilder();
        for (ProfilerEntry p : topLevelEntries_) {
            bldr.append(p.toString(log));
        }
        return bldr.toString();
    }

    public void setEnabled(boolean enable) {
        enabled_ = enable;
    }

    public void setLogger(ILog logger) {
        logger_ = logger;
    }


    /**
     * internal calss that represents the timing numbers for a names region of the code.
     */
    protected static class ProfilerEntry {

        // the name of this profiler entry
        private final String name_;
        private long startTime_ = 0;
        // the total time used by this named code section while the app was running
        private long totalTime_ = 0;
        private final List<ProfilerEntry> children_ = new LinkedList<ProfilerEntry>();

        protected ProfilerEntry(String name)
        {
            name_ = name;
        }

        protected void addChild(ProfilerEntry child)
        {
            children_.add(child);
        }

        protected void start()
        {
            startTime_ = System.currentTimeMillis();
        }

        protected void stop()
        {
            totalTime_ += System.currentTimeMillis() - startTime_;
        }

        protected long getTime()
        {
            return totalTime_;
        }


        protected void resetAll()
        {
            totalTime_ = 0;
            for (ProfilerEntry p : children_) {
                p.resetAll();
            }
        }

        protected void print(String indent)
        {
            print(indent, logger_);
        }

        protected void print(String indent, ILog logger)
        {
            double seconds = (double)totalTime_/1000.0;
            String text = indent+ "Time for "+name_+" : "+ Util.formatNumber(seconds) +" seconds";
            if (logger==null)
                System.out.println(text);
            else
                 logger.println(text);
            Iterator childIt = children_.iterator();

            long totalChildTime = 0;
            while (childIt.hasNext()) {
                ProfilerEntry p = (ProfilerEntry)childIt.next();
                totalChildTime += p.getTime();
                p.print(indent+INDENT);
            }
            assert (totalChildTime <= 1.1 * totalTime_ ): "The sum of the child times("+totalChildTime
                    +") cannot be greater than the parent time ("+totalTime_+").";
        }

        public String toString(ILog log)
        {
            log.setDestination(ILog.LOG_TO_STRING);
            StringBuilder bldr = new StringBuilder();
            log.setStringBuilder(bldr);
            print(INDENT, log);
            return bldr.toString();
        }
    }
}
