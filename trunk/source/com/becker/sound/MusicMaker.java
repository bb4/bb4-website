package com.becker.sound;


import javax.sound.midi.*;

/**
 *  This class provides a convenient way to access the Java Sound API.
 *  You can use it to generate all kinds of musical effects
 *
 *  @author Barry Becker
 */
public class MusicMaker
{

    private Sequencer sequencer;
    private Sequence sequence;
    private Synthesizer synthesizer;
    private Instrument instruments_[];
    private MidiChannel midiChannels_[];
    private MidiChannel channel_;    // current channel
    //private Track track;

    // list all the instruments here so they are easy to choose from
    // This is just my favorite subset of those that are available
    public static final String PIANO = "Piano";
    public static final String ORGAN = "Reed Organ";
    public static final String VIOLIN = "Violin";
    public static final String ORCHESTRA_HIT = "Orchestra Hit";
    public static final String TRUMPET = "Trumpet";
    public static final String TROMBONE = "Trombone";
    public static final String TUBA = "Tuba";
    public static final String WHISTLE = "Whistle";
    public static final String WARM_PAD = "Warm Pad";
    public static final String GOBLINS = "Goblins";
    public static final String DROPS = "Echo Drops";
    public static final String SITAR = "Sitar";
    public static final String SHAMISEN = "Shamisen";
    public static final String STEEL_DRUMS = "Steel Drums";
    public static final String WOODBLOCK = "Woodblock";
    public static final String TAIKO_DRUM = "Taiko Drum";
    public static final String GUITAR_FRET = "Guitar Fret Noise";
    public static final String SEASHORE = "Seashore";
    public static final String BIRD = "Bird";
    public static final String TELEPHONE = "Telephone";
    public static final String HELICOPTER = "Helicopter";
    public static final String APPLAUSE = "Applause";
    public static final String GUNSHOT = "Gunshot";
    public static final String WATERY_GLASS = "Watery Glass";
    public static final String DROPLET2 = "Droplet 2";
    public static final String AIRPLANE = "Digi-Dodo";
    public static final String ALARM = "Alarm";
    public static final String BUZZY_HIT = "buzzy hit";
    public static final String GATE_TONE = "Gate-tone";      // futuristic dorr
    public static final String DUB_KICK = "dub_kick";        // spacy
    public static final String FIFTH_PULSE = "5th Pulse";
    public static final String RICOCHET = "Ricochet Pad";
    public static final String ANAL_SEQ = "Analog Sequence";
    public static final String GOBLINS2 = "Goblins 2";
    public static final String VID_GAME2 = "Video Game 2";
    public static final String HOVERBUG = "Hoverbug";
    public static final String WHIPPED = "Whipped";
    public static final String POING = "Poing";
    public static final String METAL_SRYAY = "Metal Spray";
    public static final String FLYBY2 = "FlyBy 2";
    public static final String COSMIC_RAY = "Cosmic Ray";
    public static final String SCIENCE_TOM = "ScienceTom";
    public static final String SLO_LASER = "SloLaser";
    public static final String SCRATCH = "itchy-scratch";
    public static final String SCRATCH2 = "itchy-scratch2";
    public static final String METALLIC_SNARE = "hi_metallic_snare";    // reverb
    public static final String CHEM_TONE = "chem-tone";


    public static final String[] favorites_ = {
            PIANO, ORGAN, VIOLIN,
            ORCHESTRA_HIT, TRUMPET, TROMBONE,
            TUBA, WHISTLE, WARM_PAD, GOBLINS,
            DROPS, SITAR, SHAMISEN, STEEL_DRUMS, WOODBLOCK,
            TAIKO_DRUM, GUITAR_FRET, SEASHORE, BIRD,
            TELEPHONE, HELICOPTER, APPLAUSE, GUNSHOT,
            WATERY_GLASS, DROPLET2, AIRPLANE, ALARM,
            BUZZY_HIT, GATE_TONE, DUB_KICK,
            FIFTH_PULSE, RICOCHET,
            ANAL_SEQ, GOBLINS2, VID_GAME2,
            HOVERBUG, WHIPPED, POING, METAL_SRYAY, FLYBY2,
            COSMIC_RAY, SCIENCE_TOM, SLO_LASER,
            SCRATCH, SCRATCH2, METALLIC_SNARE, CHEM_TONE
    };

    //------ Main method - for testing--------------------------------------------------------
    public static void main( String[] args )
    {

        MusicMaker m = new MusicMaker();
        SpeechSynthesizer speech = new SpeechSynthesizer();
        //speech.sayPhoneWord("y|ouu");

        for ( int i = 6; i < favorites_.length; i++ ) {
            // first set the instrument
            m.playNote(favorites_[i], 45, 0, 1, 100 );
            //speech.sayPhoneWord(favorites_[i]);
            System.out.println(i+ " "+favorites_[i] );


            // now play the instrument in a variety of ways
            for (int j=1; j<5; j++)
                m.playNote(10+j*20, 300, 800);
            for (int j=1; j<5; j++)
                m.playNote(10+j*20, 700, 1000);
            for (int j=1; j<10; j++)
                m.playNote(10+j*10, 50, 2000);
        }




    }

    //Construct the application
    public MusicMaker()
    {
        initSynthesizer();
    }

    public String[] getFavoriteSounds()
    {
        return favorites_;
    }

    protected void initSynthesizer()
    {
        try {
            if (synthesizer == null) {
                if ((synthesizer = MidiSystem.getSynthesizer()) == null) {
                    System.out.println("getSynthesizer() failed!");
                    return;
                }
            }
            synthesizer.open();
            sequencer = MidiSystem.getSequencer();
            sequence = new Sequence(Sequence.PPQ, 10);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }

        Soundbank sb = synthesizer.getDefaultSoundbank();


        /*
        try {
            if ( synthesizer == null ) {
                try {
                    MidiDevice.Info info[] = MidiSystem.getMidiDeviceInfo();
                    if ( info.length == 0 ) {
                        System.out.println( "no midi info available" );
                        return;
                    }
                    System.out.println( "Midi info for this computer=" + info[0] );
                    MidiDevice device = MidiSystem.getMidiDevice( info[0] );
                } catch (MidiUnavailableException e) {
                    e.printStackTrace();
                    return;
                }

                if ( (synthesizer = MidiSystem.getSynthesizer()) == null ) {
                    System.out.println( "getSynthesizer() failed!" );
                    return;
                }
            }
            synthesizer.open();
            //sequencer = MidiSystem.getSequencer();
            //sequence = new Sequence(Sequence.PPQ, 10);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        Soundbank sb = synthesizer.getDefaultSoundbank();
        */

        if ( sb != null ) {
            instruments_ = synthesizer.getDefaultSoundbank().getInstruments();
            synthesizer.loadInstrument( instruments_[0] );
        }
        else {
            System.out.println( "Error: no sound bank present on this system" );
            //Assert.exception( "no sound bank" );
        }

        midiChannels_ = synthesizer.getChannels();
        //System.out.println("num midi channels = "+midiChannels_.length);
        channel_ = midiChannels_[0];

        channel_.resetAllControllers();
        channel_.setChannelPressure( 128 );
        channel_.setPitchBend( 128 );
        channel_.controlChange( 91, 128 ); // reverb
        channel_.setMute( false );

        //channel_.allNotesOff();
    }

    public int getNumInstruments()
    {
        return instruments_.length;
    }

    /**
     * will play a note until stopped
     */
    public void startNote( int instrumentType, int instrumentSubType, int note,
                           int channelIndex, int velocity )
    {
        initChannel( instrumentType, instrumentSubType, channelIndex );
        startNote( note, velocity );
    }

    public void startNote( String instrument, int note,
                           int channelIndex, int velocity )
    {
        int i = getInstrumentIndex( instrument );
        if ( i >= 0 )
            startNote( i / 8, i % 8, note, channelIndex, velocity );
    }

    /**
     * lets you play a single note on one channel for a specified duration
     */
    public void playNote( int instrumentType, int instrumentSubType, int note,
                          int channelIndex, int duration, int velocity )
    {
        initChannel( instrumentType, instrumentSubType, channelIndex );
        playNote( note, duration, velocity );
    }

    /**
     * start/stop a note
     * @param note  the pitch (1-100) (20-70 reasonable)
     * @param velocity loudness/volume (0 - 1000) (Can't hear 100)
     */
    public void startNote( int note, int velocity )
    {
        channel_.noteOn( note, velocity );
    }

    public void stopNote( int note, int velocity )
    {
        channel_.noteOff( note, velocity );
    }

    /**
     * @param note  the pitch (1-100) (20-70 reasonable)
     * @param duration in milliseconds
     * @param velocity loudness/volume (0 - 1000) (Can't hear 100 for some sounds)
     */
    public void playNote( int note, int duration, int velocity )
    {
        assert (note>=0);
        if (note>127) {
            System.out.println( "note needs to be in the range 0-127 it is "+note );
            note=127;
        }
        try {
            //System.out.println(note+"  "+duration);
            channel_.noteOn( note, velocity );
            Thread.sleep( duration );
            channel_.noteOff( note, velocity );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getInstrumentName( int instrumentType, int instrumentSubType )
    {
        int i = instrumentSubType + 8 * instrumentType;
        return instruments_[i].getName();
    }

    public void playNote( String instrument, int note,
                          int channelIndex, int duration, int velocity )
    {
        int i = getInstrumentIndex( instrument );
        if ( i >= 0 )
            playNote( i / 8, i % 8, note, channelIndex, duration, velocity );
    }

    protected void initChannel( int instrumentType, int instrumentSubType,
                                int channelIndex )
    {
        int instrument = 8 * instrumentType + instrumentSubType;
        //System.out.println(instruments_[instrument].getName());

        synthesizer.loadInstrument( instruments_[instrument] );
        channel_ = midiChannels_[channelIndex];
        channel_.programChange( instrument );
    }

    protected int getInstrumentIndex( String instrument )
    {
        int i = 0;
        if ( instruments_ == null ) return -1;
        while ( i < instruments_.length && !(instruments_[i].getName().equals( instrument )) )
            i++;
        if ( i == instruments_.length ) {
            System.out.println( "not found ****:     " + instrument );
            return -1;
        }
        else
            return i;
    }

    public void stopAllSounds()
    {
        if ( channel_ == null ) return;
        channel_.allNotesOff();
    }
}
