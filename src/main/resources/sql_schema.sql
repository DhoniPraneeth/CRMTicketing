CREATE TABLE agent (
    agent_id BIGINT PRIMARY KEY auto_increment,

    agent_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),

    availability_status BOOLEAN,
    active_ticket_count INT DEFAULT 0,
    id BIGINT
);
CREATE TABLE sla_config (
    sla_id BIGINT PRIMARY KEY AUTO_INCREMENT,

    priority VARCHAR(20) NOT NULL UNIQUE,

    response_time_hours INT NOT NULL,

    resolution_time_hours INT NOT NULL
);

CREATE TABLE ticket (
    ticket_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    descr TEXT,
    category VARCHAR(100),
    priority VARCHAR(20),
    ticket_status VARCHAR(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    due_date TIMESTAMP,
    sla_deadline TIMESTAMP,
    agent_id BIGINT,
    sla_id BIGINT,
    id BIGINT,
    CONSTRAINT fk_ticket_agent
        FOREIGN KEY (agent_id)
            REFERENCES agent(agent_id),

    CONSTRAINT fk_ticket_sla
        FOREIGN KEY (sla_id)
            REFERENCES sla_config(sla_id)
);
CREATE TABLE ticket_comment(
        comment_id BIGINT primary key AUTO_INCREMENT,
        message TEXT,
        ticket_id BIGINT,
        CONSTRAINT fk_ticket_comment
           FOREIGN KEY (ticket_id)
               REFERENCES ticket(ticket_id)
);
CREATE TABLE HISTORY(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    object_type VARCHAR(255),
    object_id VARCHAR(255),
    action VARCHAR(255),
    createdAt TIMESTAMP
);