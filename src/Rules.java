
public class Rules {
	
/*
 * Objekt, v ktorom si ukladam pravidla a vsetky informacie o nich. Nacitavam udaje pomocou konstruktoru.
 */
	private String name;
	private String condition;
	private String result;
	
	public Rules(String name, String condition, String result) {
		this.name = name;
		this.condition = condition;
		this.result = result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
}
