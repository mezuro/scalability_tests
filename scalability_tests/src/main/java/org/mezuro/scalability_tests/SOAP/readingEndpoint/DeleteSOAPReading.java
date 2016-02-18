package org.mezuro.scalability_tests.SOAP.readingEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import eu.choreos.vv.clientgenerator.Item;
import eu.choreos.vv.clientgenerator.ItemImpl;
import eu.choreos.vv.clientgenerator.WSClient;
import org.mezuro.scalability_tests.strategy.SOAPStrategy;

public class DeleteSOAPReading extends SOAPStrategy {

	private final String READING_WSDL = "http://10.0.0.12:8080/KalibroService/SOAPReadingEndpoint/?wsdl";
	private static WSClient readingClient;
	private int requestsPerStep;
	private Stack<String> idList;
	private int append = 0;
	private int step = 0;
	private List<Integer> errors;

	public DeleteSOAPReading(int requestsPerStep) throws Exception {
		this.requestsPerStep = requestsPerStep;
		idList = new Stack<String>();
		errors = new ArrayList<Integer>();
		readingClient = new WSClient(READING_WSDL);
	}

	@Override
	public void beforeStep() throws Exception {
		for (int cont = 0; cont < requestsPerStep; cont++) {
			Item saveSOAPReading = new ItemImpl("saveSOAPReading");
			saveSOAPReading.addChild("groupId").setContent("1");
			Item reading = saveSOAPReading.addChild("reading");
			reading.addChild("id").setContent("");
			reading.addChild("color").setContent("161212");
			reading.addChild("grade").setContent("5");
			reading.addChild("label").setContent("a" + append++);
			idList.push(readingClient.request("saveSOAPReading", saveSOAPReading).getContent("readingId"));
		}
	}

	@Override
	public Item request(Item item) throws Exception {
		return wsClient.request("deleteSOAPReading", idList.pop());
	}

	@Override
	public void afterRequest(Item requestResponse) throws Exception {
		if (! requestResponse.getName().equals("deleteSOAPReadingResponse"))
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
