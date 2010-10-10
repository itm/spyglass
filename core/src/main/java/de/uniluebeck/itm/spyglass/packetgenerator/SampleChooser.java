/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.packetgenerator;

import java.util.ArrayList;
import java.util.Random;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.uniluebeck.itm.spyglass.packetgenerator.samples.Sample;

/**
 * This is a class selects a random sample based on the probabilities of the samples.
 * 
 * @author dariush
 * 
 */
@Root
public class SampleChooser {

	/**
	 * List of Samples.
	 */
	@ElementList
	private final ArrayList<Sample> samples = new ArrayList<Sample>();

	/**
	 * Return a random sample based on the probabilities given inside the sample objects.
	 * 
	 * @return a sample
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
