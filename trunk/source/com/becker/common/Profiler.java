package com.becker.common;

import com.becker.common.util.Util;
import com.becker.ui.*;

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
    private static Log logger_ = null;

    /**
     * Default constructor.
     */
    public Profiler()
    {}

    /**
     * add a top level entry.
     * @param name of the top level entry
     */
    public void add(String name)
    {
        ProfilerEntry e = new ProfilerEntry(name);
        topLevelEntries_.add(e);
        hmEntries_.put(name, e);
    }

    /**
     * add an entry into the profiler entry hierarchy.
     * @param name
     * @param parent entry above us.
     */
    public void add(String name, String parent)
    {
        ProfilerEntry par = hmEntries_.get(parent);
        assert par!=null : "invalid parent: "+parent;
        ProfilerEntry e = new ProfilerEntry(name);
        par.addChild(e);
        hmEntries_.put(name, e);
    }

    /**
     * @param name the entry for whom we are to start the timing.
     */
    public void start(String name)
     {
         if (!enabled_) return;
         ProfilerEntry p = hmEntries_.get(name);
         p.start();
     }

    /**
     * @param name the entry for which we are to stop the timing and increment the total time.
     */
     public void stop(String name)
     {
         if (!enabled_) return;
         ProfilerEntry p = hmEntries_.get(name);
         p.stop();
     }


    /**
     * reset all the timing numbers to 0
     */
    public void resetAll()
    {
       Iterator childIt = topLevelEntries_.iterator();
        while (childIt.hasNext()) {
            ProfilerEntry p = (ProfilerEntry)childIt.next();
            p.resetAll();
        }
    }

    /**
     * pretty print all the performance statistics.
     */
    public void print()
    {
        if (!enabled_) return;
        Iterator childIt = topLevelEntries_.iterator();
        while (childIt.hasNext()) {
            ProfilerEntry p = (ProfilerEntry)childIt.next();
            p.print("");
        }
    }

    public void setEnabled(boolean enable)
    {
        enabled_ = enable;
    }

    public void setLogger(Log logger)
    {
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
            Iterator childIt = children_.iterator();
            while (childIt.hasNext()) {
                ProfilerEntry p = (ProfilerEntry)childIt.next();
                p.resetAll();
            }
        }

        protected void print(String indent)
        {
            double seconds = (double)totalTime_/1000.0;
            String text = indent+ "Time for "+name_+" : "+ Util.formatNumber(seconds) +" seconds";
            if (logger_==null)
                System.out.println(text);
            else                
                 logger_.println(text);
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

    }
}
