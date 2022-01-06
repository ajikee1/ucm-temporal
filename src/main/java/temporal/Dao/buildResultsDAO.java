package temporal.Dao;

public class buildResultsDAO {
    private String workflowId;
    private String runId;
    private String jobId;
    private String buildStatus;

    public buildResultsDAO(String workflowId, String runId, String jobId, String buildStatus){
        this.workflowId = workflowId;
        this.runId = runId;
        this.jobId = jobId;
        this.buildStatus = buildStatus;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getRunId() {
        return runId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getBuildStatus() {
        return buildStatus;
    }
}
