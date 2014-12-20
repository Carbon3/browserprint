package beans;

import java.util.ArrayList;

public class CharacteristicsBean {
	private ArrayList<CharacteristicBean> characteristics;

	public CharacteristicsBean(){
		characteristics = new ArrayList<CharacteristicBean>();
	}
	
	public ArrayList<CharacteristicBean> getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(ArrayList<CharacteristicBean> characteristics) {
		this.characteristics = characteristics;
	}
}
