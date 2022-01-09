package rest;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import temporal.UcmWorkFlow;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Routes {

    @RequestMapping(value = "/initiateWorkFlow/", method = RequestMethod.POST)
    public void initiateWorkFlow(@RequestBody String request) {
        WorkflowServiceStubsOptions wfOptions = WorkflowServiceStubsOptions.newBuilder().setTarget("63.141.224.130:7233").build();
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance(wfOptions);
        WorkflowClient client = WorkflowClient.newInstance(service);

        List<String> jobList = new ArrayList<>();

        try {
            JSONObject requestObj = new JSONObject(request);

            String issueId = requestObj.get("jiraTicketId").toString();

            WorkflowOptions options = WorkflowOptions.newBuilder().setWorkflowId(issueId).setTaskQueue("JENKINS_TASK_QUEUE").build();
            UcmWorkFlow workflow = client.newWorkflowStub(UcmWorkFlow.class, options);

            JSONArray jobsArray = requestObj.getJSONArray("jobList");

            if (jobsArray.length() > 0) {
                for (int i = 0; i < jobsArray.length(); i++) {
                    JSONObject jobsObject = jobsArray.getJSONObject(i);
                    String jobName = jobsObject.getString("jobName");

                    jobList.add(jobName);
                }
            }

            if (!jobList.isEmpty()) {
                WorkflowExecution we = WorkflowClient.start(workflow::initiateWorkFlow, jobList, issueId);
            }

        } catch (Exception e) {
        }


    }
}
