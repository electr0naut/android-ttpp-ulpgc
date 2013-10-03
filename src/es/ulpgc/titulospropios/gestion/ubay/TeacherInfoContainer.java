package es.ulpgc.titulospropios.gestion.ubay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TeacherInfoContainer {
	@SuppressWarnings("unused")
	private HashMap<String, String> whole;
	private String name;
	private String last_name;
	private String type;
	private String state;
	private String last_notified;
	private String num_notice;
	private String degree;
	private String email;
	private String id;
	private String degree_id;
	private String participation_id;
	private String whole_teacher;

	public static final String ULPGC = "ULPGC";
	public static final String ZERO = "0";
	public static final String[] EN_TAGS = { "Other university",
			"External teacher", "Participation confirmed",
			"Participation confirmation pending", "Never", "Unspecified" };
	public static final String[] ES_TAGS = { "Otra universidad", "Externo",
			"Participación confirmada", "Pendiente de confirmar participación",
			"Nunca", "Sin especificar" };

	public TeacherInfoContainer(HashMap<String, String> input) {
		String locale = Locale.getDefault().getLanguage();
		String[][] tags = { EN_TAGS, ES_TAGS };
		whole = input;
		int locale_index = 0;
		if (locale == "en") {
			locale_index = 0;
		} else {
			locale_index = 1;
		}

		name = input.get("name");
		last_name = input.get("last-name");

		if (input.get("type").equals("ulpgc")) {
			type = ULPGC;
		} else if (input.get("type").equals("other")) {
			type = tags[locale_index][0];
		} else if (input.get("type").equals("exter")) {
			type = tags[locale_index][1];
		}
		if (input.get("aasm-state").equals("confirmada")) {
			state = tags[locale_index][2];
		} else {
			state = tags[locale_index][3];
		}
		if (!(input.get("last-notification-at") == null)) {
			last_notified = input.get("last-notification-at").replace("Z", "")
					.replace("T", " ");
			;
		} else {
			last_notified = tags[locale_index][4];
		}
		if (!(input.get("notifications-count") == null)) {
			num_notice = input.get("notifications-count");
		} else {
			num_notice = ZERO;
		}
		if (!(input.get("degree") == null)) {
			degree = input.get("degree");
		} else {
			degree = tags[locale_index][5];
		}
		email = input.get("email");
		id = input.get("teacher-id");
		degree_id = input.get("university-specific-degree-id");
		participation_id = input.get("id");
		whole_teacher = input.get("whole");
		
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getLastName() {
		return this.last_name;
	}

	public String getType() {
		return this.type;
	}

	public String getState() {
		return this.state;
	}

	public String getLast_notified() {
		return this.last_notified;
	}

	public String getNum_notice() {
		return this.num_notice;
	}

	public String getDegree() {
		return this.degree;
	}

	public String getEmail() {
		return this.email;
	}

	public String getDegreeId() {
		return this.degree_id;
	}

	public String getPartId() {
		return this.participation_id;
	}

	public ArrayList<String> getArrayList() {
		ArrayList<String> output = new ArrayList<String>();

		output.add(name);
		output.add(last_name);
		output.add(type);
		output.add(state);
		output.add(last_notified);
		output.add(num_notice);
		output.add(degree);
		output.add(email);
		output.add(id);
		output.add(degree_id);
		output.add(participation_id);
		output.add(whole_teacher);

		return output;
	}
}