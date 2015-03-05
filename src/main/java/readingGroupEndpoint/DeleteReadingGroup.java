package readingGroupEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import support.Strategy;
import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;

public class DeleteReadingGroup extends Strategy {

	private final String READING_GROUP_WSDL = "http://10.0.0.12:8080/KalibroService/ReadingGroupEndpoint/?wsdl";
	private static WSClient readingGroupClient;
	private int requestsPerStep;
	private Stack<String> idList;
	private int append = 0;
	private int step = 0;
	private List<Integer> errors;

	public DeleteReadingGroup(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		readingGroupClient = new WSClient(READING_GROUP_WSDL);
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveReadingGroup = new ItemImpl("saveReadingGroup");
			Item readingGroup = saveReadingGroup.addChild("readingGroup");
			readingGroup.addChild("id").setContent("");
			readingGroup.addChild("description").setContent("Description");
			readingGroup.addChild("name").setContent("c" + append++);
			idList.push(readingGroupClient.request("saveReadingGroup", saveReadingGroup).getContent("readingGroupId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("deleteReadingGroup", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteReadingGroupResponse"))
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
