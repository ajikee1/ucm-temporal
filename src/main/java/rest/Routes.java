package rest;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import temporal.UcmWorkFlow;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Routes {

    @RequestMapping(value="/initiateWorkFlow/", method = RequestMethod.POST)
    public void initiate(){
        String issueId = "DIS-1";

        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options = WorkflowOptions.newBuilder().setWorkflowId(issueId).setTaskQueue("JENKINS_TASK_QUEUE").build();
        UcmWorkFlow workflow = client.newWorkflowStub(UcmWorkFlow.class, options);


        List<String> jobList = new ArrayList<>();
        jobList.add("nodeTest");
        jobList.add("temporal_demo");

        WorkflowExecution we = WorkflowClient.start(workflow::initiateWorkFlow, jobList, issueId);
    }
}
