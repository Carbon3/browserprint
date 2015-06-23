package beans;

public class StatisticsBean {
	private int numSamples;

	public StatisticsBean() {
		numSamples = -1;
	}

	public int getNumSamples() {
		return numSamples;
	}

	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}
}
