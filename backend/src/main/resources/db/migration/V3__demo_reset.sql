DELETE FROM tag;

INSERT INTO tag (gid, name, color, notes, workspace_gid) VALUES
    (NULL, 'urgent',   'hot-pink', 'Hitni zadaci',       NULL),
    (NULL, 'backend',  'blue',     NULL,                 NULL),
    (NULL, 'frontend', 'magenta',  NULL,                 NULL),
    (NULL, 'školski',  'red',      'Fakultetski zadaci', NULL);
