package de.uniluebeck.itm.spyglass.packetgenerator;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.packetgenerator.samples.Sample;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * This is a class selects a random sample based on the probabilities of the samples.
 * 
 * @author dariush
 * 
 */
@Root
public class SampleChooser {
	
	private static Logger log = SpyglassLoggerFactory.getLogger(SampleChooser.class);
	
	/**
	 * List of Samples.
	 */
	@ElementList
	private final ArrayList<Sample> samples = new ArrayList<Sample>();
	
	/**
	 * Return a random sample based on the probabilities given inside the sample objects.
	 * 
	 * @return
	 */
	public Sample getRandomSample() {
		
		// log.debug("Generating new packet.");
		
		/*
		 * Put the probabilities of the samples in an array, for more convenient access.
		 */
		final double[] pr = new double[samples.size()];
		double sum = 0;
		for (int i = 0; i < samples.size(); i++) {
			pr[i] = samples.get(i).getProbability();
			sum += pr[i];
		}

		for (int i = 0; i < samples.size(); i++) {
			pr[i] /= sum;
			if (i > 0) {
				pr[i] = pr[i - 1] + pr[i];
			}
		}
		
		// roll the dice
		final double randomNumber = new Random().nextDouble();
		
		// return the selected sample
		for (int i = 0; i < pr.length; i++) {
			if (randomNumber < pr[i]) {
				return this.samples.get(i);
			}
		}
		
		throw new RuntimeException("This should never happen.");
		// Really. It shouldn't.
		
	}
	
}
