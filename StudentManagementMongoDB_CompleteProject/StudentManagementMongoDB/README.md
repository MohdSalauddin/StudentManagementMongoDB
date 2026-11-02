# Student Management System Using MongoDB

A REST API built with Java 17, Spring Boot, Spring Data MongoDB and MongoDB.

## Requirements
- JDK 17+
- Maven 3.9+
- Docker Desktop (recommended for the 3-node replica set)
- Postman or curl
- MongoDB Database Tools (`mongodump` / `mongorestore`) for backup and restore

## Run with a local MongoDB server
1. Start MongoDB locally on port `27017`.
2. Run `mvn spring-boot:run`.
3. API base URL: `http://localhost:8080/api/students`.

For a local standalone MongoDB, set:
`MONGODB_URI=mongodb://localhost:27017/studentdb`

## Run a 3-member replica set with Docker
1. Start Docker Desktop.
2. From the project directory run:
   `docker compose up -d`
3. Wait around 20–40 seconds for the replica set initialization.
4. Check status:
   `docker exec -it studentmanagementmongodb-mongo1-1 mongosh --eval "rs.status()"`
   (The container name can differ; use `docker ps` to confirm.)
5. Connect Spring Boot using this URI:
   `mongodb://localhost:27017,localhost:27018,localhost:27019/studentdb?replicaSet=rs0`
6. Start the app:
   - PowerShell: `$env:MONGODB_URI='mongodb://localhost:27017,localhost:27018,localhost:27019/studentdb?replicaSet=rs0'`
   - macOS/Linux: `export MONGODB_URI='mongodb://localhost:27017,localhost:27018,localhost:27019/studentdb?replicaSet=rs0'`
   - Then run `mvn spring-boot:run`.

Note: The replica set advertises Docker-internal member names (`mongo1`, `mongo2`, `mongo3`). For the most reliable local application connection, run the Spring Boot app inside the same Docker network, or change the replica-set member hostnames to names resolvable from your host. The simplest demonstration is to run the application in Docker Compose on the same network and use `mongodb://mongo1:27017,mongo2:27017,mongo3:27017/studentdb?replicaSet=rs0`. Do not expose this development database publicly.

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/students` | Add a student |
| GET | `/api/students` | Get all students |
| GET | `/api/students/{id}` | Get one student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |
| GET | `/api/students?department=CSE` | Filter by department |
| GET | `/api/students?minPercentage=75` | Filter by minimum percentage |
| GET | `/api/students?department=CSE&minPercentage=75` | Combined filter |

## Sample JSON
```json
{
  "name": "Aman Sharma",
  "email": "aman@example.com",
  "department": "CSE",
  "percentage": 84.5,
  "semester": 5
}
```

## Test with curl
Create:
```bash
curl -X POST http://localhost:8080/api/students -H "Content-Type: application/json" -d '{"name":"Aman Sharma","email":"aman@example.com","department":"CSE","percentage":84.5,"semester":5}'
```
List:
```bash
curl http://localhost:8080/api/students
```
Search:
```bash
curl "http://localhost:8080/api/students?department=CSE&minPercentage=75"
```
Update (replace ID):
```bash
curl -X PUT http://localhost:8080/api/students/ID -H "Content-Type: application/json" -d '{"name":"Aman Sharma","email":"aman@example.com","department":"CSE","percentage":88.0,"semester":6}'
```
Delete (replace ID):
```bash
curl -X DELETE http://localhost:8080/api/students/ID
```

## Indexes and performance
The application creates:
- Unique ascending index on `email` to prevent duplicate email addresses and speed up email lookups.
- Compound index on `{ department: 1, percentage: -1 }` to support department + percentage filtering and percentage ordering.

Verify in `mongosh`:
```javascript
use studentdb
db.students.getIndexes()
db.students.find({ department: "CSE", percentage: { $gte: 75 } }).sort({ percentage: -1 }).explain("executionStats")
```
Use `executionStats` to compare `totalDocsExamined`, `totalKeysExamined`, and execution time before and after an index. Indexes consume disk and memory and slow writes slightly, so create only indexes that support real queries.

## Backup and restore
Install MongoDB Database Tools. Set `MONGODB_URI` to your database URI.
- macOS/Linux: `chmod +x scripts/*.sh`
- Backup: `./scripts/backup.sh`
- Restore: `./scripts/restore.sh backups/backup_YYYYMMDD_HHMMSS`

For Windows PowerShell, run equivalent commands:
```powershell
mongodump --uri="$env:MONGODB_URI" --db=studentdb --out="backups/backup_manual"
mongorestore --uri="$env:MONGODB_URI" --drop "backups/backup_manual/studentdb"
```
Backups should be stored securely outside the database host and tested by performing a restore. For production, use scheduled backups and retention policies.

## Deployment
Build a container:
```bash
docker build -t student-management-api .
```
For deployment to a hosting service, provision MongoDB Atlas, create a database user, allow only the deployment server's outbound IP where possible, and set the environment variable `MONGODB_URI` to the Atlas connection string. Deploy the Docker image or connect the Git repository to a platform that supports Docker. Set the service port to 8080. Never commit database credentials or `.env` files.

## Testing checklist
- Create a valid student.
- Reject invalid email / percentage outside 0–100.
- Get all students and one student by ID.
- Update a record and verify the changed values.
- Delete a record and confirm it is absent.
- Try a duplicate email and confirm the unique index rejects it.
- Test department and percentage filters.
- Verify indexes with `getIndexes()` and query plans with `explain()`.
- Stop the primary replica-set member and confirm another member is elected primary.
- Create a backup, delete a test record, restore the backup, and verify the record returns.

## Important notes
- This is an educational starter project, not a hardened production service.
- Add authentication/authorization, HTTPS, rate limiting, logging, monitoring, and stricter error handling before production use.
- The three-node replica set is for learning high availability; replication is not a substitute for backups.
