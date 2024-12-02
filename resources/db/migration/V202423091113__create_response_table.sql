CREATE TABLE IF NOT EXISTS response
(
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    feedback_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    responser_id BIGINT,
    time_of_responsing TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_response_company_id ON response (company_id);
CREATE INDEX idx_response_feedback_id ON response (feedback_id);