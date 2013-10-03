package es.ulpgc.titulospropios.gestion.ubay;

import java.util.ArrayList;
import java.util.HashMap;

public class DegreeInfoContainer {
	private String name;
	private String edition;
	private String state;
	private String code;
	private String type;
	private String id;

	public DegreeInfoContainer(HashMap<String, String> input) {
		name = input.get("name");
		code = input.get("code");
		if (input.containsKey("lectures-start")) {
			if (input.get("lectures-start") != null) {
				edition = input.get("lectures-start");
			} else
				edition = "";
		} else
			edition = "";
		type = input.get("degree-type-id");
		state = input.get("state");
		id = input.get("id");
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getCode() {
		return this.code;
	}

	public String getEdition() {
		return this.edition;
	}

	public String getType() {
		return this.type;
	}

	public String getState() {
		return this.state;
	}

	public ArrayList<String> getArrayList() {
		ArrayList<String> output = new ArrayList<String>();

		output.add(name);
		output.add(type);
		output.add(state);
		output.add(edition);
		output.add(code);
		output.add(id);

		return output;
	}
}
