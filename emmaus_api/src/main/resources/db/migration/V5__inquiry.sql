--- create inquiry table
CREATE TABLE IF NOT EXISTS inquiry(
    id BIGSERIAL PRIMARY KEY ,
    public_id uuid NOT NULL UNIQUE ,
    type varchar(255) NOT NULL,
    sender_name varchar(255) NOT NULL,
    sender_email varchar(255) NOT NULL,
    sender_phone varchar(255) ,
    subject varchar(255) NOT NULL,
    message text NOT NULL,
    status varchar(255) NOT NULL DEFAULT 'new',
    remark text,
    created_at timestamptz NOT NULL DEFAULT NOW(),
    updated_at timestamptz NOT NULL DEFAULT NOW()
);

CREATE INDEX inquiry_public_id_idx ON inquiry(public_id);