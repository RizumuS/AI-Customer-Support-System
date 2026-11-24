USE customer_support_db;

-- =========================================
-- 1. USERS TABLE
-- =========================================

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'AGENT', 'ADMIN') NOT NULL,
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- 2. CUSTOMERS TABLE
-- =========================================

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    address VARCHAR(255),
    company_name VARCHAR(150),

    CONSTRAINT fk_customer_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- =========================================
-- 3. AGENTS TABLE
-- =========================================

CREATE TABLE agents (
    agent_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    department VARCHAR(100),
    availability ENUM('AVAILABLE', 'BUSY', 'OFFLINE')
        DEFAULT 'AVAILABLE',

    CONSTRAINT fk_agent_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

-- =========================================
-- 4. CATEGORIES TABLE
-- =========================================

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- =========================================
-- 5. TICKETS TABLE
-- =========================================

CREATE TABLE tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(30) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    assigned_agent_id INT,
    category_id INT,

    subject VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,

    status ENUM(
        'OPEN',
        'IN_PROGRESS',
        'WAITING_FOR_CUSTOMER',
        'RESOLVED',
        'CLOSED'
    ) DEFAULT 'OPEN',

    priority ENUM(
        'LOW',
        'MEDIUM',
        'HIGH',
        'CRITICAL'
    ) DEFAULT 'MEDIUM',

    source ENUM('WEB', 'DESKTOP') DEFAULT 'WEB',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    resolved_at TIMESTAMP NULL,

    CONSTRAINT fk_ticket_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id),

    CONSTRAINT fk_ticket_agent
        FOREIGN KEY (assigned_agent_id)
        REFERENCES agents(agent_id)
        ON DELETE SET NULL,

    CONSTRAINT fk_ticket_category
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
        ON DELETE SET NULL,

    INDEX idx_ticket_status (status),
    INDEX idx_ticket_priority (priority),
    INDEX idx_ticket_customer (customer_id),
    INDEX idx_ticket_agent (assigned_agent_id),
    INDEX idx_ticket_created (created_at)
);

-- =========================================
-- 6. TICKET RESPONSES TABLE
-- =========================================

CREATE TABLE ticket_responses (
    response_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    sender_user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_internal BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_response_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES tickets(ticket_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_response_user
        FOREIGN KEY (sender_user_id)
        REFERENCES users(user_id),

    INDEX idx_response_ticket (ticket_id)
);

-- =========================================
-- 7. TICKET HISTORY TABLE
-- =========================================

CREATE TABLE ticket_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    changed_by INT NOT NULL,

    field_changed VARCHAR(50) NOT NULL,
    old_value VARCHAR(255),
    new_value VARCHAR(255),

    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_history_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES tickets(ticket_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_history_user
        FOREIGN KEY (changed_by)
        REFERENCES users(user_id),

    INDEX idx_history_ticket (ticket_id)
);

-- =========================================
-- INITIAL TICKET CATEGORIES
-- =========================================

INSERT INTO categories (category_name, description)
VALUES
('Technical Support', 'Technical problems and troubleshooting'),
('Billing', 'Payments, invoices and refunds'),
('Account Issues', 'Login and account-related problems'),
('Product Support', 'Product-related questions and assistance'),
('General Enquiry', 'General customer enquiries'),
('Feature Request', 'Suggestions for new features');

-- =========================================
-- VERIFY TABLE CREATION
-- =========================================

SHOW TABLES;
