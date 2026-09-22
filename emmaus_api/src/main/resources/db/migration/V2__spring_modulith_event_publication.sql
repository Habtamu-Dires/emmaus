--- spring modulith event publication
CREATE TABLE IF NOT EXISTS event_publication
(
    id                      UUID NOT NULL,
    event_type              VARCHAR(512) NOT NULL,
    publication_date        TIMESTAMPTZ NOT NULL,
    listener_id             VARCHAR(512) NOT NULL,
    serialized_event        TEXT NOT NULL,
    status                  VARCHAR(20) NOT NULL,
    completion_attempts     INTEGER NOT NULL,
    completion_date         TIMESTAMPTZ,
    last_resubmission_date  TIMESTAMPTZ, -- The missing piece!
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_event_publication_status ON event_publication (status);

CREATE INDEX IF NOT EXISTS idx_event_publication_status ON event_publication (status);