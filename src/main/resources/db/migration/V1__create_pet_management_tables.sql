CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    password_reset_token_id VARCHAR(36) NULL,
    active BIT NOT NULL,
    admin VARCHAR(255) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    height DOUBLE NOT NULL,
    weight DOUBLE NOT NULL,
    breed VARCHAR(100) NOT NULL,
    born_in DATETIME(6) NOT NULL,
    owner_id BIGINT NOT NULL, 
    PRIMARY KEY (id),
    INDEX idx_pets_owner (owner_id),
    CONSTRAINT fk_pets_owner FOREIGN KEY (owner_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pet_logs ( 
    id BIGINT NOT NULL AUTO_INCREMENT,
    description VARCHAR(300) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    type VARCHAR(255) NOT NULL,
    pet_id BIGINT NOT NULL, -
    PRIMARY KEY (id),
    INDEX idx_pet_logs_pet_created_at (pet_id, created_at),
    CONSTRAINT fk_pet_logs_pet FOREIGN KEY (pet_id) REFERENCES pets (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS appointments ( 
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(300) NOT NULL,
    date DATETIME(6) NOT NULL,
    pet_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_appointments_pet_date (pet_id, date),
    CONSTRAINT fk_appointments_pet FOREIGN KEY (pet_id) REFERENCES pets (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pet_metrics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    weight DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    measured_at DATETIME(6) NOT NULL,
    pet_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_pet_metrics_pet_measured_at (pet_id, measured_at),
    CONSTRAINT fk_pet_metrics_pet FOREIGN KEY (pet_id) REFERENCES pets (id)
) ENGINE=InnoDB;
