---staff profile table
CREATE TABLE IF NOT EXISTS staff_profile (
    id BIGSERIAL PRIMARY KEY,
    public_id uuid NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL ,
    last_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL UNIQUE ,
    email VARCHAR(255) NOT NULL UNIQUE ,
    position VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    description TEXT,
    bio TEXT,
    profile_pic TEXT,
    linkedin_url TEXT,
    display_order INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS staff_profile_public_id_idx ON staff_profile (public_id);

--- resource document table
CREATE TABLE IF NOT EXISTS document(
    id BIGSERIAL PRIMARY KEY ,
    public_id uuid NOT NULL UNIQUE,
    document_type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    url TEXT NOT NULL,
    thumbnail_url TEXT,
    publication_date DATE,
    display_order INTEGER,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS document_public_id_idx ON document (public_id);