package AZ;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class SoundManager
{
	/**
	 * Lejátszik egy zenét
	 * @param file A zene file neve
	 */
	public static void PlaySound(String file)
	{

		// getAudioInputStream() also accepts a File or InputStream
		try
		{
			Clip clip1 = AudioSystem.getClip();
			AudioInputStream ais1 = AudioSystem
					.getAudioInputStream(Main.class.getResourceAsStream(Const.Resources + file));
			clip1.open(ais1);
			clip1.start();
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			new Thread(() -> play(file)).start();
			e.printStackTrace();
		}
	}
	
	public static void play(String filePath)
	{
		final File file = new File(Main.class.getResource(Const.Resources + filePath).getFile());
		
		try (final AudioInputStream in = getAudioInputStream(Main.class.getResource(Const.Resources + filePath)))
		{
			
			final AudioFormat outFormat = getOutFormat(in.getFormat());
			final Info info = new Info(SourceDataLine.class, outFormat);
			
			try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info))
			{
				
				if (line != null)
				{
					line.open(outFormat);
					line.start();
					stream(getAudioInputStream(outFormat, in), line);
					line.drain();
					line.stop();
				}
			}
			
		}
		catch (UnsupportedAudioFileException | LineUnavailableException | IOException e)
		{
			throw new IllegalStateException(e);
		}
	}
	
	private static AudioFormat getOutFormat(AudioFormat inFormat)
	{
		final int ch = inFormat.getChannels();
		
		final float rate = inFormat.getSampleRate();
		return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
	}
	
	private static void stream(AudioInputStream in, SourceDataLine line) throws IOException
	{
		final byte[] buffer = new byte[4096];
		for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length))
		{
			line.write(buffer, 0, n);
		}
	}
}
