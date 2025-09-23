CREATE TABLE USERS (
                       ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                       USER_NAME VARCHAR2(255) NOT NULL,
                       EMAIL VARCHAR2(255) NOT NULL,
                       PASSWORD VARCHAR2(255) NOT NULL,
                       ROLES VARCHAR2(255) NOT NULL,
                       CREATED_AT TIMESTAMP,
                       UPDATED_AT TIMESTAMP,
                       CREATED_BY VARCHAR2(255),
                       UPDATED_BY VARCHAR2(255)
);

CREATE SEQUENCE users_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Commit changes
COMMIT;