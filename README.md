# Simple Employee Feedback System (SEFS)

## Overview
The **Simple Employee Feedback System (SEFS)** is a backend service designed to facilitate employee feedback submission, review, and response within an organization. Employees can submit feedback anonymously or with identification, and HR/Admin can review, filter, and respond to feedback to ensure a secure and structured feedback process.

## Key Features
- **Authentication & Access Control**: Role-based access allows employees, HR, and Admins to perform different actions.
- **Feedback Submission**: Employees can submit feedback either anonymously or identified, with validations for content and length.
- **Feedback Viewing**: HR/Admin can view all company feedback and mark it as reviewed.
- **Filtering Feedback**: HR/Admin can filter feedback based on date, department, anonymity, and review status.
- **Response Handling**: HR can respond to non-anonymous feedback, view all feedback responses, and update review status.

## Key Components

### 1. Authentication & Access Control
- **Login**: Uses company name, first name, and last name for authentication, and retrieves roles like Admin, HR, Employee, and Manager from the `Employee` table.
- **Access Control**:
  - **All Roles**: Can submit feedback.
  - **HR/Admin**: Can view and filter all feedback for the company.
  - **HR**: Can respond to feedback and manage feedback status.
  - **Employee**: Can view only their own feedback status.

### 2. Feedback Submission
- Employees can submit feedback (anonymous or identified) with department information.
- The feedback is stored in a database, recording the company, department, employee ID, review status, submission time, and anonymity.

### 3. Feedback Viewing
- HR/Admin can view all feedbacks submitted to their company.
- HR can respond to feedback and mark it as reviewed.

### 4. Feedback Filtering
- HR and Admin users can filter feedback using submission date, anonymity, department, and review status.

### 5. Response Handling
- HR can respond to non-anonymous feedback and update its review status.
- Employees can check the review status of their submitted feedback.
- Responses are logged in the database with feedback ID, company ID, responder ID, and timestamps.

## APIs

### 1. **Login API**
- **Method**: `POST`
- **Endpoint**: `/api/v1/employee-feedback/login`
- **Description**: Authenticates the user using company name, first name, and last name. Sets a cookie with user details such as company ID and role.
- **Response**: `HTTP 200` on success, `HTTP 500` for incorrect login details.

### 2. **Submit Feedback API**
- **Method**: `POST`
- **Endpoint**: `/api/v1/employee-feedback/submit-feedback`
- **Description**: Allows employees to submit feedback, anonymously or identified.
- **Response**: `HTTP 200` on success.

### 3. **Get All Feedbacks API**
- **Method**: `GET`
- **Endpoint**: `/api/v1/employee-feedback/get-all-feedbacks`
- **Description**: Retrieves all feedback for the company, accessible by HR/Admin only.
- **Response**: `HTTP 200`, `403 Forbidden` for non-HR/Admin users.

### 4. **Filter Feedback API**
- **Method**: `GET`
- **Endpoint**: `/api/v1/employee-feedback/filter-feedbacks`
- **Description**: Retrieves feedback based on filters like date, department, and anonymity.
- **Response**: `HTTP 200`, `403 Forbidden` for unauthorized users.

### 5. **Respond to Feedback API**
- **Method**: `POST`
- **Endpoint**: `/api/v1/employee-feedback/respond`
- **Description**: Allows HR to respond to feedback.
- **Response**: `HTTP 201` on success, `403 Forbidden` for non-HR or anonymous feedback.

### 6. **Get All Feedback Responses API**
- **Method**: `GET`
- **Endpoint**: `/api/v1/employee-feedback/get-all-feedback-responses/feedback-id/{feedbackId}`
- **Description**: Retrieves all responses to a specific feedback.
- **Response**: `HTTP 200`, `403 Forbidden` for unauthorized users.

### 7. **Get All Company Responses API**
- **Method**: `GET`
- **Endpoint**: `/api/v1/employee-feedback/get-all-responses`
- **Description**: Retrieves all responses for the company.
- **Response**: `HTTP 200`, `403 Forbidden` for non-HR/Admin users.

### 8. **Update Feedback Review Status API**
- **Method**: `PATCH`
- **Endpoint**: `/api/v1/employee-feedback/feedbackId/{feedbackId}`
- **Description**: Marks feedback as reviewed.
- **Response**: `HTTP 200`, `403 Forbidden` for non-HR users.

### 9. **Check Feedback Status API**
- **Method**: `GET`
- **Endpoint**: `/api/v1/employee-feedback/check-status/feedback-id/{feedbackId}`
- **Description**: Retrieves the status of feedback for the logged-in employee.
- **Response**: `HTTP 200`, `403 Forbidden` for unauthorized feedback access.

## Data Models

### Feedback Table
| Column               | Type       | Constraints          |
|----------------------|------------|----------------------|
| id                   | BIGINT     | PRIMARY KEY          |
| company_id           | BIGINT     | NOT NULL             |
| content              | TEXT       |                      |
| is_anonymous         | BOOLEAN    | NOT NULL             |
| status               | BOOLEAN    |                      |
| feedback_provider_id | BIGINT     | NULLABLE             |
| department           | VARCHAR    |                      |
| date                 | TIMESTAMP  | AUTOMATED            |

### Response Table
| Column       | Type       | Constraints          |
|--------------|------------|----------------------|
| id           | BIGINT     | PRIMARY KEY          |
| company_id   | BIGINT     |                      |
| feedback_id  | BIGINT     | NOT NULL             |
| response     | VARCHAR    | NOT NULL             |
| responser_id | BIGINT     | NULLABLE             |
| date         | TIMESTAMP  | AUTOMATED            |

## Installation
1. Clone the repository:  
   `git clone https://github.com/your-repository/sefs.git`
2. Install dependencies:  
   `npm install`
3. Configure environment variables:
   - Add `.env` file for database connection and authentication.
4. Run the service:  
   `npm start`

## Contributing
Contributions are welcome! Please submit a pull request with detailed information about changes.

## License
[MIT License](LICENSE)
