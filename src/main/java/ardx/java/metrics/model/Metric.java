package ardx.java.metrics.model;

public class Metric {

	private String name;
	private String type;
	private String measure;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMeasure() {
		return measure;
	}
	public void setMeasure(String measure) {
		this.measure = measure;
	}
	public Metric() {
		super();
	}
	
	public Metric(String name, String type, String measure) {
		super();
		this.name = name;
		this.type = type;
		this.measure = measure;
	}
	
}
