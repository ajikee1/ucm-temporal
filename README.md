# ucm-temporal-Demo-Steps

1. Run temporal on remote server:
```bash
cd /home/customer/temporal/docker_code/docker-compose
docker-compose -f docker-compose-mysql.yml up
```
Temporal running at: http://XX.XX.XX.XX:8088

2. Start Jenkins server on remote host: 
```bash
sudo systemctl start jenkins
```
Jenkins running at: http://XX.XX.XX.XX:8080

3. Clone this git repo to local
```bash
git clone https://github.com/ajikee1/ucm-temporal.git
```

4. JIRA ticket that triggers the Jenkins build: 'https://ajirh.atlassian.net/browse/DIS-1'

5. Run `SpringApp.java` and `UcmWorker.java`
- `SpringApp.java`: Runs the SpringBoot App
- `UcmWorker.java`Runs the Temporal Worker

6. Trigger UcmWorkflow using with POST to `http://localhost:9090/initiateWorkFlow/` with the below request body:

```
{
    "jiraTicketId": "DIS-1",
    "jobList": [
        {
            "jobName": "Jenkins_job_one",
            "buildNumber": "1"
        },
        {
            "jobName": "Jenkins_job_two",
            "buildNumber": "2"
        },
        {
            "jobName": "Jenkins_job_three",
            "buildNumber": "2"
        },
        {
            "jobName": "Jenkins_job_four",
            "buildNumber": "2"
        },
        {
            "jobName": "Jenkins_job_five",
            "buildNumber": "2"
        },
                {
            "jobName": "jenkins_job_six_fail",
            "buildNumber": "5"
        }
    ]
}
```
