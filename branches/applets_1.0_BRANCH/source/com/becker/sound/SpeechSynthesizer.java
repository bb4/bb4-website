/*  Java Speech Synthesizer
 *  (C) LOTONtech Limited 2001
 */
package com.becker.sound;

import com.becker.ui.GUIUtil;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * See http://www.javaworld.com/javaworld/jw-08-2001/jw-0817-javatalk.html?page=1
 * 
 * The speech engine works by concatenating short sound samples that represent the smallest units of human -- in this case English -- speech. Those sound samples, called allophones, are labeled with a one-, two-, or three-letter identifier. Some identifiers are obvious and some not so obvious, as you can see from the phonetic representation of the word "hello." 

h -- sounds as you would expect 
e -- sounds as you would expect 
l -- sounds as you would expect, but notice that I've reduced a double "l" to a single one 
oo -- is the sound for "hello," not for "bot," and not for "too" 

Here is a list of the available allophones:

a -- as in cat 
b -- as in cab 
c -- as in cat 
d -- as in dot 
e -- as in bet 
f -- as in frog 
g -- as in frog 
h -- as in hog 
i -- as in pig 
j -- as in jig 
k -- as in keg 
l -- as in leg 
m -- as in met 
n -- as in begin 
o -- as in not 
p -- as in pot 
r -- as in rot 
s -- as in sat 
t -- as in sat 
u -- as in put 
v -- as in have 
w -- as in wet 
y -- as in yet 
z -- as in zoo 

aa -- as in fake 
ay -- as in hay 
ee -- as in bee 
ii -- as in high 
oo -- as in go 

bb -- variation of b with different emphasis 
dd -- variation of d with different emphasis 
ggg -- variation of g with different emphasis 
hh -- variation of h with different emphasis 
ll -- variation of l with different emphasis 
nn -- variation of n with different emphasis 
rr -- variation of r with different emphasis 
tt -- variation of t with different emphasis 
yy -- variation of y with different emphasis 

ar -- as in car 
aer -- as in care 
ch -- as in which 
ck -- as in check 
ear -- as in beer 
er -- as in later 
err -- as in later (longer sound) 
ng -- as in feeding 
or -- as in law 
ou -- as in zoo 
ouu -- as in zoo (longer sound) 
ow -- as in cow 
oy -- as in boy 
sh -- as in shut 
th -- as in thing 
dth -- as in this 
uh -- variation of u 
wh -- as in where 
zh -- as in Asian 
 */
public class SpeechSynthesizer
{
    private SourceDataLine line = null;
    
    /** delay in millis between words. */
    private static final int DELAY_BETWEEN_WORDS = 150;

    /*
     * This method speaks a phonetic word specified on the command line.
     */
    public static void main( String args[] )
    {
        SpeechSynthesizer player = new SpeechSynthesizer();
        if ( args.length > 0 ) player.sayPhoneWord( args[0] );
        System.exit( 0 );
    }
    
     /*
     * This method speaks the given phonetic words.
     */
    public void sayText( String text )
    {        
        sayPhoneWords( text.split(" "));
    }


    /*
     * This method speaks the given phonetic words.
     */
    public void sayPhoneWords( String[] words )
    {
        for (final String newVar : words) {
            sayPhoneWord(newVar);
        }
    }

    /*
     * This method speaks the given phonetic word.
     */
    public void sayPhoneWord( String word )
    {
        // -- set up a dummy byte array for the previous sound --
        byte[] previousSound = null;

        // -- split the input string into separate allophones --
        StringTokenizer st = new StringTokenizer( word, "|", false );

        //System.out.println("about to say: "+ word);
        while ( st.hasMoreTokens() ) {
            // -- construct a file name for the allophone --
            String thisPhoneFile = st.nextToken();
            if (thisPhoneFile.equals(",") || thisPhoneFile.equals(".")) {
                if (thisPhoneFile.equals(",")) {
                    pause(180);
                }
                else {
                    pause(680);
                }
                continue;
            }
            thisPhoneFile = "com/becker/sound/allophones/" + thisPhoneFile + ".au";

            // -- get the data from the file --
            byte[] thisSound = getSound( thisPhoneFile );
            assert (thisSound.length > 0) : "Invalid sound file: "+thisPhoneFile;

            if ( previousSound != null ) {
                // -- merge the previous allophone with this one if we can --
                int mergeCount = 0;
                if ( previousSound.length >= 500 && thisSound.length >= 500 )  {
                    mergeCount = 500;
                }
                for ( int i = 0; i < mergeCount; i++ ) {
                    previousSound[previousSound.length - mergeCount + i]
                            = (byte) ((previousSound[previousSound.length - mergeCount + i] + thisSound[i]) / 2);
                }

                // -- play the previous allophone --
                playSound( previousSound );

                // -- set the truncated current allophone as previous --
                byte[] newSound = new byte[thisSound.length - mergeCount];
                for ( int ii = 0; ii < newSound.length; ii++ ) {
                    newSound[ii] = thisSound[ii + mergeCount];
                }
                previousSound = newSound;
            }
            else
                previousSound = thisSound;
        }

        // -- play the final sound and drain the sound channel --
        playSound( previousSound );
        drain();
    }

    /*
     * This method drains the sound channel.
     */
    private void drain()
    {
        if ( line != null ) 
        {
            // this used to be just drain, but I added flush to make it work post java 1.5
            line.drain();
            //System.out.println("draining fp=" + line.getFramePosition() + " info=" + line.getLineInfo());       
            pause(90);
            line.flush();
        }
        pause(DELAY_BETWEEN_WORDS);
    }
    
    private void pause(int delay) {
        try {
            Thread.sleep( delay );
        } catch (Exception e) {
            e.printStackTrace();            
        }
    }

    /*
     * This method plays a sound sample.
     */
    private void playSound( byte[] data )
    {
        if (data == null) return;
        if ( data.length > 0 ) line.write( data, 0, data.length );
    }

    /*
     * This method reads the file for a single allophone and constructs a byte vector.
     */
    private byte[] getSound( String sPath )
    {
        try {
            //URL url = SpeechSynthesizer.class.getResource( fileName );
            //System.out.println("getSound sPath=" + sPath);
            URL url = GUIUtil.getURL( sPath );
            AudioInputStream stream = AudioSystem.getAudioInputStream( url );
            AudioFormat format = stream.getFormat();

            // -- convert an ALAW/ULAW sound to PCM for playback --
            if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                    (format.getEncoding() == AudioFormat.Encoding.ALAW) ) {
                AudioFormat tmpFormat = createAudioFormat(format);
                stream = AudioSystem.getAudioInputStream( tmpFormat, stream );
                format = tmpFormat;
            }

            if ( line == null ) {
                line = createDataLine(format);
            }

            // -- some size calculations --
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() >> 3;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;

            byte[] data = new byte[bufferLengthInBytes];

            // -- read the data bytes and count them --
            int numBytesRead = 0;
            numBytesRead = stream.read(data);

            byte maxByte = 0;

            // -- truncate the byte array to the correct size --
            byte[] newData = new byte[numBytesRead];
            for ( int i = 0; i < numBytesRead; i++ ) {
                newData[i] = data[i];
                if ( newData[i] > maxByte ) maxByte = newData[i];
            }

            return newData;
        } catch (Exception e) {
            System.out.println( "Something wrong with "+sPath );
            e.printStackTrace();
            return new byte[0];
        }
    }

    private AudioFormat createAudioFormat(AudioFormat format) {
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                format.getSampleRate(),
                format.getSampleSizeInBits() << 1,
                format.getChannels(),
                format.getFrameSize() << 1,
                format.getFrameRate(),
                true );
    }

    private SourceDataLine createDataLine(AudioFormat format) throws Exception {
        SourceDataLine dataLine;
        // -- output line not instantiated yet --
        // -- can we find a suitable kind of line? --
        DataLine.Info outInfo = new DataLine.Info( SourceDataLine.class, format );
        if ( !AudioSystem.isLineSupported( outInfo ) ) {
            System.out.println( "Line matching " + outInfo + " not supported." );
            throw new Exception( "Line matching " + outInfo + " not supported." );
        }

        // -- open the source data line (the output line) --
        dataLine = (SourceDataLine) AudioSystem.getLine( outInfo );
        dataLine.open( format, 50000 );
        dataLine.start();
        return dataLine;
    }
}
