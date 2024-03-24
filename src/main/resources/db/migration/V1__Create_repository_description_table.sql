CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE repository_info
(
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    html_url         VARCHAR(255) NOT NULL,
    language         VARCHAR(255) ,
    stargazers_count INTEGER      NOT NULL,
    created_date     DATE         NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_created_date ON repository_info (created_date);
CREATE INDEX IF NOT EXISTS idx_language ON repository_info (language);