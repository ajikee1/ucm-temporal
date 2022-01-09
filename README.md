# ucm-temporal

1. Run temporal on remote server:
```bash
cd /home/customer/temporal/docker_code/docker-compose
docker-compose -f docker-compose-mysql.yml up
```
Temporal running at: http://XX.XX.XX.XX:8088

2. Start Jenkins server on remote host: 
```bash
sudo systemctl status jenkins
```
