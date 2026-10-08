--- program table
CREATE TABLE IF NOT EXISTS program(
    id BIGSERIAL PRIMARY KEY,
    public_id uuid NOT NULL UNIQUE ,
    name VARCHAR(255) NOT NULL UNIQUE ,
    description TEXT ,
    metric_number INTEGER,
    metric_label VARCHAR(255),
    logo_url TEXT,
    status VARCHAR(255) NOT NULL,
    display_order INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX program_name_idx ON program(name);
CREATE INDEX program_public_id_idx ON program(public_id);


--- impact story table
CREATE TABLE IF NOT EXISTS impact_story(
    id BIGSERIAL PRIMARY KEY ,
    public_id uuid NOT NULL UNIQUE ,
    program_id BIGINT NOT NULL REFERENCES program(id),
    beneficiary_name VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL,
    gender VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    image_url TEXT,
    video_url TEXT,
    short_quote TEXT NOT NULL,
    full_story TEXT NOT NULL,
    status VARCHAR(255) NOT NULL,
    remark TEXT,
    display_order INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX impact_story_public_id_idx ON impact_story(public_id);