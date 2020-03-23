package org.javautil.core.text;

import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordFrequency {

	private final Logger                   logger         = LoggerFactory.getLogger(getClass());

	private Set<String>                    excludeWords;

	private final TreeMap<String, Integer> words          = new TreeMap<String, Integer>();

	private boolean                        upperCaseWords = true;

	void incrementWord(final String word) {
		Integer count = words.get(word);
		if (count == null) {
			count = new Integer(1);
		} else {
			count++;
		}
		words.put(word, count);
	}

	void addWord(final String word) {
		String w = null;
		if (word != null) {
			if (upperCaseWords) {
				w = word.toUpperCase();
			} else {
				w = word;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("adding '" + w + "'");
			}
			if (excludeWords == null) {
				incrementWord(w);
			} else if (!excludeWords.contains(w)) {
				incrementWord(w);
			}
		}
	}

	/*
	 * @return the upperCaseWords
	 */
	public boolean isUpperCaseWords() {
		return upperCaseWords;
	}

	/*
	 * @param upperCaseWords the upperCaseWords to set
	 */
	public void setUpperCaseWords(final boolean upperCaseWords) {
		this.upperCaseWords = upperCaseWords;
	}

	/*
	 * @return the excludeWords
	 */
	public Set<String> getExcludeWords() {
		return excludeWords;
	}

	public void setExcludeWords(final Set<String> excludeSet) {
		this.excludeWords = excludeSet;

	}

	/*
	 * Returns the words map. The key is the word. The value is the number of
	 * occurrences.
	 */
	public TreeMap<String, Integer> getWords() {
		return words;
	}

	/*
	 * Returns all of the words in the set sorted.
	 * 
	 * @return
	 */
	public String[] getWordArray() {
		final String[] returnValue = new String[words.size()];
		int i = 0;
		for (final String w : words.keySet()) {
			returnValue[i++] = w;
		}
		return returnValue;
	}

	public int getWordFrequency(final String word) {
		int returnValue = 0;
		String w = word;
		if (word != null && upperCaseWords) {
			w = word.toUpperCase();
		}
		final Integer count = words.get(w);
		if (count != null) {
			returnValue = count;
		}
		return returnValue;
	}
}
