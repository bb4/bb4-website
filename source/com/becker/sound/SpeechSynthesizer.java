/*  Java Speech Synthesizer
 *  (C) LOTONtech Limited 2001
 */
package com.becker.sound;

import com.becker.common.Assert;
import com.becker.ui.GUIUtil;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.StringTokenizer;

public class SpeechSynthesizer
{
    private SourceDataLine line = null;

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
    public void sayPhoneWords( String[] words )
    {
        for ( int i = 0; i < words.length; i++ ) {
            sayPhoneWord( words[i] );
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

        while ( st.hasMoreTokens() ) {
            // -- construct a file name for the allophone --
            String thisPhoneFile = st.nextToken();
            thisPhoneFile = "com/becker/sound/allophones/" + thisPhoneFile + ".au";

            // -- get the data from the file --
            byte[] thisSound = getSound( thisPhoneFile );
            //Assert.isTrue(thisSound.length>0, "Invalid sound file: "+thisPhoneFile);

            if ( previousSound != null ) {
                // -- merge the previous allophone with this one if we can --
                int mergeCount = 0;
                if ( previousSound.length >= 500 && thisSound.length >= 500 ) mergeCount = 500;
                for ( int i = 0; i < mergeCount; i++ ) {
                    previousSound[previousSound.length - mergeCount + i]
                            = (byte) ((previousSound[previousSound.length - mergeCount + i] + thisSound[i]) / 2);
                }

                // -- play the previous allophone --
                playSound( previousSound );

                // -- set the truncated current allophone as previous --
                byte[] newSound = new byte[thisSound.length - mergeCount];
                for ( int ii = 0; ii < newSound.length; ii++ )
                    newSound[ii] = thisSound[ii + mergeCount];
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
        if ( line != null ) line.drain();
        try {
            Thread.sleep( 100 );
        } catch (Exception e) {
        }
    }

    /*
     * This method plays a sound sample.
     */
    private void playSound( byte[] data )
    {
        if ( data.length > 0 ) line.write( data, 0, data.length );
    }

    /*
     * This method reads the file for a single allophone and
     * contructs a byte vector.
     */
    private byte[] getSound( String sPath )
    {
        try {
            //URL url = SpeechSynthesizer.class.getResource( fileName );
            URL url = GUIUtil.getURL( sPath );
            AudioInputStream stream = AudioSystem.getAudioInputStream( url );
            AudioFormat format = stream.getFormat();

            // -- convert an ALAW/ULAW sound to PCM for playback --
            if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                    (format.getEncoding() == AudioFormat.Encoding.ALAW) ) {
                AudioFormat tmpFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        format.getSampleSizeInBits() * 2,
                        format.getChannels(),
                        format.getFrameSize() * 2,
                        format.getFrameRate(),
                        true );

                stream = AudioSystem.getAudioInputStream( tmpFormat, stream );
                format = tmpFormat;
            }

            DataLine.Info info = new DataLine.Info( Clip.class,
                                                    format,
                                                    ((int) stream.getFrameLength() * format.getFrameSize()));

            if ( line == null ) {
                // -- output line not instantiated yet --
                // -- can we find a suitable kind of line? --
                DataLine.Info outInfo = new DataLine.Info( SourceDataLine.class,
                        format );
                if ( !AudioSystem.isLineSupported( outInfo ) ) {
                    System.out.println( "Line matching " + outInfo + " not supported." );
                    throw new Exception( "Line matching " + outInfo + " not supported." );
                }

                // -- open the source data line (the output line) --
                line = (SourceDataLine) AudioSystem.getLine( outInfo );
                line.open( format, 50000 );
                line.start();
            }

            // -- some size calculations --
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;

            byte[] data = new byte[bufferLengthInBytes];

            // -- read the data bytes and count them --
            int numBytesRead = 0;
            if ((numBytesRead = stream.read(data)) != -1) {
                int numBytesRemaining = numBytesRead;
            }

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
}
