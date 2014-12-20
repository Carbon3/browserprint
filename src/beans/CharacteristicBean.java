package beans;

public class CharacteristicBean {
	private String name;
	private double bits;
	private double inX;
	private String value;

	public CharacteristicBean(){
		name = "";
		bits = 0;
		inX = 0;
		value = "";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBits() {
		return bits;
	}

	public void setBits(double bits) {
		this.bits = bits;
	}

	public double getInX() {
		return inX;
	}

	public void setInX(double inX) {
		this.inX = inX;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
