package es.ulpgc.titulospropios.gestion.ubay;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectInfoContainer {
	private String name;
	private String course;
	private String semester;
	private String ECTSCredits;
	private String starts;
	private String ends;
	private String kind;
	private String id;
	private String whole_subject;

	public SubjectInfoContainer(HashMap<String, String> input) {
		name = input.get("name");
		course = input.get("course");
		if (input.containsKey("start-date")) {
			if (input.get("start-date") != null
					&& (!input.get("start-date").equals(""))) {
				starts = input.get("start-date");
			} else
				starts = "";
		} else {
			starts = "";
		}
		if (input.containsKey("end-date")) {
			if (input.get("end-date") != null
					&& (!input.get("end-date").equals(""))) {
				ends = input.get("end-date");
			} else
				ends = "";
		} else {
			ends = "";
		}
		semester = input.get("semester");
		ECTSCredits = input.get("ects-credits");
		kind = input.get("kind-id");
		id = input.get("id");
		whole_subject = input.get("whole");
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getCourse() {
		return this.course;
	}

	public String getSemester() {
		return this.semester;
	}

	public String getKind() {
		return this.kind;
	}

	public String getStarts() {
		return this.starts;
	}

	public String getEnds() {
		return this.ends;
	}

	public String getEcts() {
		return this.ECTSCredits;
	}

	public String getWhole() {
		return this.whole_subject;
	}

	public ArrayList<String> getArrayList() {
		ArrayList<String> output = new ArrayList<String>();

		output.add(name);
		output.add(course);
		output.add(semester);
		output.add(starts);
		output.add(ends);
		output.add(kind);
		output.add(ECTSCredits);
		output.add(id);
		output.add(whole_subject);

		return output;
	}
}