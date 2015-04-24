package readingEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import support.RESTStrategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.RSClient;

public class DeleteReading extends RESTStrategy {

	private final String READING_WSDL = "http://10.0.0.12:8080/KalibroService/ReadingEndpoint/?wsdl";
	private static RSClient readingClient;
	private int requestsPerStep;
	private Stack<String> idList;
	private int append = 0;
	private int step = 0;
	private List<Integer> errors;

	public DeleteReading(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		readingClient = new RSClient(READING_WSDL);
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveReading = new ItemImpl("saveReading");
			saveReading.addChild("groupId").setContent("1");
			Item reading = saveReading.addChild("reading");
			reading.addChild("id").setContent("");
			reading.addChild("color").setContent("161212");
			reading.addChild("grade").setContent("5");
			reading.addChild("label").setContent("a" + append++);
			idList.push(readingClient.request("saveReading", saveReading).getContent("readingId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return rsClient.request("deleteReading", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteReadingResponse"))
			step++;
	}

	@Override
	public void afterStep() throws Exception {
		errors.add(step);
		step = 0;
	}

	@Override
	public void afterExperiment() throws Exception {
		int cont = 0;
		for (int error : errors) {
			System.out.println("Number of errors of step " + (cont++) + ": " + error);
		}
	}

}
